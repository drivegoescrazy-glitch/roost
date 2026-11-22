package com.timwoodcreates.roost.block;

import com.timwoodcreates.roost.RoostTextures;
import com.timwoodcreates.roost.block.entity.RoostBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockRoost extends Block implements BlockEntityProvider {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final IntProperty CHICKEN = IntProperty.of("chicken", 0, RoostTextures.MAX_MODEL_INDEX);

	public BlockRoost() {
		super(AbstractBlock.Settings.create().strength(2.0F, 5.0F).sounds(BlockSoundGroup.WOOD));
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(CHICKEN, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, CHICKEN);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction facing = context.getPlayer() == null
				? Direction.NORTH
				: context.getPlayer().getHorizontalFacing().getOpposite();
		return this.getDefaultState().with(FACING, facing).with(CHICKEN, 0);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new RoostBlockEntity(pos, state);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}

		if (!(world.getBlockEntity(pos) instanceof RoostBlockEntity roost)) {
			return ActionResult.PASS;
		}

		if (player.isSneaking() && roost.pullChickenOut(player)) {
			return ActionResult.SUCCESS;
		}

		player.openHandledScreen(roost);
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
			if (blockEntity instanceof RoostBlockEntity roost) {
				roost.tick();
			}
		};
	}
}
