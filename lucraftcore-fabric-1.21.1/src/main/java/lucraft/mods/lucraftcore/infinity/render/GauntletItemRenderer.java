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

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfinityGauntlet))
            return;

        matrices.push();

        if (mode == ModelTransformationMode.GUI) {
            // In the inventory the gauntlet is the original 2D sprite of the mod,
            // not the 3D model.
            renderIcon(matrices, vertexConsumers, light, overlay);
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
     * Draws the original 16x16 item icon as a flat quad filling the item cube.
     * The item cube is the 0..1 box the GUI hands to builtin renderers, and the
     * GUI matrix flips Y, so the texture rows are reversed to keep the sprite
     * upright.
     */
    private static void renderIcon(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   int light, int overlay) {
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(ICON_TEXTURE));
        float z = 0.5F;
        iconVertex(buffer, entry, light, overlay, 0.0F, 0.0F, z, 0.0F, 1.0F);
        iconVertex(buffer, entry, light, overlay, 1.0F, 0.0F, z, 1.0F, 1.0F);
        iconVertex(buffer, entry, light, overlay, 1.0F, 1.0F, z, 1.0F, 0.0F);
        iconVertex(buffer, entry, light, overlay, 0.0F, 1.0F, z, 0.0F, 0.0F);
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
