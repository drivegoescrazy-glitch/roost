package lucraft.mods.lucraftcore.util.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/** Drawing helpers shared by the mod's screens. */
public final class LCGuiHelper {

    private LCGuiHelper() {
    }

    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height) {
        context.drawTexture(texture, x, y, u, v, width, height);
    }

    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int u, int v,
                                   int width, int height, int textureWidth, int textureHeight) {
        context.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    /** Draws a vertical energy bar, red by default. */
    public static void drawEnergyBar(DrawContext context, int x, int y, int width, int height, int energy, int capacity) {
        drawBar(context, x, y, width, height, capacity <= 0 ? 0F : (float) energy / capacity, 0xFFE03C28);
    }

    /** Draws a vertical fluid bar in the supplied colour. */
    public static void drawFluidBar(DrawContext context, int x, int y, int width, int height, int amount, int capacity, FluidColor color) {
        drawBar(context, x, y, width, height, capacity <= 0 ? 0F : (float) amount / capacity, color.argb());
    }

    public static void drawBar(DrawContext context, int x, int y, int width, int height, float fraction, int color) {
        context.fill(x, y, x + width, y + height, 0xFF1A1A1A);
        int filled = Math.round(Math.max(0F, Math.min(1F, fraction)) * height);
        if (filled > 0)
            context.fill(x + 1, y + height - filled, x + width - 1, y + height - 1, color);
    }

    public static void drawTooltipBackground(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xF0100010);
        context.fill(x, y, x + width, y + 1, 0xFF5000FF);
    }

    /** Small colour record so callers do not have to unpack ARGB themselves. */
    public record FluidColor(int argb) {

        public static FluidColor of(int red, int green, int blue) {
            return new FluidColor(0xFF000000 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
    }
}
