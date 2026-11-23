package lucraft.mods.lucraftcore.infinity;

import net.minecraft.util.Formatting;

/** The six infinity stones, in the original gauntlet slot order. */
public enum EnumInfinityStone {

    SPACE(Formatting.AQUA),
    SOUL(Formatting.GOLD),
    REALITY(Formatting.RED),
    TIME(Formatting.GREEN),
    POWER(Formatting.LIGHT_PURPLE),
    MIND(Formatting.YELLOW);

    private final Formatting color;

    EnumInfinityStone(Formatting color) {
        this.color = color;
    }

    public Formatting getTextColor() {
        return color;
    }
}
