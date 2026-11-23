package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.util.network.EnergyBeamPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The energy blast bolt. The 1.12 mod fired an {@code EntityEnergyBlast} — a
 * throwable moving at 1.5 blocks per tick whose renderer drew a glowing line
 * one block long along its motion with ten additive glow layers and a white
 * core ({@code LCRenderHelper.drawGlowingLine}). The beam is drawn the same way
 * here: the shot travels from the player's eyes to the point the server's
 * hitscan reached, then disappears.
 *
 * <p>The list is written from the client thread only (the payload handler
 * schedules onto it), and the renderer is exception safe: the matrix stack is
 * always popped, so a bad frame can never leave the game's renderer wedged on
 * the main menu.</p>
 */
public final class EnergyBeams {

    private static final Identifier WHITE_TEXTURE = LucraftCore.id("textures/models/white.png");

    /** The original throwable's throw velocity. */
    private static final double SPEED = 1.5D;
    /** Length of the glowing line the original renderer drew. */
    private static final double BOLT_LENGTH = 1.0D;
    /** Half width of the white core, in blocks. */
    private static final double CORE_HALF_WIDTH = 0.045D;
    /** Glow layers: half width multiplier and alpha, outermost first. */
    private static final float[][] LAYERS = {
            {3.0F, 0.10F},
            {2.0F, 0.16F},
            {1.3F, 0.26F},
            {0.7F, 1.0F}
    };

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

    private static final List<Beam> BEAMS = new CopyOnWriteArrayList<>();

    private EnergyBeams() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Beam beam : BEAMS) {
                beam.travelled += SPEED;
                if (beam.finished())
                    BEAMS.remove(beam);
            }
        });
        WorldRenderEvents.AFTER_TRANSLUCENT.register(EnergyBeams::render);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> BEAMS.clear());
    }

    /** Adds a shot that the server resolved. Must be called on the client thread. */
    public static void add(EnergyBeamPayload payload) {
        Vec3d from = payload.from();
        Vec3d to = payload.to();
        if (!isFinite(from) || !isFinite(to))
            return;
        Vec3d delta = to.subtract(from);
        double length = delta.length();
        if (!(length > 1.0E-4D) || !Double.isFinite(length))
            return;
        BEAMS.add(new Beam(from, delta.normalize(), length, payload.color()));
    }

    public static void clear() {
        BEAMS.clear();
    }

    private static void render(WorldRenderContext context) {
        if (BEAMS.isEmpty())
            return;
        MatrixStack matrices = context.matrixStack();
        if (matrices == null || context.consumers() == null || context.camera() == null)
            return;

        Vec3d camera = context.camera().getPos();
        matrices.push();
        try {
            matrices.translate(-camera.x, -camera.y, -camera.z);
            MatrixStack.Entry entry = matrices.peek();
            VertexConsumer buffer = context.consumers().getBuffer(RenderLayer.getEntityTranslucentEmissive(WHITE_TEXTURE));

            for (Beam beam : BEAMS) {
                // The bolt is a segment trailing the travelling head, clipped at
                // the muzzle so the shot grows out of the player's hand.
                double head = Math.min(beam.travelled, beam.length);
                double tail = Math.max(0.0D, head - BOLT_LENGTH);
                if (head <= tail)
                    continue;
                Vec3d start = beam.from.add(beam.direction.multiply(tail));
                Vec3d end = beam.from.add(beam.direction.multiply(head));

                Vec3d axis = end.subtract(start);
                if (!(axis.lengthSquared() > 1.0E-6D))
                    continue;
                axis = axis.normalize();
                // A single ribbon turned to face the camera. Two crossed ribbons
                // would be edge-on (and so invisible) from most angles and show
                // as an X where they overlap, so the bolt is one billboarded quad.
                Vec3d across = perpendicular(axis, camera.subtract(start));

                float red = ((beam.color >> 16) & 0xFF) / 255.0F;
                float green = ((beam.color >> 8) & 0xFF) / 255.0F;
                float blue = (beam.color & 0xFF) / 255.0F;

                for (int layer = 0; layer < LAYERS.length; layer++) {
                    boolean core = layer == LAYERS.length - 1;
                    float r = core ? 1.0F : red;
                    float g = core ? 1.0F : green;
                    float b = core ? 1.0F : blue;
                    float a = LAYERS[layer][1] * (core ? 1.0F : 0.55F);
                    double halfWidth = CORE_HALF_WIDTH * LAYERS[layer][0];
                    quad(buffer, entry, start, end, across, halfWidth, r, g, b, a);
                }
            }
        } catch (Exception e) {
            // Never let a bad shot take the frame (and the client) down.
            lucraft.mods.lucraftcore.LucraftCore.reportError("energy beam render", e);
        } finally {
            matrices.pop();
        }
    }

    private static boolean isFinite(Vec3d vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }

    /** A unit vector perpendicular to {@code axis}, bent towards {@code hint}. */
    private static Vec3d perpendicular(Vec3d axis, Vec3d hint) {
        Vec3d side = axis.crossProduct(hint);
        if (side.lengthSquared() < 1.0E-6D) {
            // Looking straight down the beam: any perpendicular will do.
            side = axis.crossProduct(Math.abs(axis.y) > 0.9D
                    ? new Vec3d(1.0D, 0.0D, 0.0D) : new Vec3d(0.0D, 1.0D, 0.0D));
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
        // Emitted with both windings: the beam has to be visible from either side.
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
        buffer.vertex(entry, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, a)
                .texture(0.5F, 0.5F)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0.0F, 1.0F, 0.0F);
    }
}
