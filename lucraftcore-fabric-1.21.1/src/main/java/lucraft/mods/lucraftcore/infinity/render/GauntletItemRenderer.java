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
 * of the 1.12 mod in its fingerless variant, worn around the hand while held,
 * and every inserted stone drawn as the same small cube the stones use
 * elsewhere, at the knuckle positions the original renderer defined.
 *
 * <p>The 1.12 mod put the 3D model behind a {@code builtin/entity} model whose
 * {@code display} block described the old {@code TileEntityItemStackRenderer}
 * pipeline, where it stacked on top of the renderer's own translate/scale.
 * Those numbers do not carry over to the 1.21 item pipeline as-is, so the
 * gauntlet is placed here, per camera mode, in the item's own frame: the model
 * is turned so the knuckle stones face the viewer ({@code +Z}), the wrist
 * points down ({@code -Y}) and the fist is hand sized, which is how the
 * gauntlet read in the original mod.</p>
 */
public class GauntletItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    public static final Identifier TEXTURE = LucraftCore.id("textures/models/infinity_gauntlet.png");

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfinityGauntlet))
            return;

        GauntletModel.Built model = GauntletModel.create();
        // The gauntlet is worn over the hand: the original ships the version
        // without the separate finger parts (LCConfig.fingersOnGauntlet = false).
        GauntletModel.setFingersVisible(model, false);

        matrices.push();
        // 1.21 hands builtin item renderers a stack that has already been moved
        // into the 0..1 item cube; undo that so the gauntlet can be placed around
        // the item's own origin.
        matrices.translate(0.5D, 0.5D, 0.5D);
        applyMode(matrices, mode);
        matrices.scale(-1.0F, -1.0F, -1.0F);
        // The model's own coordinates run from y = 0.875 (wrist end) to
        // y = 1.5 (knuckles). While worn, the wrist cuff is what the hand goes
        // into, so the cuff straddles the item origin; everywhere else the whole
        // gauntlet is centred on it.
        double yOffset = isHand(mode) ? -1.375D : -1.19D;
        matrices.translate(0.0D, yOffset, 0.0D);
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
     * Places the gauntlet for a camera mode. The model frame handed to this
     * method has the knuckle stones towards {@code +Z}, the fingers towards
     * {@code +Y} and the wrist towards {@code -Y}; every mode only tilts,
     * mirrors and sizes it from there.
     */
    private static void applyMode(MatrixStack matrices, ModelTransformationMode mode) {
        switch (mode) {
            case GUI -> {
                matrices.scale(0.90F, 0.90F, 0.90F);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(18.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-25.0F));
                matrices.translate(0.0D, -0.02D, 0.0D);
            }
            case FIXED -> {
                matrices.scale(0.70F, 0.70F, 0.70F);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-20.0F));
            }
            case GROUND -> {
                matrices.scale(0.55F, 0.55F, 0.55F);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25.0F));
                matrices.translate(0.0D, -0.15D, 0.0D);
            }
            case HEAD -> {
                matrices.scale(0.50F, 0.50F, 0.50F);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
                matrices.translate(0.0D, 0.10D, 0.0D);
            }
            case FIRST_PERSON_RIGHT_HAND -> {
                matrices.translate(0.06D, -0.02D, 0.02D);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-15.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-22.0F));
                matrices.scale(0.62F, 0.62F, 0.62F);
            }
            case FIRST_PERSON_LEFT_HAND -> {
                matrices.translate(-0.06D, -0.02D, 0.02D);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-15.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(22.0F));
                matrices.scale(0.62F, 0.62F, 0.62F);
            }
            case THIRD_PERSON_RIGHT_HAND -> {
                matrices.translate(0.0D, -0.02D, 0.0D);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-28.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-35.0F));
                matrices.scale(0.60F, 0.60F, 0.60F);
            }
            case THIRD_PERSON_LEFT_HAND -> {
                matrices.translate(0.0D, -0.02D, 0.0D);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-28.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(35.0F));
                matrices.scale(0.60F, 0.60F, 0.60F);
            }
        }
    }

    private static boolean isHand(ModelTransformationMode mode) {
        return mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND
                || mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND
                || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
                || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
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
