package com.timwoodcreates.roost.screen.slot;

import com.timwoodcreates.roost.block.entity.ChickenContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotSeeds extends Slot {
	public SlotSeeds(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (this.inventory instanceof ChickenContainerBlockEntity container) {
			container.willNeedToUpdateChickenInfo();
		}
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return ChickenContainerBlockEntity.isSeed(stack);
	}
}
