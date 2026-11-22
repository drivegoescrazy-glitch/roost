package com.timwoodcreates.roost.block.entity;

import com.timwoodcreates.roost.RoostBlockEntities;
import com.timwoodcreates.roost.RoostConfig;
import com.timwoodcreates.roost.RoostTextures;
import com.timwoodcreates.roost.block.BlockRoost;
import com.timwoodcreates.roost.data.DataChicken;
import com.timwoodcreates.roost.screen.RoostScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class RoostBlockEntity extends ChickenContainerBlockEntity {
	private static final int CHICKEN_SLOT = 0;
	private static final int MAX_CHICKENS = 16;

	public RoostBlockEntity(BlockPos pos, BlockState state) {
		super(RoostBlockEntities.ROOST, pos, state, 5, 1);
	}

	@Override
	protected void spawnChickenDrop() {
		DataChicken chicken = getChickenData(CHICKEN_SLOT);
		if (chicken != null) {
			putStackInOutput(chicken.createDropStack());
		}
	}

	public DataChicken createChickenData() {
		return createChickenData(CHICKEN_SLOT);
	}

	public int getChickenModelIndex() {
		DataChicken chicken = createChickenData();
		return chicken == null ? 0 : RoostTextures.modelIndex(chicken.getTextureName());
	}

	public boolean putChickenIn(ItemStack newStack) {
		if (!isValid(CHICKEN_SLOT, newStack)) {
			return false;
		}

		ItemStack oldStack = getStack(CHICKEN_SLOT);

		if (oldStack.isEmpty()) {
			setStack(CHICKEN_SLOT, newStack.split(MAX_CHICKENS));
			markDirty();
			playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM);
			return true;
		}

		if (ItemStack.areItemsEqual(oldStack, newStack)) {
			int itemsAfterAdding = Math.min(oldStack.getCount() + newStack.getCount(), MAX_CHICKENS);
			int itemsToAdd = itemsAfterAdding - oldStack.getCount();
			if (itemsToAdd > 0) {
				newStack.decrement(itemsToAdd);
				oldStack.increment(itemsToAdd);
				markDirty();
				playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM);
				return true;
			}
		}

		return false;
	}

	public boolean pullChickenOut(PlayerEntity player) {
		ItemStack spawnStack = getStack(CHICKEN_SLOT);

		if (spawnStack.isEmpty()) {
			return false;
		}

		player.getInventory().insertStack(spawnStack);
		setStack(CHICKEN_SLOT, ItemStack.EMPTY);
		markDirty();
		playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM);

		return true;
	}

	private void playSound(net.minecraft.sound.SoundEvent soundEvent) {
		if (this.world != null) {
			this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
		}
	}

	@Override
	protected int requiredSeedsForDrop() {
		return 0;
	}

	@Override
	protected double speedMultiplier() {
		return RoostConfig.get().roostSpeed;
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.roost.roost");
	}

	@Override
	protected BlockState applyBlockState(BlockState state) {
		return state.with(BlockRoost.CHICKEN, getChickenModelIndex());
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new RoostScreenHandler(syncId, playerInventory, this, createPropertyDelegate());
	}
}
