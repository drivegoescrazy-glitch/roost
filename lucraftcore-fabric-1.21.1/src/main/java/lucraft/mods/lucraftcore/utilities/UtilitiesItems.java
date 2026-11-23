package lucraft.mods.lucraftcore.utilities;

import lucraft.mods.lucraftcore.LucraftCore;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Items that used to live in the utilities module (crafting tools and components). */
public class UtilitiesItems {

    public static final List<Item> ALL = new ArrayList<>();
    public static final Map<String, Item> TRI_POLYMER = new LinkedHashMap<>();

    /** Same order as the original EnumDyeColor names (silver = light gray). */
    public static final String[] TRI_POLYMER_COLOURS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    public static Item HAMMER;
    public static Item WIRE_CUTTER;
    public static Item INJECTION;
    public static Item INSTRUCTION;
    public static Item LV_CAPACITOR;
    public static Item MV_CAPACITOR;
    public static Item HV_CAPACITOR;
    public static Item SERVO_MOTOR;
    public static Item BASIC_CIRCUIT;
    public static Item ADVANCED_CIRCUIT;
    public static ItemGroup GROUP;

    public static void register() {
        HAMMER = register("hammer", new ItemHammer());
        WIRE_CUTTER = register("wire_cutter", new ItemHammer());
        INJECTION = register("injection", new ItemInjection());
        INSTRUCTION = register("instruction", new ItemInstruction());
        LV_CAPACITOR = register("lv_capacitor", new Item(new Item.Settings()));
        MV_CAPACITOR = register("mv_capacitor", new Item(new Item.Settings()));
        HV_CAPACITOR = register("hv_capacitor", new Item(new Item.Settings()));
        SERVO_MOTOR = register("servo_motor", new Item(new Item.Settings()));
        BASIC_CIRCUIT = register("basic_circuit", new Item(new Item.Settings()));
        ADVANCED_CIRCUIT = register("advanced_circuit", new Item(new Item.Settings()));

        for (String colour : TRI_POLYMER_COLOURS) {
            TRI_POLYMER.put(colour, register("tri_polymer_" + colour, new Item(new Item.Settings())));
        }

        GROUP = Registry.register(Registries.ITEM_GROUP, LucraftCore.id("lucraftcore"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.tabLucraftCore"))
                        .icon(() -> new ItemStack(HAMMER))
                        .entries((context, entries) -> {
                            for (Item item : ALL)
                                entries.add(item);
                            for (lucraft.mods.lucraftcore.utilities.recipes.InstructionRecipe recipe
                                    : lucraft.mods.lucraftcore.utilities.recipes.InstructionRecipe.getAll())
                                entries.add(ItemInstruction.createStack(recipe));
                            for (lucraft.mods.lucraftcore.superpowers.suitsets.SuitSet suit
                                    : lucraft.mods.lucraftcore.superpowers.suitsets.SuitSetRegistry.getAll())
                                for (Item piece : suit.getPieces().values())
                                    entries.add(piece);
                            for (Item machine : lucraft.mods.lucraftcore.utilities.blocks.UtilitiesBlocks.ALL_ITEMS)
                                entries.add(machine);
                            for (Item infinity : lucraft.mods.lucraftcore.infinity.ModuleInfinity.ALL_ITEMS)
                                entries.add(infinity);
                        })
                        .build());

        LucraftCore.LOGGER.info("Utilities module registered: {} items", ALL.size());
    }

    private static Item register(String name, Item item) {
        Registry.register(Registries.ITEM, LucraftCore.id(name), item);
        ALL.add(item);
        return item;
    }
}
