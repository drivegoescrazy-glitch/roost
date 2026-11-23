package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.util.network.EnergyBeamPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The energy blast bolt. The 1.12 mod fired an {@code EntityEnergyBlast} — a
 * throwable moving at 1.5 blocks per tick whose renderer drew a glowing line
 * one block long along its motion with ten additive glow layers and a white
 * core ({@code LCRenderHelper.drawGlowingLine}). The beam is drawn the same way
 * here: the shot travels from the player's eyes to the point the server's
 * hitscan reached, then disappears.
 */
public final class EnergyBeams {

    /** The original throwable's throw velocity. */
    private static final double SPEED = 1.5D;
    /** Length of the glowing line the original renderer drew. */
    private static final double BOLT_LENGTH = 1.0D;
    /** Glow layers around the core, as in the original. */
    private static final int LAYERS = 10;

    /** One shot in flight. */
    private static final class Beam {
        private final Vec3d from;
        private final Vec3d direction;
        private final double length;
        private final int color;
        private double travelled;

        private Beam(Vec3d from, Vec3d direction, double length, int color) {
            this.from = from;
            this.direction = direction;
            this.length = length;
            this.color = color;
        }

        private boolean finished() {
            return this.travelled > this.length + BOLT_LENGTH;
        }
    }

    private static final List<Beam> BEAMS = new ArrayList<>();

    private EnergyBeams() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Iterator<Beam> iterator = BEAMS.iterator();
            while (iterator.hasNext()) {
                Beam beam = iterator.next();
                beam.travelled += SPEED;
                if (beam.finished())
                    iterator.remove();
            }
        });
        WorldRenderEvents.AFTER_TRANSLUCENT.register(EnergyBeams::render);
    }

    /** Adds a shot that the server resolved. */
    public static void add(EnergyBeamPayload payload) {
        Vec3d from = payload.from();
        Vec3d delta = payload.to().subtract(from);
        double length = delta.length();
        if (length < 1.0E-4D)
            return;
        BEAMS.add(new Beam(from, delta.normalize(), length, payload.color()));
    }

    public static void clear() {
        BEAMS.clear();
    }

    private static void render(WorldRenderContext context) {
        if (BEAMS.isEmpty() || context.matrixStack() == null || context.consumers() == null)
            return;

        Vec3d camera = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer buffer = context.consumers().getBuffer(RenderLayer.getLightning());

        for (Beam beam : BEAMS) {
            // The bolt is a segment trailing the travelling head, clipped at the
            // muzzle so the shot grows out of the player's hand.
            double head = beam.travelled;
            double tail = Math.max(0.0D, head - BOLT_LENGTH);
            if (head <= tail)
                continue;
            Vec3d start = beam.from.add(beam.direction.multiply(tail));
            Vec3d end = beam.from.add(beam.direction.multiply(Math.min(head, beam.length)));

            Vec3d axis = end.subtract(start);
            if (axis.lengthSquared() < 1.0E-6D)
                continue;
            axis = axis.normalize();
            // Two perpendicular ribbons make the bolt visible from any angle,
            // the way the original's four-sided glowing line was.
            Vec3d across = perpendicular(axis, camera.subtract(start));
            Vec3d other = axis.crossProduct(across).normalize();

            float red = ((beam.color >> 16) & 0xFF) / 255.0F;
            float green = ((beam.color >> 8) & 0xFF) / 255.0F;
            float blue = (beam.color & 0xFF) / 255.0F;

            for (int layer = 0; layer <= LAYERS; layer++) {
                boolean core = layer == LAYERS;
                float r = core ? 1.0F : red;
                float g = core ? 1.0F : green;
                float b = core ? 1.0F : blue;
                float a = core ? 1.0F : 1.0F / (LAYERS * 2.0F);
                double size = 0.5D + (core ? 0.0D : layer * (1.25D / LAYERS));
                double halfWidth = 0.0625D * size;
                quad(buffer, entry, start, end, across, halfWidth, r, g, b, a);
                quad(buffer, entry, start, end, other, halfWidth, r, g, b, a);
            }
        }

        matrices.pop();
    }

    /** A unit vector perpendicular to {@code axis}, bent towards {@code hint}. */
    private static Vec3d perpendicular(Vec3d axis, Vec3d hint) {
        Vec3d side = axis.crossProduct(hint);
        if (side.lengthSquared() < 1.0E-6D) {
            // Looking straight down the beam: any perpendicular will do.
            side = axis.crossProduct(Math.abs(axis.y) > 0.9D ? new Vec3d(1.0D, 0.0D, 0.0D) : new Vec3d(0.0D, 1.0D, 0.0D));
        }
        return side.normalize();
    }

    private static void quad(VertexConsumer buffer, MatrixStack.Entry entry, Vec3d start, Vec3d end,
                             Vec3d across, double halfWidth, float r, float g, float b, float a) {
        Vec3d offset = across.multiply(halfWidth);
        Vec3d a0 = start.subtract(offset);
        Vec3d a1 = start.add(offset);
        Vec3d b0 = end.subtract(offset);
        Vec3d b1 = end.add(offset);
        // Emitted with both windings: the lightning layer keeps back-face
        // culling on, and a beam has to be visible from either side.
        vertex(buffer, entry, a0, r, g, b, a);
        vertex(buffer, entry, b0, r, g, b, a);
        vertex(buffer, entry, b1, r, g, b, a);
        vertex(buffer, entry, a1, r, g, b, a);
        vertex(buffer, entry, a1, r, g, b, a);
        vertex(buffer, entry, b1, r, g, b, a);
        vertex(buffer, entry, b0, r, g, b, a);
        vertex(buffer, entry, a0, r, g, b, a);
    }

    private static void vertex(VertexConsumer buffer, MatrixStack.Entry entry, Vec3d pos,
                               float r, float g, float b, float a) {
        buffer.vertex(entry, (float) pos.x, (float) pos.y, (float) pos.z).color(r, g, b, a);
    }
}
