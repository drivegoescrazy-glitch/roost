package lucraft.mods.lucraftcore.infinity.render;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.items.InventoryInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

/**
 * The original 3D gauntlet item renderer: the gauntlet model with the texture
 * of the 1.12 mod, closed into a fist while held, and every inserted stone
 * drawn as the same small cube the stones use elsewhere, at the knuckle
 * positions the original renderer defined.
 */
public class GauntletItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    public static final Identifier TEXTURE = LucraftCore.id("textures/models/infinity_gauntlet.png");

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfinityGauntlet))
            return;

        GauntletModel.Built model = GauntletModel.create();
        // A gauntlet lying on the ground shows an open hand; a held one is a fist.
        GauntletModel.setFist(model, mode != ModelTransformationMode.GROUND
                && mode != ModelTransformationMode.FIXED
                && mode != ModelTransformationMode.GUI);

        matrices.push();
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
