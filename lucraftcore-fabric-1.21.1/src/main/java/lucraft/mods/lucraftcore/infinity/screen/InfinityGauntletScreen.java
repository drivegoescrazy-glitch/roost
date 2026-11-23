package lucraft.mods.lucraftcore.infinity.screen;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * The original gauntlet screen: the 256x256 {@code infinity_gauntlet.png}
 * drawn as a 176x211 panel, with no title text (the 1.12 GUI drew none).
 */
public class InfinityGauntletScreen extends HandledScreen<InfinityGauntletScreenHandler> {

    private static final Identifier TEXTURE = LucraftCore.id("textures/gui/infinity_gauntlet.png");

    public InfinityGauntletScreen(InfinityGauntletScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 211;
        this.titleX = 8;
        this.titleY = 6;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // A throw while a screen draws leaves the GUI render state broken for the
        // rest of the session (black screen, buttons without their background),
        // so the panel draw is exception safe like the rest of the mod's drawing.
        LucraftCore.guard("infinity gauntlet screen", () ->
                context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight));
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // The original screen has no title or inventory label.
    }
}
