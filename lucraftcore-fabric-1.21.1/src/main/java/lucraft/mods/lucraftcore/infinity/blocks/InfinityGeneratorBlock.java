package lucraft.mods.lucraftcore.infinity.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfinityGeneratorBlock extends BlockWithEntity {

    public InfinityGeneratorBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(InfinityGeneratorBlock::new);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfinityGeneratorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type,
                lucraft.mods.lucraftcore.infinity.ModuleInfinity.INFINITY_GENERATOR_ENTITY, InfinityGeneratorBlockEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof InfinityGeneratorBlockEntity generator) {
            lucraft.mods.lucraftcore.utilities.blocks.UtilitiesBlocks.openMachine(player, generator,
                    Text.translatable("container.lucraftcore.infinity_generator"));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !state.isOf(newState.getBlock())
                && world.getBlockEntity(pos) instanceof InfinityGeneratorBlockEntity generator) {
            for (int i = 0; i < generator.getInventory().size(); i++) {
                ItemStack stack = generator.getInventory().getStack(i);
                if (!stack.isEmpty())
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
