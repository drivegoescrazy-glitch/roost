package lucraft.mods.lucraftcore.util.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Button with the mod's default narration. */
public class LCButton extends ButtonWidget {

    public LCButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    public static LCButton of(int x, int y, int width, int height, Text message, PressAction onPress) {
        return new LCButton(x, y, width, height, message, onPress);
    }
}
