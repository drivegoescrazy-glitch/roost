package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * The original 1.12 ability icons. Every 1.12 ability drew a 16x16 cell from
 * {@code textures/gui/icons.png} with {@code LCRenderHelper.drawIcon(...)} as
 * {@code uv = (column * 16, row * 16)}; a few drew an item instead of a sprite.
 * The sheet in this port is byte-identical to the one in the 1.12 jar, so the
 * same coordinates are used here.
 *
 * <p>{@code Ability.drawIcon} in 1.12 fell back to the cell (row 0, column 4),
 * so abilities without their own icon (and abilities of addons the port does
 * not know) get that same cell instead of a letter.</p>
 */
public final class AbilityIcons {

    public static final Identifier TEXTURE = LucraftCore.id("textures/gui/icons.png");

    /** The icon {@code Ability.drawIcon} used when a subclass did not override it. */
    private static final int DEFAULT_COLUMN = 4;
    private static final int DEFAULT_ROW = 0;

    /** A 16x16 cell of the icon sheet, in the 1.12 {@code row}/{@code column} terms. */
    private record Icon(int row, int column) {
    }

    private static final Map<String, Icon> ICONS = new HashMap<>();
    private static final Map<String, Identifier> ITEM_ICONS = new HashMap<>();

    static {
        // Ability.java default icon, inherited by abilities without their own.
        ICONS.put("command", new Icon(0, 4));
        ICONS.put("slowfall", new Icon(0, 4));
        ICONS.put("generator", new Icon(0, 4));
        ICONS.put("toggle_power", new Icon(0, 4));
        ICONS.put("healing", new Icon(0, 0));
        ICONS.put("strength", new Icon(0, 1));
        ICONS.put("punch", new Icon(0, 2));
        ICONS.put("sprint", new Icon(0, 3));
        ICONS.put("speed", new Icon(0, 3));
        ICONS.put("jump_boost", new Icon(0, 4));
        ICONS.put("damage_resistance", new Icon(0, 5));
        ICONS.put("resistance", new Icon(0, 5));
        ICONS.put("fire_resistance", new Icon(0, 6));
        ICONS.put("step_assist", new Icon(0, 7));
        ICONS.put("fall_resistance", new Icon(0, 8));
        ICONS.put("size_change", new Icon(0, 10));
        ICONS.put("health", new Icon(0, 11));
        ICONS.put("knockback_resistance", new Icon(0, 13));
        ICONS.put("energy_blast", new Icon(0, 15));
        ICONS.put("flight", new Icon(1, 0));
        ICONS.put("water_breathing", new Icon(1, 1));
        ICONS.put("tough_lungs", new Icon(1, 1));
        ICONS.put("invisibility", new Icon(1, 6));

        // The Infinityraft stone abilities. The addon's own abilities either
        // drew a stone/clock/gauntlet item or inherited a Lucraft icon.
        ICONS.put("infinity_flight", new Icon(1, 0));
        ICONS.put("infinity_blast", new Icon(0, 15));
        ICONS.put("infinity_healing", new Icon(0, 0));
        ICONS.put("infinity_strength", new Icon(0, 1));
        ICONS.put("infinity_resistance", new Icon(0, 5));
        ICONS.put("infinity_punch", new Icon(0, 2));
        ICONS.put("infinity_invisibility", new Icon(1, 6));
        ICONS.put("infinity_size", new Icon(0, 10));

        // Item icons, exactly the stacks the 1.12 abilities drew.
        ITEM_ICONS.put("teleport", Identifier.of("minecraft", "ender_pearl"));
        ITEM_ICONS.put("command_loop", Identifier.of("minecraft", "command_block"));
        ITEM_ICONS.put("command_toggle", Identifier.of("minecraft", "command_block"));
        ITEM_ICONS.put("command_held", Identifier.of("minecraft", "command_block"));
        ITEM_ICONS.put("snap", Identifier.of("infinity", "infinity_gauntlet"));
        ITEM_ICONS.put("kill", Identifier.of("infinity", "soul_stone"));
        ITEM_ICONS.put("changesnap", Identifier.of("infinity", "mind_stone"));
        ITEM_ICONS.put("stop_time", Identifier.of("infinity", "time_stone"));
        ITEM_ICONS.put("fast_forward", Identifier.of("minecraft", "clock"));
    }

    private AbilityIcons() {
    }

    /**
     * Draws the ability's original icon at {@code x, y} (16x16), falling back to
     * the original default cell like {@code Ability.drawIcon} did.
     */
    public static boolean draw(DrawContext context, String id, int x, int y) {
        String key = normalize(id);
        Icon icon = ICONS.get(key);
        if (icon == null) {
            Identifier itemId = ITEM_ICONS.get(key);
            if (itemId != null) {
                Item item = Registries.ITEM.get(itemId);
                if (item != Items.AIR) {
                    context.drawItem(new ItemStack(item), x, y);
                    return true;
                }
            }
            icon = new Icon(DEFAULT_ROW, DEFAULT_COLUMN);
        }
        context.drawTexture(TEXTURE, x, y, icon.column() * 16, icon.row() * 16, 16, 16);
        return true;
    }

    private static String normalize(String id) {
        if (id == null)
            return "";
        return id.startsWith("lucraftcore:") ? id.substring("lucraftcore:".length()) : id;
    }
}
