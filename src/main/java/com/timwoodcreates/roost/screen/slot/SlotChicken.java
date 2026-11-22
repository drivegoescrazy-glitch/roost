package com.timwoodcreates.roost.screen.slot;

import com.timwoodcreates.roost.data.DataChicken;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotChicken extends Slot {
	public SlotChicken(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return DataChicken.isChicken(stack);
	}
}
