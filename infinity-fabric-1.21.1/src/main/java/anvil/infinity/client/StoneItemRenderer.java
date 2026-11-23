package anvil.infinity.client;

import anvil.infinity.Infinity;
import anvil.infinity.config.InfinityConfig;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * The original stones were not flat textures: Lucraft drew a small rotating 3D
 * cube, tinted with the stone's configured colour and overlaid with two
 * additive glint passes in the shine colour. This reproduces that renderer with
 * the same cube, UV layout, scale, tint and glint animation.
 */
public class StoneItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    // Lucraft ships the stone texture; the original renderer used it too.
    private static final Identifier STONE_TEXTURE = Identifier.of("lucraftcore", "textures/models/stone.png");

    private static ModelPart cube;

    /** 6x6x6 cube at the original rotation point (0, 21, 0), 32x16 texture. */
    public static ModelPart cube() {
        if (cube == null) {
            ModelData data = new ModelData();
            data.getRoot().addChild("stone",
                    ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6),
                    ModelTransform.pivot(0.0F, 21.0F, 0.0F));
            cube = TexturedModelData.of(data, 32, 16).createModel();
        }
        return cube;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfinityStone stone))
            return;

        int color = InfinityConfig.color(stone.getType());
        int shine = InfinityConfig.shine(stone.getType());

        matrices.push();
        matrices.translate(0.5D, -0.1D, 0.5D);
        matrices.scale(0.4F, 0.4F, 0.4F);

        cube().render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(STONE_TEXTURE)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, color | 0xFF000000);

        // Two additive glint passes. In the original these were drawn with the
        // drift and the 1/3 scale on the *texture* matrix: only the shine moved,
        // never the cube. The glint layer owns that animation here, so the cube
        // is drawn in place and the layer scrolls the glint over it.
        for (int pass = 0; pass < 2; pass++) {
            cube().render(matrices, vertexConsumers.getBuffer(RenderLayer.getGlint()),
                    LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV,
                    (shine & 0xFFFFFF) | 0x80000000);
        }

        matrices.pop();
    }
}
