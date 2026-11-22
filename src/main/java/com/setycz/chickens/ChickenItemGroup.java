package com.setycz.chickens;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The Chickens creative tab.
 *
 * <p>The tab contents are built once into a stable, duplicate-free list and then
 * copied into the item group every time the game asks for them. Building them
 * this way matters: the 1.21.1 creative screen throws if a group reports the
 * same stack twice, and that exception used to escape while the inventory was
 * being opened, taking the whole client down with it.</p>
 */
public final class ChickenItemGroup {
	private static List<ItemStack> displayStacks;
	private static int builtKinds = -1;

	public static final ItemGroup CHICKENS = Registry.register(Registries.ITEM_GROUP,
			Identifier.of(ChickensMod.MOD_ID, "chickens"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemGroup.chickens"))
					.icon(() -> new ItemStack(ChickenItems.ANALYZER))
					.entries((context, entries) -> {
						for (ItemStack stack : displayStacks()) {
							try {
								entries.add(stack.copy());
							} catch (Throwable throwable) {
								// A single bad or repeated stack must never break the
								// creative inventory screen.
								ChickensMod.LOGGER.warn("Skipping creative tab entry {}", stack, throwable);
							}
						}
					})
					.build());

	private ChickenItemGroup() {
	}

	/** Rebuilds the group's list with the client's world context, if possible. */
	public static void updateDisplay(FeatureSet enabledFeatures, boolean hasPermissions,
			RegistryWrapper.WrapperLookup lookup) {
		try {
			CHICKENS.updateEntries(new ItemGroup.DisplayContext(enabledFeatures, hasPermissions, lookup));
		} catch (Throwable throwable) {
			ChickensMod.LOGGER.warn("Could not refresh the creative tab", throwable);
		}
	}

	private static synchronized List<ItemStack> displayStacks() {
		List<ChickensRegistryItem> kinds = ChickensRegistry.getItems();
		if (displayStacks != null && builtKinds == kinds.size()) {
			return displayStacks;
		}

		Set<ItemStack> unique = new LinkedHashSet<>();
		add(unique, new ItemStack(ChickenItems.ANALYZER));
		add(unique, new ItemStack(ChickenItems.COLORED_EGG));
		add(unique, new ItemStack(ChickenItems.LIQUID_EGG));
		for (ChickensRegistryItem chicken : kinds) {
			add(unique, ChickenItems.spawnEgg(chicken));
			if (chicken.getColorKey() != null) {
				add(unique, ChickenItems.coloredEgg(chicken));
			}
		}
		for (var block : ChickenBlocks.henhouses().values()) {
			add(unique, new ItemStack(block));
		}

		displayStacks = List.copyOf(new ArrayList<>(unique));
		builtKinds = kinds.size();
		ChickensMod.LOGGER.info("Creative tab ready with {} entries", displayStacks.size());
		return displayStacks;
	}

	private static void add(Set<ItemStack> stacks, ItemStack stack) {
		if (!stack.isEmpty()) {
			stacks.add(stack);
		}
	}

	public static void init() {
		// Registration happens on class load.
	}
}
