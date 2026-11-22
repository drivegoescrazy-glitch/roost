package com.setycz.chickens.block.entity;

import com.setycz.chickens.ChickenBlockEntities;
import com.setycz.chickens.screen.HenhouseScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The henhouse: 9 output slots, a hay bale fuel slot and a dirt byproduct slot.
 * Chickens within five blocks push their lay items into it, spending one hay
 * bale of energy per 100 items and producing one dirt per spent hay bale.
 */
public class HenhouseBlockEntity extends BlockEntity implements Inventory, ExtendedScreenHandlerFactory<BlockPos> {
	public static final int HAY_SLOT = 0;
	public static final int DIRT_SLOT = 1;
	public static final int FIRST_OUTPUT_SLOT = 2;
	public static final int LAST_OUTPUT_SLOT = 10;
	public static final int SLOT_COUNT = 11;

	public static final int HAY_BALE_ENERGY = 100;
	public static final int SEARCH_RADIUS = 5;

	private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
	private int energy;

	public HenhouseBlockEntity(BlockPos pos, BlockState state) {
		super(ChickenBlockEntities.HENHOUSE, pos, state);
	}

	public static ItemStack pushItemStack(ItemStack stack, World world, BlockPos origin) {
		if (world.isClient() || stack.isEmpty()) {
			return stack;
		}

		List<HenhouseBlockEntity> henhouses = findHenhouses(world, origin);
		for (HenhouseBlockEntity henhouse : henhouses) {
			stack = henhouse.pushItemStack(stack);
			if (stack.isEmpty()) {
				break;
			}
		}
		return stack;
	}

	private static List<HenhouseBlockEntity> findHenhouses(World world, BlockPos origin) {
		List<HenhouseBlockEntity> found = new ArrayList<>();
		for (BlockPos pos : BlockPos.iterate(origin.add(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
				origin.add(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
			if (world.getBlockEntity(pos) instanceof HenhouseBlockEntity henhouse) {
				found.add(henhouse);
			}
		}
		found.sort(Comparator.comparingDouble(henhouse -> henhouse.getPos().getSquaredDistance(origin)));
		return found;
	}

	private ItemStack pushItemStack(ItemStack stack) {
		int capacity = this.getEffectiveCapacity();
		if (capacity <= 0) {
			return stack;
		}

		for (int slot = FIRST_OUTPUT_SLOT; slot <= LAST_OUTPUT_SLOT && !stack.isEmpty() && capacity > 0; slot++) {
			int before = stack.getCount();
			ItemStack simulated = this.insert(slot, stack, true);
			int moved = before - simulated.getCount();
			if (moved > 0) {
				capacity -= moved;
				stack = this.insert(slot, stack, false);
				this.consumeEnergy(moved);
			}
		}

		this.markDirty();
		return stack;
	}

	private ItemStack insert(int slot, ItemStack stack, boolean simulate) {
		ItemStack existing = this.items.get(slot);
		if (existing.isEmpty()) {
			if (!simulate) {
				this.items.set(slot, stack.copy());
			}
			return ItemStack.EMPTY;
		}
		if (!ItemStack.areItemsAndComponentsEqual(existing, stack)) {
			return stack;
		}
		int space = Math.min(this.getMaxCountPerStack(), existing.getMaxCount()) - existing.getCount();
		int moved = Math.min(space, stack.getCount());
		if (moved <= 0) {
			return stack;
		}
		if (!simulate) {
			existing.increment(moved);
			this.markDirty();
		}
		ItemStack remainder = stack.copy();
		remainder.decrement(moved);
		return remainder;
	}

	private void consumeEnergy(int amount) {
		while (amount > 0) {
			if (this.energy == 0) {
				ItemStack hay = this.items.get(HAY_SLOT);
				if (hay.isEmpty() || !hay.isOf(Items.HAY_BLOCK)) {
					return;
				}
				hay.decrement(1);
				this.energy += HAY_BALE_ENERGY;
			}
			int consumed = Math.min(amount, this.energy);
			this.energy -= consumed;
			amount -= consumed;
			if (this.energy <= 0) {
				this.addDirt();
			}
		}
		this.markDirty();
	}

	private void addDirt() {
		ItemStack dirt = this.items.get(DIRT_SLOT);
		if (dirt.isEmpty()) {
			this.items.set(DIRT_SLOT, new ItemStack(Items.DIRT));
		} else if (dirt.isOf(Items.DIRT) && dirt.getCount() < dirt.getMaxCount()) {
			dirt.increment(1);
		}
		this.markDirty();
	}

	private int getInputCapacity() {
		int potential = this.energy;
		ItemStack hay = this.items.get(HAY_SLOT);
		if (!hay.isEmpty() && hay.isOf(Items.HAY_BLOCK)) {
			potential += hay.getCount() * HAY_BALE_ENERGY;
		}
		return potential;
	}

	private int getOutputCapacity() {
		ItemStack dirt = this.items.get(DIRT_SLOT);
		if (dirt.isEmpty()) {
			return 64 * HAY_BALE_ENERGY;
		}
		if (!dirt.isOf(Items.DIRT)) {
			return 0;
		}
		return (dirt.getMaxCount() - dirt.getCount()) * HAY_BALE_ENERGY;
	}

	private int getEffectiveCapacity() {
		return Math.min(this.getInputCapacity(), this.getOutputCapacity());
	}

	public int getEnergy() {
		return this.energy;
	}

	public PropertyDelegate createPropertyDelegate() {
		return new PropertyDelegate() {
			@Override
			public int get(int index) {
				return HenhouseBlockEntity.this.energy;
			}

			@Override
			public void set(int index, int value) {
				HenhouseBlockEntity.this.energy = value;
			}

			@Override
			public int size() {
				return 1;
			}
		};
	}

	public void dropContents(World world, BlockPos pos) {
		for (int slot = 0; slot < this.size(); slot++) {
			ItemStack stack = this.getStack(slot);
			if (!stack.isEmpty()) {
				net.minecraft.util.ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				this.setStack(slot, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public int size() {
		return SLOT_COUNT;
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.items.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack result = Inventories.splitStack(this.items, slot, amount);
		if (!result.isEmpty()) {
			this.markDirty();
		}
		return result;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.items, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.items.set(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}
		this.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.world != null && this.world.getBlockEntity(this.pos) == this
				&& player.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clear() {
		this.items.clear();
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		this.items.clear();
		Inventories.readNbt(nbt, this.items, registries);
		this.energy = nbt.getInt("energy");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		Inventories.writeNbt(nbt, this.items, registries);
		nbt.putInt("energy", this.energy);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("container.henhouse");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new HenhouseScreenHandler(syncId, playerInventory, this, this.createPropertyDelegate());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
		return this.pos;
	}

	public Direction getFacing() {
		return this.getCachedState().get(net.minecraft.block.HorizontalFacingBlock.FACING);
	}
}
