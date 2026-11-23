package lucraft.mods.lucraftcore.utilities.blocks;

import com.mojang.serialization.MapCodec;
import lucraft.mods.lucraftcore.utilities.recipes.InstructionRecipe;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.world.World;

/**
 * Instruction-driven crafting station. Right-click with an instruction item to
 * craft the first affordable recipe; sneak-right-click lists the recipes.
 */
public class ConstructionTableBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public ConstructionTableBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(ConstructionTableBlock::new);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConstructionTableBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof ConstructionTableBlockEntity table))
            return ActionResult.PASS;
        ItemStack held = player.getMainHandStack();
        if (table.getRecipes(held).isEmpty()) {
            player.sendMessage(Text.translatable("lucraftcore.info.noinstructionrecipes"), true);
            return ActionResult.SUCCESS;
        }
        if (player.isSneaking()) {
            for (InstructionRecipe recipe : table.getRecipes(held))
                player.sendMessage(recipe.getOutput().getName().copy(), false);
            return ActionResult.SUCCESS;
        }
        if (table.craftFrom(player, held)) {
            player.sendMessage(Text.translatable("lucraftcore.info.crafted"), true);
            player.swingHand(Hand.MAIN_HAND);
        } else {
            player.sendMessage(Text.translatable("lucraftcore.info.missingrequirements"), true);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, net.minecraft.util.BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !state.isOf(newState.getBlock())
                && world.getBlockEntity(pos) instanceof UtilitiesBlockEntity machine)
            ItemScatterer.spawn(world, pos, machine);
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
