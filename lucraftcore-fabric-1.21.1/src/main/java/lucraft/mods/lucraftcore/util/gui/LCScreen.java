package lucraft.mods.lucraftcore.util.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Base for the mod's container screens: supplies the background texture size and
 * draws the vanilla-style background. Subclasses add widgets and foreground
 * decorations.
 */
public abstract class LCScreen<T extends ScreenHandler> extends HandledScreen<T> {

    private final Identifier background;

    protected LCScreen(T handler, PlayerInventory inventory, Text title, Identifier background, int width, int height) {
        super(handler, inventory, title);
        this.background = background;
        this.backgroundWidth = width;
        this.backgroundHeight = height;
        this.titleX = 8;
        this.titleY = 6;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(background, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    public Identifier getBackground() {
        return background;
    }
}
