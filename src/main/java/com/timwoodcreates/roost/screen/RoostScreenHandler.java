package com.timwoodcreates.roost.screen;

import com.timwoodcreates.roost.RoostScreenHandlers;
import com.timwoodcreates.roost.screen.slot.SlotChicken;
import com.timwoodcreates.roost.screen.slot.SlotReadOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.text.DecimalFormat;

public class RoostScreenHandler extends ScreenHandler {
	private static final int SLOT_COUNT = 5;
	private static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");

	private final Inventory inventory;
	private final PropertyDelegate properties;

	public RoostScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate properties) {
		super(RoostScreenHandlers.ROOST, syncId);
		checkSize(inventory, SLOT_COUNT);
		checkDataCount(properties, 1);
		this.inventory = inventory;
		this.properties = properties;
		this.addProperties(properties);

		this.addSlot(new SlotChicken(inventory, 0, 26, 20));

		for (int i = 0; i < 4; ++i) {
			this.addSlot(new SlotReadOnly(inventory, i + 1, 80 + i * 18, 20));
		}

		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 51 + row * 18));
			}
		}

		for (int column = 0; column < 9; ++column) {
			this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 109));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index) {
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasStack()) {
			ItemStack current = slot.getStack();
			previous = current.copy();

			if (index < SLOT_COUNT) {
				if (!this.insertItem(current, SLOT_COUNT, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(current, 0, 1, false)) {
				return ItemStack.EMPTY;
			}

			if (current.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return previous;
	}

	public double getProgress() {
		return this.properties.get(0) / 1000.0d;
	}

	public String getFormattedProgress() {
		return FORMATTER.format(getProgress());
	}

	public int getProgressWidth() {
		double progress = getProgress();
		return progress == 0.0d ? 0 : 1 + (int) (progress * 25);
	}
}
