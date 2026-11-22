package com.timwoodcreates.roost.block.entity;

import com.timwoodcreates.roost.RoostBlockEntities;
import com.timwoodcreates.roost.RoostConfig;
import com.timwoodcreates.roost.block.BlockBreeder;
import com.timwoodcreates.roost.data.DataChicken;
import com.timwoodcreates.roost.screen.BreederScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BreederBlockEntity extends ChickenContainerBlockEntity {
	private static final double PARTICLE_Y = 0.5d;

	public BreederBlockEntity(BlockPos pos, BlockState state) {
		super(RoostBlockEntities.BREEDER, pos, state, 6, 2);
	}

	@Override
	protected void spawnChickenDrop() {
		DataChicken left = getChickenData(0);
		DataChicken right = getChickenData(1);

		if (left != null && right != null) {
			putStackInOutput(left.createChildStack(right, this.world));
			playSpawnSound();
			spawnParticles();
		}
	}

	private void playSpawnSound() {
		if (this.world != null) {
			this.world.playSound(null, this.pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 0.5f, 0.8f);
		}
	}

	private void spawnParticles() {
		spawnParticle(-0.1d, 0.5d, 0.0d, 0.2d);
		spawnParticle(0.5d, -0.1d, 0.2d, 0.0d);
		spawnParticle(1.1d, 0.5d, 0.0d, 0.2d);
		spawnParticle(0.5d, 1.1d, 0.2d, 0.0d);
	}

	private void spawnParticle(double x, double z, double xOffset, double zOffset) {
		if (this.world instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(ParticleTypes.HEART,
					this.pos.getX() + x, this.pos.getY() + PARTICLE_Y, this.pos.getZ() + z,
					2, xOffset, 0.2d, zOffset, 0.02d);
		}
	}

	@Override
	protected int requiredSeedsForDrop() {
		return 2;
	}

	@Override
	protected double speedMultiplier() {
		return RoostConfig.get().breederSpeed;
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.roost.breeder");
	}

	@Override
	protected BlockState applyBlockState(BlockState state) {
		return state.with(BlockBreeder.IS_BREEDING, isFullOfChickensCached())
				.with(BlockBreeder.HAS_SEEDS, isFullOfSeedsCached());
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new BreederScreenHandler(syncId, playerInventory, this, createPropertyDelegate());
	}
}
