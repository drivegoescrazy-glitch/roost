package com.timwoodcreates.roost;

import com.timwoodcreates.roost.item.ItemCatcher;
import com.timwoodcreates.roost.item.ItemChicken;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class RoostItems {
	public static final BlockItem ROOST = new BlockItem(RoostBlocks.ROOST, new Item.Settings());
	public static final BlockItem BREEDER = new BlockItem(RoostBlocks.BREEDER, new Item.Settings());
	public static final BlockItem COLLECTOR = new BlockItem(RoostBlocks.COLLECTOR, new Item.Settings());
	public static final ItemCatcher ITEM_CATCHER = new ItemCatcher();
	public static final ItemChicken ITEM_CHICKEN = new ItemChicken();

	private RoostItems() {
	}

	public static void init() {
		register("roost", ROOST);
		register("breeder", BREEDER);
		register("collector", COLLECTOR);
		register("catcher", ITEM_CATCHER);
		register("chicken", ITEM_CHICKEN);
	}

	private static void register(String name, Item item) {
		Registry.register(Registries.ITEM, Identifier.of(Roost.MODID, name), item);
	}
}
