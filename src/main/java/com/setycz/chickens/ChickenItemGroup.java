package com.setycz.chickens;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** The Chickens creative tab. */
public final class ChickenItemGroup {
	public static final ItemGroup CHICKENS = Registry.register(Registries.ITEM_GROUP,
			Identifier.of(ChickensMod.MOD_ID, "chickens"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemGroup.chickens"))
					.icon(() -> new ItemStack(ChickenItems.ANALYZER))
					.entries((context, entries) -> {
						entries.add(ChickenItems.ANALYZER);
						entries.add(ChickenItems.COLORED_EGG);
						entries.add(ChickenItems.LIQUID_EGG);
						for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
							entries.add(ChickenItems.spawnEgg(chicken));
							if (chicken.getColorKey() != null) {
								entries.add(ChickenItems.coloredEgg(chicken));
							}
						}
						ChickenBlocks.henhouses().values().forEach(block -> entries.add(block));
					})
					.build());

	private ChickenItemGroup() {
	}

	public static void init() {
		// Registration happens on class load.
	}
}
