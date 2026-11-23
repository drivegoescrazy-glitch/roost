package lucraft.mods.lucraftcore.materials;

import lucraft.mods.lucraftcore.LCConfig;
import lucraft.mods.lucraftcore.LucraftCore;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.world.gen.GenerationStep;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModuleMaterials {

    public static final Map<Material, Map<Material.Component, Item>> ITEMS = new LinkedHashMap<>();
    public static final Map<Material, Block> ORES = new LinkedHashMap<>();
    public static final Map<Material, Block> BLOCKS = new LinkedHashMap<>();

    private static StatusEffect radiation;
    private static RegistryEntry<StatusEffect> radiationEntry;
    public static ItemGroup GROUP;

    private static final Material.Component[] ITEM_COMPONENTS = {
            Material.Component.INGOT, Material.Component.NUGGET, Material.Component.PLATE,
            Material.Component.DUST, Material.Component.WIRING
    };

    public static void register() {
        if (!LCConfig.materials)
            return;

        radiation = Registry.register(Registries.STATUS_EFFECT, LucraftCore.id("radiation"), new RadiationStatusEffect());
        radiationEntry = Registries.STATUS_EFFECT.getEntry(radiation);

        registerItems();
        registerBlocks();
        registerCreativeTab();
        registerWorldgen();

        LucraftCore.LOGGER.info("Materials module registered: {} materials, {} items, {} blocks",
                Material.ALL.size(), ITEMS.values().stream().mapToInt(Map::size).sum(), ORES.size() + BLOCKS.size());
    }

    public static RegistryEntry<StatusEffect> radiationEntry() {
        return radiationEntry;
    }

    public static StatusEffect radiation() {
        return radiation;
    }

    private static void registerItems() {
        for (Material material : Material.ALL) {
            for (Material.Component component : ITEM_COMPONENTS) {
                if (!material.has(component))
                    continue;
                if ((material == Material.IRON || material == Material.GOLD)
                        && (component == Material.Component.INGOT || component == Material.Component.NUGGET))
                    continue;
                Item item = new ItemLCMaterial(material, component);
                Registry.register(Registries.ITEM, material.getId(component), item);
                ITEMS.computeIfAbsent(material, m -> new LinkedHashMap<>()).put(component, item);
            }
        }
    }

    private static void registerBlocks() {
        for (Material material : Material.ALL) {
            for (Material.Component component : new Material.Component[]{Material.Component.ORE, Material.Component.BLOCK}) {
                if (!material.has(component))
                    continue;
                Block block = new BlockLCMaterial(material, component);
                Registry.register(Registries.BLOCK, material.getId(component), block);
                Registry.register(Registries.ITEM, material.getId(component), new BlockItem(block, new Item.Settings()));
                (component == Material.Component.ORE ? ORES : BLOCKS).put(material, block);
            }
        }
    }

    private static void registerCreativeTab() {
        GROUP = Registry.register(Registries.ITEM_GROUP, LucraftCore.id("materials"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.tabMaterials"))
                        .icon(() -> new ItemStack(ITEMS.get(Material.IRIDIUM).get(Material.Component.PLATE)))
                        .entries((context, entries) -> {
                            for (Material material : Material.ALL) {
                                Map<Material.Component, Item> items = ITEMS.get(material);
                                if (items != null) {
                                    for (Material.Component component : ITEM_COMPONENTS) {
                                        Item item = items.get(component);
                                        if (item != null)
                                            entries.add(item);
                                    }
                                }
                                Block ore = ORES.get(material);
                                if (ore != null)
                                    entries.add(ore);
                                Block block = BLOCKS.get(material);
                                if (block != null)
                                    entries.add(block);
                            }
                        })
                        .build());
    }

    private static void registerWorldgen() {
        for (Material material : Material.ALL) {
            if (!material.generatesOre())
                continue;
            BiomeModifications.addFeature(
                    BiomeSelectors.foundInOverworld(),
                    GenerationStep.Feature.UNDERGROUND_ORES,
                    RegistryKey.of(RegistryKeys.PLACED_FEATURE, LucraftCore.id("ore_" + material.getName())));
        }
    }
}
