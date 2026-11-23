package lucraft.mods.lucraftcore.infinity;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.infinity.blocks.InfinityGeneratorBlock;
import lucraft.mods.lucraftcore.infinity.blocks.InfinityGeneratorBlockEntity;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemLCCast;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/** Infinity gauntlet, gauntlet cast and infinity generator. */
public final class ModuleInfinity {

    public static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static Item INFINITY_GAUNTLET;
    public static Item INFINITY_GAUNTLET_CAST;
    public static Block INFINITY_GENERATOR;
    public static BlockEntityType<InfinityGeneratorBlockEntity> INFINITY_GENERATOR_ENTITY;

    private ModuleInfinity() {
    }

    public static void register() {
        INFINITY_GAUNTLET = registerItem("infinity_gauntlet", new ItemInfinityGauntlet());
        INFINITY_GAUNTLET_CAST = registerItem("infinity_gauntlet_cast", new ItemLCCast());

        INFINITY_GENERATOR = Registry.register(Registries.BLOCK, LucraftCore.id("infinity_generator"),
                new InfinityGeneratorBlock(AbstractBlock.Settings.create().strength(5.0F, 10.0F).requiresTool().nonOpaque()));
        ALL_ITEMS.add(Registry.register(Registries.ITEM, LucraftCore.id("infinity_generator"),
                new BlockItem(INFINITY_GENERATOR, new Item.Settings())));

        @SuppressWarnings("unchecked")
        BlockEntityType<InfinityGeneratorBlockEntity> type =
                (BlockEntityType<InfinityGeneratorBlockEntity>) BlockEntityType.Builder
                        .create(InfinityGeneratorBlockEntity::new, INFINITY_GENERATOR).build(null);
        INFINITY_GENERATOR_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                LucraftCore.id("infinity_generator"), type);

        LucraftCore.LOGGER.info("Infinity module registered");
    }

    private static Item registerItem(String name, Item item) {
        Item registered = Registry.register(Registries.ITEM, LucraftCore.id(name), item);
        ALL_ITEMS.add(registered);
        return registered;
    }
}
