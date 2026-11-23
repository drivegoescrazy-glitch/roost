package lucraft.mods.lucraftcore.infinity.render;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;

/**
 * The original {@code ModelInfinityGauntlet}: a Techne-style model of a hand
 * wearing the gauntlet, with six stone blocks on the knuckles and the two-part
 * fingers that close when the gauntlet is held.
 *
 * <p>Every box, pivot, rotation point and UV is taken from the 1.12 model, so
 * the 64x16 {@code models/infinity_gauntlet.png} maps identically.</p>
 */
public final class GauntletModel {

    private static final float FINGER_ANGLE = -0.34906584F;
    private static final float THUMB_ANGLE = -0.17453292F;

    private GauntletModel() {
    }

    /** A built model plus the parts the renderer needs to pose. */
    public record Built(ModelPart root, ModelPart index, ModelPart middle, ModelPart ring,
                        ModelPart pinkie, ModelPart thumb, ModelPart indexTip, ModelPart middleTip,
                        ModelPart ringTip, ModelPart pinkieTip, ModelPart thumbTip) {
    }

    public static Built create() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData shape1 = root.addChild("shape1",
                ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, 0.0F, -3.0F, 6, 4, 6),
                ModelTransform.pivot(0.0F, 20.0F, 0.0F));

        root.addChild("hand",
                ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 0.0F, -2.5F, 5, 4, 5),
                ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        ModelPartData index = root.addChild("index_0",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                ModelTransform.pivot(-2.0F, 16.0F, -2.0F));
        ModelPartData indexTip = index.addChild("index_1",
                ModelPartBuilder.create().uv(43, 2).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                ModelTransform.of(0.0F, -2.0F, 0.0F, FINGER_ANGLE, 0.0F, 0.0F));

        ModelPartData middle = root.addChild("middle_0",
                ModelPartBuilder.create().uv(18, 0).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                ModelTransform.pivot(-0.6F, 16.0F, -2.0F));
        ModelPartData middleTip = middle.addChild("middle_1",
                ModelPartBuilder.create().uv(47, 2).cuboid(-0.5F, -3.0F, -0.5F, 1, 3, 1),
                ModelTransform.of(0.0F, -2.0F, 0.0F, FINGER_ANGLE, 0.0F, 0.0F));

        ModelPartData ring = root.addChild("ring_0",
                ModelPartBuilder.create().uv(22, 0).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                ModelTransform.pivot(0.7F, 16.0F, -2.0F));
        ModelPartData ringTip = ring.addChild("ring_1",
                ModelPartBuilder.create().uv(51, 2).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                ModelTransform.of(0.0F, -2.0F, 0.0F, FINGER_ANGLE, 0.0F, 0.0F));

        ModelPartData pinkie = root.addChild("pinkie_0",
                ModelPartBuilder.create().uv(39, 0).cuboid(-0.5F, -1.0F, -0.5F, 1, 1, 1),
                ModelTransform.pivot(2.0F, 16.0F, -2.0F));
        ModelPartData pinkieTip = pinkie.addChild("pinkie_1",
                ModelPartBuilder.create().uv(55, 2).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                ModelTransform.of(0.0F, -1.0F, 0.0F, FINGER_ANGLE, 0.0F, 0.0F));

        ModelPartData thumb = root.addChild("thumb_0",
                ModelPartBuilder.create().uv(43, 0).cuboid(-1.0F, -1.0F, -0.5F, 1, 1, 1),
                ModelTransform.of(-2.0F, 16.0F, 1.0F, 0.0F, 0.0F, THUMB_ANGLE));
        ModelPartData thumbTip = thumb.addChild("thumb_1",
                ModelPartBuilder.create().uv(59, 2).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                ModelTransform.of(-0.5F, -1.0F, 0.0F, FINGER_ANGLE, 0.0F, 0.0F));

        root.addChild("stone_0", stone(47, 0), ModelTransform.pivot(1.4F, 16.2F, -2.6F));
        root.addChild("stone_1", stone(51, 0), ModelTransform.pivot(0.2F, 16.2F, -2.6F));
        root.addChild("stone_2", stone(55, 0), ModelTransform.pivot(-1.1F, 16.2F, -2.6F));
        root.addChild("stone_3", stone(59, 0), ModelTransform.pivot(-2.4F, 16.2F, -2.6F));
        root.addChild("stone_4", stone(25, 2), ModelTransform.pivot(-0.4F, 17.7F, -2.6F));
        root.addChild("stone_5", stone(39, 2), ModelTransform.pivot(-2.6F, 16.6F, 0.5F));

        ModelPart model = TexturedModelData.of(data, 64, 16).createModel();
        return new Built(model,
                model.getChild("index_0"), model.getChild("middle_0"), model.getChild("ring_0"),
                model.getChild("pinkie_0"), model.getChild("thumb_0"),
                model.getChild("index_0").getChild("index_1"),
                model.getChild("middle_0").getChild("middle_1"),
                model.getChild("ring_0").getChild("ring_1"),
                model.getChild("pinkie_0").getChild("pinkie_1"),
                model.getChild("thumb_0").getChild("thumb_1"));
    }

    private static ModelPartBuilder stone(int u, int v) {
        return ModelPartBuilder.create().uv(u, v).cuboid(0.0F, 0.0F, 0.0F, 1, 1, 1);
    }

    /** The original config option that hides the fingers entirely. */
    public static void setFingersVisible(Built model, boolean visible) {
        model.index().visible = visible;
        model.middle().visible = visible;
        model.ring().visible = visible;
        model.pinkie().visible = visible;
        model.thumb().visible = visible;
    }

    /**
     * The original "Fist" pose: the hand closes around whatever it holds. The
     * 1.12 renderer applied these angles while the gauntlet was held or in the
     * hotbar and left the hand open when it lay on the ground.
     */
    public static void setFist(Built model, boolean fist) {
        if (!fist)
            return;
        model.index().pitch = -1.0F;
        model.middle().pitch = -1.0F;
        model.ring().pitch = -1.0F;
        model.pinkie().pitch = -1.0F;
        model.thumb().yaw = (float) Math.toRadians(90.0D);

        model.indexTip().pitch = -0.6F;
        model.middleTip().pitch = -0.6F;
        model.ringTip().pitch = -0.6F;
        model.pinkieTip().pitch = -0.6F;
        model.thumbTip().pitch = -1.4F;
    }
}
