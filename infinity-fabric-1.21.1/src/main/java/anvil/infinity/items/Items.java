package anvil.infinity.items;

import anvil.infinity.Infinity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/** The six infinity stones, registered under their original ids. */
public final class Items {

    public static final List<Item> ALL = new ArrayList<>();

    public static Item POWER_STONE;
    public static Item MIND_STONE;
    public static Item REALITY_STONE;
    public static Item SOUL_STONE;
    public static Item TIME_STONE;
    public static Item SPACE_STONE;

    private Items() {
    }

    public static void register() {
        POWER_STONE = register("power_stone", new ItemPowerStone());
        MIND_STONE = register("mind_stone", new ItemMindStone());
        REALITY_STONE = register("reality_stone", new ItemRealityStone());
        SOUL_STONE = register("soul_stone", new ItemSoulStone());
        TIME_STONE = register("time_stone", new ItemTimeStone());
        SPACE_STONE = register("space_stone", new ItemSpaceStone());
        Infinity.LOGGER.debug("Registered {} infinity stones", ALL.size());
    }

    private static Item register(String name, Item item) {
        Item registered = Registry.register(Registries.ITEM, Infinity.id(name), item);
        ALL.add(registered);
        return registered;
    }
}
