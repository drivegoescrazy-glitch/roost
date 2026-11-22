package com.setycz.chickens.screen;

import com.setycz.chickens.ChickenScreenHandlers;
import com.setycz.chickens.block.entity.HenhouseBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

/** Mirrors the original 166x166 henhouse container. */
public class HenhouseScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public HenhouseScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
		this(syncId, playerInventory, resolveInventory(playerInventory, pos), resolveDelegate(playerInventory, pos));
	}

	public HenhouseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
								 PropertyDelegate propertyDelegate) {
		super(ChickenScreenHandlers.HENHOUSE, syncId);
		checkSize(inventory, HenhouseBlockEntity.SLOT_COUNT);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, HenhouseBlockEntity.HAY_SLOT, 25, 19) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isOf(Items.HAY_BLOCK);
			}

			@Override
			public boolean canTakeItems(PlayerEntity playerEntity) {
				return false;
			}
		});

		this.addSlot(new Slot(inventory, HenhouseBlockEntity.DIRT_SLOT, 25, 55) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isOf(Items.DIRT);
			}
		});

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				this.addSlot(new Slot(inventory, HenhouseBlockEntity.FIRST_OUTPUT_SLOT + row * 3 + column,
						98 + column * 18, 17 + row * 18) {
					@Override
					public boolean canInsert(ItemStack stack) {
						return false;
					}
				});
			}
		}

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
			}
		}
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
		}

		this.addProperties(propertyDelegate);
	}

	private static Inventory resolveInventory(PlayerInventory playerInventory, BlockPos pos) {
		if (playerInventory.player.getWorld().getBlockEntity(pos) instanceof HenhouseBlockEntity henhouse) {
			return henhouse;
		}
		return new SimpleInventory(HenhouseBlockEntity.SLOT_COUNT);
	}

	private static PropertyDelegate resolveDelegate(PlayerInventory playerInventory, BlockPos pos) {
		if (playerInventory.player.getWorld().getBlockEntity(pos) instanceof HenhouseBlockEntity henhouse) {
			return henhouse.createPropertyDelegate();
		}
		return new ArrayPropertyDelegate(1);
	}

	public int getEnergy() {
		return this.propertyDelegate.get(0);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasStack()) {
			ItemStack stack = slot.getStack();
			result = stack.copy();

			if (index < HenhouseBlockEntity.SLOT_COUNT) {
				if (!this.insertItem(stack, HenhouseBlockEntity.SLOT_COUNT, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(stack, 0, HenhouseBlockEntity.SLOT_COUNT, false)) {
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}
		return result;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}
}
