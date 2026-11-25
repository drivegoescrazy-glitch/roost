package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.util.network.EnergyBeamPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
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
 * one block long along its motion. That renderer used
 * {@code LCRenderHelper.drawGlowingLine}: ten to thirty square coloured glow
 * prisms with additive alpha, grown from half a block to 1.75 blocks wide, and
 * a solid white core. This reproduces the geometry and layer count rather than
 * drawing one billboard with a handful of alpha passes.
 *
 * <p>The beam is drawn as the server's hitscan travels: a one-block segment
 * follows the projectile head from the muzzle to the hit point. The list is
 * written from the client thread only and the matrix stack is always popped.
 */
public final class EnergyBeams {

    private static final Identifier WHITE_TEXTURE = LucraftCore.id("textures/models/white.png");

    /** The original throwable's throw velocity. */
    private static final double SPEED = 1.5D;
    /** Length of the glowing line the original renderer drew. */
    private static final double BOLT_LENGTH = 1.0D;
    /** Original {@code thickness} argument. */
    private static final double BASE_SIZE = 0.5D;
    /** Original layer growth: {@code layer * 1.25 / layers}. */
    private static final double SIZE_STEP = 1.25D;
    /** Original {@code 0.0625 * size} half-width. */
    private static final double HALF_WIDTH_SCALE = 0.0625D;
    /** How far each glow layer extends past the bright core, in blocks. */
    private static final double END_OVERHANG = 0.1D;

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
        ClientTickEvents.END_CLIENT_TICK.register(client -> LucraftCore.guard("energy beam tick", () -> {
            for (Beam beam : BEAMS) {
                beam.travelled += SPEED;
                if (beam.finished())
                    BEAMS.remove(beam);
            }
        }));
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
            // entity_translucent is untextured-looking with a white texture and,
            // unlike the emissive layer, has no second sampler to go missing.
            VertexConsumer buffer = context.consumers().getBuffer(RenderLayer.getEntityTranslucent(WHITE_TEXTURE));

            // The original used 10 layers on fast graphics and 30 on fancy.
            int fancy = MinecraftClient.getInstance().options.getGraphicsMode().getValue() == GraphicsMode.FAST ? 0 : 1;
            int layers = 10 + fancy * 20;

            for (Beam beam : BEAMS) {
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

                Vec3d up = stableUp(axis);
                Vec3d side = axis.crossProduct(up).normalize();
                up = side.crossProduct(axis).normalize();

                float red = ((beam.color >> 16) & 0xFF) / 255.0F;
                float green = ((beam.color >> 8) & 0xFF) / 255.0F;
                float blue = (beam.color & 0xFF) / 255.0F;

                for (int layer = 0; layer < layers; layer++) {
                    double size = BASE_SIZE + layer * (SIZE_STEP / layers);
                    double halfWidth = HALF_WIDTH_SCALE * size;
                    // Original alpha was 1 / layers / 2 per glow layer. Keep a
                    // little more on fancy so thirty layers still read as a glow
                    // with normal alpha blending instead of additive blending.
                    float alpha = Math.max(0.03F, 1.0F / layers / 2.0F);
                    double overhang = (1.0D - (double) layer / layers) * END_OVERHANG;
                    Vec3d layerStart = start.subtract(axis.multiply(overhang));
                    Vec3d layerEnd = end.add(axis.multiply(overhang));
                    prism(buffer, entry, layerStart, layerEnd, side, up, halfWidth,
                            red, green, blue, alpha);
                }

                // The original last pass is an opaque white core at the base
                // thickness, drawn after every coloured glow layer.
                prism(buffer, entry, start, end, side, up,
                        HALF_WIDTH_SCALE * BASE_SIZE, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        } catch (Throwable e) {
            // Never let a bad shot take the frame (and the client) down.
            LucraftCore.reportError("energy beam render", e);
        } finally {
            matrices.pop();
        }
    }

    private static boolean isFinite(Vec3d vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }

    /** A unit vector perpendicular to the beam, with a stable fallback. */
    private static Vec3d stableUp(Vec3d axis) {
        Vec3d up = Math.abs(axis.y) > 0.9D ? new Vec3d(1.0D, 0.0D, 0.0D) : new Vec3d(0.0D, 1.0D, 0.0D);
        up = up.subtract(axis.multiply(axis.dotProduct(up)));
        if (up.lengthSquared() < 1.0E-6D)
            up = new Vec3d(0.0D, 0.0D, 1.0D).subtract(axis.multiply(axis.z));
        return up.normalize();
    }

    /**
     * A square tube around the segment: four long faces, no end caps, exactly
     * like the four side faces the 1.12 helper emitted for each glow layer.
     * Each face is sent with both windings because the beam is viewed from
     * every angle and entity_translucent culls back faces.
     */
    private static void prism(VertexConsumer buffer, MatrixStack.Entry entry,
                              Vec3d start, Vec3d end, Vec3d side, Vec3d up, double halfWidth,
                              float r, float g, float b, float a) {
        Vec3d s = side.multiply(halfWidth);
        Vec3d u = up.multiply(halfWidth);

        // Corner order: +u face, -u face, +s face, -s face. The corners are at
        // start/end plus or minus each half-width; a face through the axis
        // (the previous code) is just a cross, not a tube.
        face(buffer, entry, start.add(u).add(s), start.add(u).subtract(s),
                end.add(u).subtract(s), end.add(u).add(s), r, g, b, a);
        face(buffer, entry, start.subtract(u).add(s), start.subtract(u).subtract(s),
                end.subtract(u).subtract(s), end.subtract(u).add(s), r, g, b, a);
        face(buffer, entry, start.add(s).add(u), start.add(s).subtract(u),
                end.add(s).subtract(u), end.add(s).add(u), r, g, b, a);
        face(buffer, entry, start.subtract(s).add(u), start.subtract(s).subtract(u),
                end.subtract(s).subtract(u), end.subtract(s).add(u), r, g, b, a);
    }

    /** One quad, sent once for each side so the tube is visible from outside. */
    private static void face(VertexConsumer buffer, MatrixStack.Entry entry,
                             Vec3d a, Vec3d b, Vec3d c, Vec3d d,
                             float r, float g, float bl, float alpha) {
        quad(buffer, entry, a, b, c, d, r, g, bl, alpha);
        quad(buffer, entry, d, c, b, a, r, g, bl, alpha);
    }

    private static void quad(VertexConsumer buffer, MatrixStack.Entry entry,
                             Vec3d a, Vec3d b, Vec3d c, Vec3d d,
                             float r, float g, float bl, float alpha) {
        vertex(buffer, entry, a, r, g, bl, alpha);
        vertex(buffer, entry, b, r, g, bl, alpha);
        vertex(buffer, entry, c, r, g, bl, alpha);
        vertex(buffer, entry, d, r, g, bl, alpha);
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
