package lucraft.mods.lucraftcore.utilities.blocks;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Machine blocks and their block entities. */
public final class UtilitiesBlocks {

    public static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static Block CONSTRUCTION_TABLE;
    public static Block SUIT_STAND;
    public static Block EXTRACTOR;
    public static Block BOILER;
    public static Block FURNACE_GENERATOR;

    public static BlockEntityType<ConstructionTableBlockEntity> CONSTRUCTION_TABLE_ENTITY;
    public static BlockEntityType<SuitStandBlockEntity> SUIT_STAND_ENTITY;
    public static BlockEntityType<ExtractorBlockEntity> EXTRACTOR_ENTITY;
    public static BlockEntityType<BoilerBlockEntity> BOILER_ENTITY;
    public static BlockEntityType<FurnaceGeneratorBlockEntity> FURNACE_GENERATOR_ENTITY;

    private UtilitiesBlocks() {
    }

    public static void register() {
        CONSTRUCTION_TABLE = registerBlock("construction_table", ConstructionTableBlock::new);
        SUIT_STAND = registerBlock("suit_stand", SuitStandBlock::new);
        EXTRACTOR = registerBlock("extractor", ExtractorBlock::new);
        BOILER = registerBlock("boiler", BoilerBlock::new);
        FURNACE_GENERATOR = registerBlock("furnace_generator", FurnaceGeneratorBlock::new);

        CONSTRUCTION_TABLE_ENTITY = registerEntity("construction_table", ConstructionTableBlockEntity::new, CONSTRUCTION_TABLE);
        SUIT_STAND_ENTITY = registerEntity("suit_stand", SuitStandBlockEntity::new, SUIT_STAND);
        EXTRACTOR_ENTITY = registerEntity("extractor", ExtractorBlockEntity::new, EXTRACTOR);
        BOILER_ENTITY = registerEntity("boiler", BoilerBlockEntity::new, BOILER);
        FURNACE_GENERATOR_ENTITY = registerEntity("furnace_generator", FurnaceGeneratorBlockEntity::new, FURNACE_GENERATOR);

        LucraftCore.LOGGER.info("Registered {} machine block(s)", ALL_ITEMS.size());
    }

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> factory) {
        Identifier id = LucraftCore.id(name);
        AbstractBlock.Settings settings = AbstractBlock.Settings.create().strength(3.0F).requiresTool();
        Block block = Registry.register(Registries.BLOCK, id, factory.apply(settings));
        Item item = Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        ALL_ITEMS.add(item);
        return block;
    }

    @SuppressWarnings("unchecked")
    private static <T extends net.minecraft.block.entity.BlockEntity> BlockEntityType<T> registerEntity(
            String name, BlockEntityType.BlockEntityFactory<? extends T> factory, Block block) {
        BlockEntityType<T> type = (BlockEntityType<T>) BlockEntityType.Builder.create(factory, block).build(null);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, LucraftCore.id(name), type);
    }

    /** Opens a vanilla chest-style view of the machine's inventory. */
    public static void openMachine(PlayerEntity player, UtilitiesBlockEntity machine, Text title) {
        Inventory inventory = machine.getInventory();
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) ->
                new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory,
                        inventory, 1), title));
    }
}
