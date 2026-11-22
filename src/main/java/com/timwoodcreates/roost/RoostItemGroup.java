package com.timwoodcreates.roost;

import com.timwoodcreates.roost.api.ChickenType;
import com.timwoodcreates.roost.data.DataChicken;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class RoostItemGroup {
	public static final RegistryKey<ItemGroup> GROUP =
			RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Roost.MODID, "roost"));

	private RoostItemGroup() {
	}

	public static void init() {
		Registry.register(Registries.ITEM_GROUP, GROUP, FabricItemGroup.builder()
				.icon(() -> new ItemStack(RoostItems.ITEM_CHICKEN))
				.displayName(Text.translatable("itemGroup.roost"))
				.build());

		ItemGroupEvents.modifyEntriesEvent(GROUP).register(entries -> {
			entries.add(RoostItems.ROOST);
			entries.add(RoostItems.BREEDER);
			entries.add(RoostItems.COLLECTOR);
			entries.add(RoostItems.ITEM_CATCHER);

			for (ChickenType type : RoostChickens.sorted()) {
				DataChicken chicken = DataChicken.of(type);
				if (chicken != null) {
					entries.add(chicken.buildChickenStack());
				}
			}
		});
	}
}
