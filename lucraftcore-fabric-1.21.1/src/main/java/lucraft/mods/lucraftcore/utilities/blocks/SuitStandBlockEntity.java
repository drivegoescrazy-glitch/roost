package lucraft.mods.lucraftcore.utilities.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * Armour storage: four armour slots plus three general slots. The worn-suit
 * check only looks at players, so the stand is storage rather than a suit
 * wearer for now.
 */
public class SuitStandBlockEntity extends UtilitiesBlockEntity {

    public SuitStandBlockEntity(BlockPos pos, BlockState state) {
        super(UtilitiesBlocks.SUIT_STAND_ENTITY, pos, state, 9);
    }
}
