package lucraft.mods.lucraftcore.utilities.blocks;

import lucraft.mods.lucraftcore.utilities.recipes.BoilerRecipe;
import lucraft.mods.lucraftcore.utilities.recipes.MachineRecipes;
import lucraft.mods.lucraftcore.util.energy.EnergyStorage;
import lucraft.mods.lucraftcore.util.fluids.FluidTank;
import lucraft.mods.lucraftcore.util.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Heats one fluid into another using FE. Tanks hold 5000 mB each. */
public class BoilerBlockEntity extends UtilitiesBlockEntity {

    public static final int TANK_CAPACITY = 5_000;
    public static final int CAPACITY = 50_000;
    public static final int MAX_TRANSFER = 1_000;

    private final FluidTank inputTank = new FluidTank(TANK_CAPACITY);
    private final FluidTank outputTank = new FluidTank(TANK_CAPACITY);
    private final EnergyStorage energy = new EnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    private BoilerRecipe activeRecipe;
    private int progress;
    private int maxProgress;

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(UtilitiesBlocks.BOILER_ENTITY, pos, state, 9);
        inputTank.setListener(this::markDirty);
        outputTank.setListener(this::markDirty);
    }

    public FluidTank getInputTank() {
        return inputTank;
    }

    public FluidTank getOutputTank() {
        return outputTank;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public static void tick(World world, BlockPos pos, BlockState state, BoilerBlockEntity entity) {
        if (world.isClient)
            return;
        entity.tickServer();
    }

    private void tickServer() {
        transferBuckets();

        if (activeRecipe == null) {
            activeRecipe = MachineRecipes.findBoiler(inputTank.getFluid(), inputTank.getAmount());
            if (activeRecipe == null)
                return;
            if (outputTank.fill(activeRecipe.output(), activeRecipe.outputAmount(), true) < activeRecipe.outputAmount()) {
                activeRecipe = null;
                return;
            }
            maxProgress = Math.max(1, activeRecipe.time());
            progress = 0;
        }

        int cost = activeRecipe.energyPerTick();
        if (energy.extractEnergy(cost, true) < cost) {
            activeRecipe = null;
            return;
        }
        energy.extractEnergy(cost, false);
        progress++;
        if (progress >= maxProgress) {
            inputTank.drain(activeRecipe.inputAmount(), false);
            outputTank.fill(activeRecipe.output(), activeRecipe.outputAmount(), false);
            activeRecipe = null;
            progress = 0;
        }
        markDirty();
    }

    /** Moves fluids between buckets in the four container slots and the tanks. */
    private void transferBuckets() {
        for (int slot = 0; slot < 2; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isEmpty() || !FluidUtil.isFilledBucket(stack))
                continue;
            ItemStack result = FluidUtil.emptyBucketIntoTank(stack, inputTank);
            if (!result.isEmpty()) {
                inventory.setStack(slot, result);
                markDirty();
            }
        }
        for (int slot = 2; slot < 4; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isEmpty() || !FluidUtil.isEmptyBucket(stack))
                continue;
            ItemStack result = FluidUtil.fillBucketFromTank(stack, outputTank);
            if (!result.isEmpty()) {
                inventory.setStack(slot, result);
                markDirty();
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.put("InputTank", inputTank.writeNbt(new NbtCompound()));
        nbt.put("OutputTank", outputTank.writeNbt(new NbtCompound()));
        nbt.putInt("Energy", energy.getEnergyStored());
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        inputTank.readNbt(nbt.getCompound("InputTank"));
        outputTank.readNbt(nbt.getCompound("OutputTank"));
        energy.setEnergyStored(nbt.getInt("Energy"));
        progress = nbt.getInt("Progress");
        maxProgress = nbt.getInt("MaxProgress");
    }
}
