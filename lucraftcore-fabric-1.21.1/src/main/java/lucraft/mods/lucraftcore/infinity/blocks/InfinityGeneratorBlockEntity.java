package lucraft.mods.lucraftcore.infinity.blocks;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import lucraft.mods.lucraftcore.util.energy.EnergyBlock;
import lucraft.mods.lucraftcore.util.energy.EnergyStorage;
import lucraft.mods.lucraftcore.util.energy.EnergyUtil;
import lucraft.mods.lucraftcore.util.inventory.StackInventory;
import lucraft.mods.lucraftcore.utilities.blocks.UtilitiesBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/** Generates FE from a contained infinity stone and pushes it to neighbours. */
public class InfinityGeneratorBlockEntity extends UtilitiesBlockEntity implements EnergyBlock {

    public static final int CAPACITY = 2_000_000;
    public static final int MAX_TRANSFER = 100_000;

    private final EnergyStorage energy = new EnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER);

    public InfinityGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModuleInfinity.INFINITY_GENERATOR_ENTITY, pos, state, new StackInventory(9) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return slot == 0 && stack.getItem() instanceof ItemInfinityStone;
            }
        });
    }

    @Override
    public EnergyStorage getEnergy() {
        return energy;
    }

    public static void tick(World world, BlockPos pos, BlockState state, InfinityGeneratorBlockEntity entity) {
        if (!world.isClient)
            LucraftCore.guard("infinity generator tick", () -> entity.tickServer(world));
    }

    private void tickServer(World world) {
        boolean changed = false;
        ItemStack stoneStack = inventory.getStack(0);
        if (energy.getEnergyStored() < CAPACITY && stoneStack.getItem() instanceof ItemInfinityStone stone) {
            int accepted = energy.receiveEnergy(stone.getEnergyPerTick(stoneStack), false);
            changed |= accepted > 0;
        }

        if (energy.getEnergyStored() > 0) {
            int budget = Math.min(energy.getEnergyStored(), MAX_TRANSFER);
            for (Direction direction : Direction.values()) {
                if (budget <= 0)
                    break;
                if (!(world.getBlockEntity(pos.offset(direction)) instanceof EnergyBlock target))
                    continue;
                int moved = EnergyUtil.transfer(energy, target.getEnergy(), budget);
                budget -= moved;
                changed |= moved > 0;
            }
        }

        if (changed)
            markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        energy.setEnergyStored(nbt.getInt("Energy"));
    }
}
