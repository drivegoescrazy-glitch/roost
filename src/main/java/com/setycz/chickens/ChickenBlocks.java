package com.setycz.chickens;

import com.setycz.chickens.block.HenhouseBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

/** The six henhouse wood variants. */
public final class ChickenBlocks {
	private static final Map<String, Block> HENHOUSES = new LinkedHashMap<>();

	public static final Block HENHOUSE = registerHenhouse("henhouse");
	public static final Block HENHOUSE_ACACIA = registerHenhouse("henhouse_acacia");
	public static final Block HENHOUSE_BIRCH = registerHenhouse("henhouse_birch");
	public static final Block HENHOUSE_DARK_OAK = registerHenhouse("henhouse_dark_oak");
	public static final Block HENHOUSE_JUNGLE = registerHenhouse("henhouse_jungle");
	public static final Block HENHOUSE_SPRUCE = registerHenhouse("henhouse_spruce");

	private ChickenBlocks() {
	}

	private static Block registerHenhouse(String name) {
		Identifier id = Identifier.of(ChickensMod.MOD_ID, name);
		Block block = Registry.register(Registries.BLOCK, id,
				new HenhouseBlock(AbstractBlock.Settings.create().strength(2.0F).sounds(BlockSoundGroup.WOOD)));
		Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
		HENHOUSES.put(name, block);
		return block;
	}

	public static void init() {
		// Registration happens on class load.
	}

	public static Map<String, Block> henhouses() {
		return HENHOUSES;
	}
}
