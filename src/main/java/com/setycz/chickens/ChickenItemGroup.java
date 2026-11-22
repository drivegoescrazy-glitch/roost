package com.setycz.chickens;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The Chickens creative tab.
 *
 * <p>Two rules keep this tab from ever taking the game down:</p>
 * <ul>
 *   <li>the entries are rebuilt from scratch on every request, from a
 *       duplicate-free list, and each add is guarded, so a bad or repeated
 *       stack can never abort the creative screen (1.21.1 throws out of the
 *       screen's refresh if a tab misbehaves, which kills the client and
 *       leaves the singleplayer server hung);</li>
 *   <li>the group is given a page as soon as it is registered. Fabric API's
 *       creative-screen pagination throws "Item group has no page" for any
 *       group whose page is still unset, and that throw happens on the render
 *       thread while the inventory is opening.</li>
 * </ul>
 */
public final class ChickenItemGroup {
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
								ChickensMod.LOGGER.warn("Skipping creative tab entry {}", stack, throwable);
							}
						}
					})
					.build());

	private ChickenItemGroup() {
	}

	/**
	 * Builds the tab contents. Never cached: the list is rebuilt for every
	 * refresh so no item group state is shared between the client and the
	 * integrated server in singleplayer.
	 */
	private static List<ItemStack> displayStacks() {
		Set<ItemStack> unique = new LinkedHashSet<>();
		add(unique, new ItemStack(ChickenItems.ANALYZER));
		add(unique, new ItemStack(ChickenItems.COLORED_EGG));
		add(unique, new ItemStack(ChickenItems.LIQUID_EGG));
		for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
			add(unique, ChickenItems.spawnEgg(chicken));
			if (chicken.getColorKey() != null) {
				add(unique, ChickenItems.coloredEgg(chicken));
			}
		}
		for (var block : ChickenBlocks.henhouses().values()) {
			add(unique, new ItemStack(block));
		}
		return List.copyOf(unique);
	}

	private static void add(Set<ItemStack> stacks, ItemStack stack) {
		if (!stack.isEmpty()) {
			stacks.add(stack);
		}
	}

	public static void init() {
		ensurePages();
	}

	/**
	 * Fabric API assigns creative-tab pages in a pass that runs at the end of
	 * {@code ItemGroups.updateEntries}. If that pass never completes - another
	 * mod's tab entries throwing is enough - every group keeps page {@code -1},
	 * and the creative screen then throws "Item group has no page" while it is
	 * opening. In singleplayer that kills the client's connection to the
	 * integrated server and leaves the game stuck on a dead menu.
	 *
	 * <p>This gives every group that has no page a usable fallback: 0 for
	 * vanilla groups, 1 for ours. A normal pagination pass overwrites these
	 * values, so it only matters when that pass failed.</p>
	 */
	private static void ensurePages() {
		try {
			Class<?> impl = Class.forName("net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl");
			var getPage = impl.getMethod("fabric_getPage");
			var setPage = impl.getMethod("fabric_setPage", int.class);
			int assigned = 0;
			for (ItemGroup group : Registries.ITEM_GROUP) {
				try {
					getPage.invoke(group);
				} catch (Throwable noPage) {
					setPage.invoke(group, group == CHICKENS ? 1 : 0);
					assigned++;
				}
			}
			ChickensMod.LOGGER.info("Assigned fallback creative tab pages to {} item groups", assigned);
		} catch (Throwable throwable) {
			ChickensMod.LOGGER.debug("Fabric item group pagination unavailable", throwable);
		}
	}
}
