package com.timwoodcreates.roost.block;

import com.timwoodcreates.roost.block.entity.BreederBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockBreeder extends Block implements BlockEntityProvider {
	public static final BooleanProperty IS_BREEDING = BooleanProperty.of("is_breeding");
	public static final BooleanProperty HAS_SEEDS = BooleanProperty.of("has_seeds");

	public BlockBreeder() {
		super(AbstractBlock.Settings.create().strength(2.0F, 5.0F).sounds(BlockSoundGroup.WOOD));
		this.setDefaultState(this.stateManager.getDefaultState()
				.with(IS_BREEDING, false)
				.with(HAS_SEEDS, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(IS_BREEDING, HAS_SEEDS);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BreederBlockEntity(pos, state);
	}

	/**
	 * Mirrors the old isOpaqueCube override: the breeder only counts as opaque while the curtain
	 * is open. Note that the old code returned true for isOpaqueCube when IS_BREEDING was set.
	 */
	@Override
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return !state.get(IS_BREEDING);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}

		if (!(world.getBlockEntity(pos) instanceof BreederBlockEntity breeder)) {
			return ActionResult.PASS;
		}

		player.openHandledScreen(breeder);
		return ActionResult.SUCCESS;
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			ItemScatterer.onStateReplaced(state, newState, world, pos);
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (world.isClient) {
			return null;
		}
		return (tickWorld, tickPos, tickState, blockEntity) -> {
			if (blockEntity instanceof BreederBlockEntity breeder) {
				breeder.tick();
			}
		};
	}
}
