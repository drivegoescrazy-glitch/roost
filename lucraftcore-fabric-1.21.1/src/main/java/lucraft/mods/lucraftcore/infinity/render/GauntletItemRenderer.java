package lucraft.mods.lucraftcore.infinity.render;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.items.InventoryInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The original 3D gauntlet item renderer: the gauntlet model with the texture
 * of the 1.12 mod in its fingerless variant, worn around the hand while held,
 * and every inserted stone drawn as the same small cube the stones use
 * elsewhere, at the knuckle positions the original renderer defined.
 *
 * <p>The placement is the original one. The 1.12 mod described the item with a
 * {@code builtin/entity} model whose {@code display} block positioned the item
 * cube per camera mode, and its {@code TileEntityItemStackRenderer} then moved
 * the model inside that cube with {@code translate(0.5, 1.8, 0.5)} and
 * {@code scale(-1, -1, -1)}. Both halves are reproduced here unchanged, so the
 * cuff ends up around the player's hand in first and third person exactly as it
 * did in the original mod.</p>
 *
 * <p>In the inventory the item is the original 2D icon instead of the model,
 * drawn flat over the whole slot.</p>
 */
public class GauntletItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    public static final Identifier TEXTURE = LucraftCore.id("textures/models/infinity_gauntlet.png");
    /** The original 1.12 item icon, used for the inventory and only there. */
    public static final Identifier ICON_TEXTURE = LucraftCore.id("textures/item/infinity_gauntlet.png");
    /** Side of the original item icon, in texels. */
    private static final int ICON_SIZE = 16;

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfinityGauntlet))
            return;

        matrices.push();

        if (mode == ModelTransformationMode.GUI) {
            // In the inventory the gauntlet is the original 2D sprite of the mod,
            // not the 3D model.
            renderIcon(stack, matrices, vertexConsumers, light, overlay);
            matrices.pop();
            return;
        }

        GauntletModel.Built model = GauntletModel.create();
        // The gauntlet is worn over the hand: the original ships the version
        // without the separate finger parts (LCConfig.fingersOnGauntlet = false).
        GauntletModel.setFingersVisible(model, false);

        // The body of the original TileEntityItemStackRenderer.
        matrices.translate(0.5D, 1.8D, 0.5D);
        matrices.scale(-1.0F, -1.0F, -1.0F);
        model.root().render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE)),
                light, overlay, 0xFFFFFFFF);

        List<ItemStack> stones = ItemInfinityGauntlet.getStones(stack);
        for (int i = 0; i < stones.size() && i < InventoryInfinityGauntlet.SLOTS.length; i++) {
            ItemStack stone = stones.get(i);
            if (stone.isEmpty())
                continue;
            BuiltinItemRendererRegistry.DynamicItemRenderer renderer =
                    BuiltinItemRendererRegistry.INSTANCE.get(stone.getItem());
            if (renderer == null)
                continue;

            matrices.push();
            translateToStone(matrices, InventoryInfinityGauntlet.SLOTS[i]);
            matrices.scale(0.35F, 0.35F, 0.35F);
            matrices.translate(-0.5D, -0.5D, -0.5D);
            renderer.render(stone, mode, matrices, vertexConsumers, light, overlay);
            matrices.pop();
        }

        matrices.pop();
    }

    /**
     * Draws the original 16x16 item icon as a flat quad filling the item cube,
     * then the pixel each inserted stone adds to it. In 1.12 the icon was the
     * bare gauntlet plus one coloured pixel per stone from
     * {@code textures/items/infinity_gauntlet_<stone>.png}, at the positions
     * those textures store, so the icon shows what the gauntlet holds.
     *
     * <p>The item cube is the 0..1 box the GUI hands to builtin renderers, and
     * the GUI matrix flips Y, so both the sprite rows and the stone pixels are
     * mirrored vertically.</p>
     */
    private static void renderIcon(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   int light, int overlay) {
        drawIconQuad(matrices, vertexConsumers, light, overlay, ICON_TEXTURE,
                0, 0, ICON_SIZE, 0.5F);

        for (EnumInfinityStone type : InventoryInfinityGauntlet.SLOTS) {
            if (!ItemInfinityGauntlet.hasStone(stack, type))
                continue;
            int[] marker = STONE_MARKERS.get(type);
            drawIconQuad(matrices, vertexConsumers, light, overlay, stoneIcon(type),
                    marker[0], marker[1], 1, 0.52F);
        }
    }

    /** The 1x1 icon pixel each stone adds, as {@code {x, y}} in the 16x16 icon. */
    private static final Map<EnumInfinityStone, int[]> STONE_MARKERS = Map.of(
            EnumInfinityStone.SOUL, new int[]{4, 7},
            EnumInfinityStone.REALITY, new int[]{6, 7},
            EnumInfinityStone.SPACE, new int[]{8, 7},
            EnumInfinityStone.POWER, new int[]{10, 7},
            EnumInfinityStone.MIND, new int[]{7, 10},
            EnumInfinityStone.TIME, new int[]{12, 8});

    /** The original per-stone icon overlay of the 1.12 jar. */
    private static Identifier stoneIcon(EnumInfinityStone type) {
        return LucraftCore.id("textures/item/infinity_gauntlet_" + type.name().toLowerCase(Locale.ROOT) + ".png");
    }

    /**
     * Draws one {@code size x size} texel block of the icon sheet placed over
     * the matching area of the item cube.
     */
    private static void drawIconQuad(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                     int light, int overlay, Identifier texture,
                                     int texelX, int texelY, int size, float z) {
        float x0 = texelX / (float) ICON_SIZE;
        float x1 = (texelX + size) / (float) ICON_SIZE;
        float y0 = 1.0F - (texelY + size) / (float) ICON_SIZE;
        float y1 = 1.0F - texelY / (float) ICON_SIZE;
        float u0 = x0;
        float u1 = x1;
        float v0 = texelY / (float) ICON_SIZE;
        float v1 = (texelY + size) / (float) ICON_SIZE;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
        MatrixStack.Entry entry = matrices.peek();
        iconVertex(buffer, entry, light, overlay, x0, y0, z, u0, v1);
        iconVertex(buffer, entry, light, overlay, x1, y0, z, u1, v1);
        iconVertex(buffer, entry, light, overlay, x1, y1, z, u1, v0);
        iconVertex(buffer, entry, light, overlay, x0, y1, z, u0, v0);
    }

    private static void iconVertex(VertexConsumer buffer, MatrixStack.Entry entry, int light, int overlay,
                                   float x, float y, float z, float u, float v) {
        buffer.vertex(entry.getPositionMatrix(), x, y, z)
                .color(0xFFFFFFFF)
                .texture(u, v)
                .overlay(overlay)
                .light(light)
                .normal(entry, 0.0F, 0.0F, 1.0F);
    }

    /** The original per-stone placement on the gauntlet. */
    private static void translateToStone(MatrixStack matrices, EnumInfinityStone type) {
        switch (type) {
            case SOUL -> matrices.translate(0.119D, 1.07D, -0.14D);
            case REALITY -> matrices.translate(0.044D, 1.07D, -0.14D);
            case SPACE -> matrices.translate(-0.0375D, 1.07D, -0.14D);
            case POWER -> matrices.translate(-0.118D, 1.07D, -0.14D);
            case MIND -> matrices.translate(0.006D, 1.164D, -0.14D);
            case TIME -> {
                matrices.translate(-0.14D, 1.095D, 0.062D);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            }
        }
    }
}
