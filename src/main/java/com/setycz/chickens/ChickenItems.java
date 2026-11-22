package com.setycz.chickens;

import com.setycz.chickens.item.AnalyzerItem;
import com.setycz.chickens.item.ChickenSpawnEggItem;
import com.setycz.chickens.item.ColoredEggItem;
import com.setycz.chickens.item.LiquidEggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** All Chickens items, using the original registry names. */
public final class ChickenItems {
	public static final Item ANALYZER = register("analyzer",
			new AnalyzerItem(new Item.Settings().maxCount(1).maxDamage(238)));
	public static final Item COLORED_EGG = register("colored_egg",
			new ColoredEggItem(new Item.Settings().maxCount(16)));
	public static final Item SPAWN_EGG = register("spawn_egg",
			new ChickenSpawnEggItem(new Item.Settings().maxCount(64)));
	public static final Item LIQUID_EGG = register("liquid_egg",
			new LiquidEggItem(new Item.Settings().maxCount(16)));

	private ChickenItems() {
	}

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(ChickensMod.MOD_ID, name), item);
	}

	public static void init() {
		// Registration happens on class load.
	}

	public static ItemStack liquidEgg(String liquid) {
		ItemStack stack = new ItemStack(LIQUID_EGG);
		stack.set(ChickenComponents.LIQUID, liquid);
		return stack;
	}

	public static ItemStack coloredEgg(ChickensRegistryItem chicken) {
		ItemStack stack = new ItemStack(COLORED_EGG);
		stack.set(ChickenComponents.CHICKEN_TYPE, chicken.getRegistryName().toString());
		return stack;
	}

	public static ItemStack spawnEgg(ChickensRegistryItem chicken) {
		ItemStack stack = new ItemStack(SPAWN_EGG);
		stack.set(ChickenComponents.CHICKEN_TYPE, chicken.getRegistryName().toString());
		return stack;
	}
}
