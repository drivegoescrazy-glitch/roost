package com.timwoodcreates.roost.block.entity;

import com.timwoodcreates.roost.RoostBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CollectorBlockEntity extends BlockEntity implements SidedInventory, Nameable {
	private static final int SLOT_COUNT = 27;

	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
	private int searchOffset = 0;

	public CollectorBlockEntity(BlockPos pos, BlockState state) {
		super(RoostBlockEntities.COLLECTOR, pos, state);
	}

	public void tick() {
		if (this.world == null || this.world.isClient) {
			return;
		}

		this.searchOffset = (this.searchOffset + 1) % SLOT_COUNT;
		gatherItems();
	}

	private void gatherItems() {
		for (int x = -4; x < 5; x++) {
			int y = this.searchOffset / 9;
			int z = (this.searchOffset % 9) - 4;
			gatherItemAtPos(this.pos.add(x, y, z));
		}
	}

	private void gatherItemAtPos(BlockPos target) {
		BlockEntity blockEntity = this.world.getBlockEntity(target);
		if (!(blockEntity instanceof RoostBlockEntity roost)) {
			return;
		}

		for (int slot : roost.getAvailableSlots(null)) {
			ItemStack stack = roost.getStack(slot);
			if (stack.isEmpty() || !roost.canExtract(slot, stack, null)) {
				continue;
			}

			ItemStack pulled = roost.removeStack(slot, 1);
			ItemStack remainder = insert(pulled);
			if (!remainder.isEmpty()) {
				roost.setStack(slot, remainder);
			} else {
				roost.markDirty();
				markDirty();
			}
			return;
		}
	}

	private ItemStack insert(ItemStack stack) {
		for (int i = 0; i < SLOT_COUNT && !stack.isEmpty(); i++) {
			ItemStack existing = getStack(i);

			if (existing.isEmpty()) {
				setStack(i, stack.copy());
				stack.setCount(0);
			} else if (ItemStack.areItemsEqual(existing, stack)) {
				int max = Math.min(existing.getMaxCount(), getMaxCountPerStack());
				if (existing.getCount() < max) {
					int moved = Math.min(stack.getCount(), max - existing.getCount());
					existing.increment(moved);
					stack.decrement(moved);
				}
			}
		}

		markDirty();
		return stack;
	}

	public void openScreen(PlayerEntity player) {
		player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
				(syncId, playerInventory, playerEntity) -> GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this),
				getDisplayName()));
	}

	@Override
	public Text getName() {
		return Text.translatable("container.roost.collector");
	}

	// Inventory

	@Override
	public int size() {
		return SLOT_COUNT;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : this.inventory) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack stack = this.inventory.get(slot);
		this.inventory.set(slot, ItemStack.EMPTY);
		markDirty();
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack stack = this.inventory.get(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack result;
		if (stack.getCount() <= amount) {
			this.inventory.set(slot, ItemStack.EMPTY);
			result = stack;
		} else {
			result = stack.split(amount);
		}

		markDirty();
		return result;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory.set(slot, stack);
		if (stack.getCount() > getMaxCountPerStack()) {
			stack.setCount(getMaxCountPerStack());
		}
		markDirty();
	}

	@Override
	public void clear() {
		this.inventory.clear();
		markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		int[] slots = new int[SLOT_COUNT];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}
		return slots;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction direction) {
		return true;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction direction) {
		return true;
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		Inventories.readNbt(nbt, this.inventory, registries);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		Inventories.writeNbt(nbt, this.inventory, registries);
	}
}
