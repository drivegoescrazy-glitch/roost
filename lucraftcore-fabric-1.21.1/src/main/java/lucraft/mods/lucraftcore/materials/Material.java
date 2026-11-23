package lucraft.mods.lucraftcore.materials;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Material {

    public enum Component {
        INGOT("ingot"), ORE("ore"), BLOCK("block"), NUGGET("nugget"), PLATE("plate"), DUST("dust"), WIRING("wiring");

        private final String name;

        Component(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getOrePrefix() {
            return this == WIRING ? "wire" : name;
        }
    }

    public static final Map<String, Material> BY_NAME = new LinkedHashMap<>();
    public static final List<Material> ALL = new ArrayList<>();

    public static final Material IRON = create("iron", 0, 0, 0xd8d8d8, 769, false, Component.DUST, Component.PLATE);
    public static final Material GOLD = create("gold", 0, 0, 0xffff8b, 532, false, Component.DUST, Component.PLATE);
    public static final Material COPPER = create("copper", 1, 8, 0xd0952f, 542, false, Component.values()).ore(3, 8, 11, 0, 75);
    public static final Material TIN = create("tin", 1, 8, 0xd8bfaa, 350, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(2, 7, 9, 20, 55);
    public static final Material NICKEL = create("nickel", 1, 9, 0xedede8, 727, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(1, 4, 3, 5, 20);
    public static final Material LEAD = create("lead", 2, 9, 0x668595, 400, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(2, 8, 6, 10, 35);
    public static final Material SILVER = create("silver", 2, 10, 0xe2e8e9, 480, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(3, 8, 6, 5, 30);
    public static final Material PALLADIUM = create("palladium", 2, 9, 0xe7e7e7, 500, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(2, 6, 4, 2, 18);
    public static final Material TITANIUM = create("titanium", 2, 17, 0x474747, 800, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(2, 5, 2, 4, 14);
    public static final Material VIBRANIUM = create("vibranium", 3, 23, 0x91c1cc, 950, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(1, 4, 2, 3, 12);
    public static final Material OSMIUM = create("osmium", 1, 9, 0x8aa9a9, 500, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(1, 5, 5, 10, 32);
    public static final Material IRIDIUM = create("iridium", 3, 13, 0xd8dedf, 900, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(1, 4, 2, 3, 12);
    public static final Material DWARF_STAR_ALLOY = create("dwarf_star_alloy", 2, 15, 0x7a7c80, 760, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(2, 6, 4, 2, 18);
    public static final Material BRONZE = create("bronze", 1, 11, 0xb35f36, 475, false, Component.INGOT, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST);
    public static final Material GOLD_TITANIUM_ALLOY = create("gold_titanium_alloy", 2, 20, 0x80731f, 850, false, Component.INGOT, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST);
    public static final Material INTERTIUM = create("intertium", 1, 11, 0xa15e5e, 752, false, Component.INGOT, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST);
    public static final Material URANIUM = create("uranium", 1, 9, 0x88d160, 0, true, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST).ore(3, 6, 4, 8, 24);
    public static final Material STEEL = create("steel", 2, 13, 0x656565, 681, false, Component.INGOT, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST);
    public static final Material ADAMANTIUM = create("adamantium", 3, 30, 0xf7ffff, 1000, false, Component.INGOT, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST);
    public static final Material URU = create("uru", 3, 60, 0xd8d8d8, 3000, false, Component.INGOT, Component.ORE, Component.BLOCK, Component.NUGGET, Component.PLATE, Component.DUST);
    public static final Material COAL = create("coal", 0, 0, 0x2a2a2a, 0, false, Component.DUST);
    public static final Material CHARCOAL = create("charcoal", 0, 0, 0x2f2618, 0, false, Component.DUST);

    private final String name;
    private final int harvestLevel;
    private final float resistance;
    private final int color;
    private final int temperature;
    private final boolean radioactive;
    private final List<Component> components;
    private int[] oreSettings;

    private Material(String name, int harvestLevel, float resistance, int color, int temperature, boolean radioactive, Component[] components) {
        this.name = name;
        this.harvestLevel = harvestLevel;
        this.resistance = resistance;
        this.color = color;
        this.temperature = temperature;
        this.radioactive = radioactive;
        this.components = Arrays.asList(components);
    }

    private static Material create(String name, int harvestLevel, float resistance, int color, int temperature, boolean radioactive, Component... components) {
        Material material = new Material(name, harvestLevel, resistance, color, temperature, radioactive, components);
        BY_NAME.put(name, material);
        ALL.add(material);
        return material;
    }

    private Material ore(int minVeinSize, int maxVeinSize, int chance, int minY, int maxY) {
        this.oreSettings = new int[]{minVeinSize, maxVeinSize, chance, minY, maxY};
        return this;
    }

    public String getName() {
        return name;
    }

    public Identifier getId(Component component) {
        return LucraftCore.id(component.getName() + "_" + name);
    }

    public boolean has(Component component) {
        return components.contains(component);
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public float getResistance() {
        return resistance;
    }

    public float getBlockHardness() {
        return resistance / 2F;
    }

    public int getColor() {
        return color;
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean isRadioactive() {
        return radioactive;
    }

    public int[] getOreSettings() {
        return oreSettings;
    }

    public boolean generatesOre() {
        return oreSettings != null;
    }

    public String getOreDictionaryName(Component component) {
        StringBuilder builder = new StringBuilder(component.getOrePrefix());
        boolean upper = true;
        for (char c : name.toCharArray()) {
            if (c == '_') {
                upper = true;
                continue;
            }
            builder.append(upper ? Character.toUpperCase(c) : c);
            upper = false;
        }
        return builder.toString();
    }

    public Item getItem(Component component) {
        if (component == Component.INGOT && this == IRON)
            return Items.IRON_INGOT;
        if (component == Component.INGOT && this == GOLD)
            return Items.GOLD_INGOT;
        if (component == Component.NUGGET && this == IRON)
            return Items.IRON_NUGGET;
        if (component == Component.NUGGET && this == GOLD)
            return Items.GOLD_NUGGET;
        if (component == Component.ORE && this == IRON)
            return Items.IRON_ORE;
        if (component == Component.BLOCK && this == IRON)
            return Items.IRON_BLOCK;
        if (component == Component.ORE && this == GOLD)
            return Items.GOLD_ORE;
        if (component == Component.BLOCK && this == GOLD)
            return Items.GOLD_BLOCK;

        Item item = Registries.ITEM.get(getId(component));
        if (item == Items.AIR)
            return null;
        return item;
    }

    public ItemStack getStack(Component component) {
        Item item = getItem(component);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    public static Material byName(String name) {
        return BY_NAME.get(name);
    }
}
