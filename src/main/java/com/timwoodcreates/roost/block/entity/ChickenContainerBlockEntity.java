package com.timwoodcreates.roost.block.entity;

import com.timwoodcreates.roost.data.DataChicken;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.text.DecimalFormat;

/**
 * Shared behaviour of the roost and the breeder: chickens in the first slots, an optional seed
 * slot, output slots, and a timer that produces a drop once the animals are ready.
 */
public abstract class ChickenContainerBlockEntity extends BlockEntity
		implements SidedInventory, Nameable, ExtendedScreenHandlerFactory<BlockPos> {

	private static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");

	private final int inventorySize;
	private final int chickenSlots;
	private final DefaultedList<ItemStack> inventory;
	private final DataChicken[] chickenData;

	private boolean mightNeedToUpdateChickenInfo = true;
	private boolean skipNextTimerReset = false;
	private int timeUntilNextDrop = 0;
	private int timeElapsed = 0;
	private int progress = 0;

	private boolean fullOfChickens = false;
	private boolean fullOfSeeds = false;

	protected ChickenContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
			int inventorySize, int chickenSlots) {
		super(type, pos, state);
		this.inventorySize = inventorySize;
		this.chickenSlots = chickenSlots;
		this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
		this.chickenData = new DataChicken[chickenSlots];
	}

	// Update

	public void tick() {
		if (this.world == null || this.world.isClient) {
			return;
		}

		updateChickenInfoIfNeeded();
		updateTimerIfNeeded();
		spawnChickenDropIfNeeded();
		updateProgress();
		this.skipNextTimerReset = false;
		notifyBlockStateChanged();
	}

	public void willNeedToUpdateChickenInfo() {
		this.mightNeedToUpdateChickenInfo = true;
	}

	private void updateChickenInfoIfNeeded() {
		if (!this.mightNeedToUpdateChickenInfo) {
			return;
		}

		this.fullOfChickens = isFullOfChickens();
		this.fullOfSeeds = isFullOfSeeds();
		this.mightNeedToUpdateChickenInfo = false;
	}

	private void updateChickenInfoIfNeededForSlot(int slot) {
		DataChicken oldChicken = this.chickenData[slot];
		DataChicken newChicken = createChickenData(slot);

		boolean wasCreated = oldChicken == null && newChicken != null;
		boolean wasDeleted = oldChicken != null && newChicken == null;
		boolean wasChanged = oldChicken != null && newChicken != null && !oldChicken.isEqual(newChicken);

		if (wasCreated || wasChanged || wasDeleted) {
			this.chickenData[slot] = newChicken;
			if (!this.skipNextTimerReset) {
				resetTimer();
			}
		}
	}

	protected DataChicken getChickenData(int slot) {
		if (slot < 0 || slot >= this.chickenData.length) {
			return null;
		}
		return this.chickenData[slot];
	}

	protected DataChicken createChickenData(int slot) {
		return DataChicken.getDataFromStack(getStack(slot));
	}

	private void updateTimerIfNeeded() {
		if (this.fullOfChickens && this.fullOfSeeds && !outputIsFull()) {
			this.timeElapsed += getTimeElapsed();
			markDirty();
		}
	}

	private void updateProgress() {
		this.progress = this.timeUntilNextDrop == 0 ? 0 : (this.timeElapsed * 1000 / this.timeUntilNextDrop);
	}

	private int getTimeElapsed() {
		int time = Integer.MAX_VALUE;
		for (int i = 0; i < this.chickenData.length; i++) {
			if (this.chickenData[i] == null) {
				return 0;
			}
			time = Math.min(time, this.chickenData[i].getAddedTime(getStack(i)));
		}
		return time;
	}

	public double getProgress() {
		return this.progress / 1000.0d;
	}

	public String getFormattedProgress() {
		return FORMATTER.format(getProgress());
	}

	private void spawnChickenDropIfNeeded() {
		if (this.fullOfChickens && this.fullOfSeeds && this.timeElapsed >= this.timeUntilNextDrop) {
			if (this.timeUntilNextDrop > 0) {
				removeStack(this.chickenSlots, requiredSeedsForDrop());
				spawnChickenDrop();
			}
			resetTimer();
		}
	}

	private void resetTimer() {
		this.timeElapsed = 0;
		this.timeUntilNextDrop = 0;
		for (int i = 0; i < this.chickenData.length; i++) {
			if (this.chickenData[i] != null) {
				this.timeUntilNextDrop = Math.max(this.timeUntilNextDrop, this.chickenData[i].getLayTime());
				this.timeUntilNextDrop /= speedMultiplier();
			}
		}
		markDirty();
	}

	protected abstract void spawnChickenDrop();

	protected abstract int requiredSeedsForDrop();

	protected abstract double speedMultiplier();

	protected abstract Text getContainerName();

	protected int getChickenSlots() {
		return this.chickenSlots;
	}

	protected int getOutputStackIndex() {
		return this.chickenSlots + (requiredSeedsForDrop() > 0 ? 1 : 0);
	}

	public boolean isFullOfChickens() {
		for (int i = 0; i < this.chickenData.length; i++) {
			updateChickenInfoIfNeededForSlot(i);
			if (this.chickenData[i] == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isFullOfSeeds() {
		int needed = requiredSeedsForDrop();
		if (needed == 0) {
			return true;
		}
		return getStack(this.chickenSlots).getCount() >= needed;
	}

	protected boolean isFullOfChickensCached() {
		return this.fullOfChickens;
	}

	protected boolean isFullOfSeedsCached() {
		return this.fullOfSeeds;
	}

	private boolean outputIsFull() {
		for (int i = getOutputStackIndex(); i < this.inventorySize; i++) {
			ItemStack stack = getStack(i);
			if (stack.getCount() < stack.getMaxCount()) {
				return false;
			}
		}
		return true;
	}

	protected ItemStack putStackInOutput(ItemStack stack) {
		for (int i = getOutputStackIndex(); i < this.inventorySize && !stack.isEmpty(); i++) {
			stack = insertStack(stack, i);
		}
		markDirty();
		return stack;
	}

	private ItemStack insertStack(ItemStack stack, int index) {
		int max = Math.min(stack.getMaxCount(), getMaxCountPerStack());
		ItemStack outputStack = getStack(index);

		if (outputStack.isEmpty()) {
			if (stack.getCount() >= max) {
				setStack(index, stack);
				stack = ItemStack.EMPTY;
			} else {
				setStack(index, stack.split(max));
			}
		} else if (canCombine(outputStack, stack)) {
			if (outputStack.getCount() < max) {
				int itemsToMove = Math.min(stack.getCount(), max - outputStack.getCount());
				stack.decrement(itemsToMove);
				outputStack.increment(itemsToMove);
			}
		}

		return stack;
	}

	private boolean canCombine(ItemStack a, ItemStack b) {
		if (a.getItem() != b.getItem()) {
			return false;
		}
		if (a.getCount() > a.getMaxCount()) {
			return false;
		}
		return ItemStack.areItemsEqual(a, b);
	}

	public static boolean isSeed(ItemStack stack) {
		return stack.isOf(Items.WHEAT_SEEDS)
				|| stack.isOf(Items.MELON_SEEDS)
				|| stack.isOf(Items.PUMPKIN_SEEDS)
				|| stack.isOf(Items.BEETROOT_SEEDS);
	}

	protected void notifyBlockStateChanged() {
		if (this.world == null || this.world.isClient) {
			return;
		}
		BlockState state = this.world.getBlockState(this.pos);
		BlockState newState = applyBlockState(state);
		if (newState != state) {
			this.world.setBlockState(this.pos, newState, net.minecraft.block.Block.NOTIFY_LISTENERS);
		}
	}

	protected BlockState applyBlockState(BlockState state) {
		return state;
	}

	// Inventory

	@Override
	public int size() {
		return this.inventorySize;
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
		if (slot < getOutputStackIndex()) {
			willNeedToUpdateChickenInfo();
		}
		ItemStack stack = this.inventory.get(slot);
		this.inventory.set(slot, ItemStack.EMPTY);
		markDirty();
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (slot < getOutputStackIndex()) {
			willNeedToUpdateChickenInfo();
		}

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
		if (slot < getOutputStackIndex()) {
			willNeedToUpdateChickenInfo();
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
		if (slot < this.chickenSlots) {
			return DataChicken.isChicken(stack);
		}
		if (slot < getOutputStackIndex()) {
			return isSeed(stack);
		}
		return false;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		int[] slots = new int[this.inventorySize];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}
		return slots;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction direction) {
		return isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction direction) {
		return slot >= getOutputStackIndex();
	}

	// Progress exposed to the screen handler

	public PropertyDelegate createPropertyDelegate() {
		return new PropertyDelegate() {
			@Override
			public int get(int index) {
				return index == 0 ? progress : 0;
			}

			@Override
			public void set(int index, int value) {
				if (index == 0) {
					progress = value;
				}
			}

			@Override
			public int size() {
				return 1;
			}
		};
	}

	// Naming and menu

	@Override
	public Text getName() {
		return getContainerName();
	}

	@Override
	public Text getDisplayName() {
		return getName();
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
		return this.pos;
	}

	@Override
	public abstract ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player);

	// Saving

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		clear();
		Inventories.readNbt(nbt, this.inventory, registries);
		this.timeUntilNextDrop = nbt.getInt("TimeUntilNextChild");
		this.timeElapsed = nbt.getInt("TimeElapsed");
		this.skipNextTimerReset = true;
		this.mightNeedToUpdateChickenInfo = true;
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		Inventories.writeNbt(nbt, this.inventory, registries);
		nbt.putInt("TimeUntilNextChild", this.timeUntilNextDrop);
		nbt.putInt("TimeElapsed", this.timeElapsed);
	}

}
