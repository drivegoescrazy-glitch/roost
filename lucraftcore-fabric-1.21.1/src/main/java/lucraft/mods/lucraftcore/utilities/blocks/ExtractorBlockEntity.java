package lucraft.mods.lucraftcore.utilities.blocks;

import lucraft.mods.lucraftcore.utilities.recipes.ExtractorRecipe;
import lucraft.mods.lucraftcore.utilities.recipes.MachineRecipes;
import lucraft.mods.lucraftcore.util.energy.EnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Consumes FE to turn an input item into an output item. */
public class ExtractorBlockEntity extends UtilitiesBlockEntity {

    public static final int CAPACITY = 100_000;
    public static final int MAX_TRANSFER = 1_000;

    private final EnergyStorage energy = new EnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    private ExtractorRecipe activeRecipe;
    private int progress;
    private int maxProgress;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(UtilitiesBlocks.EXTRACTOR_ENTITY, pos, state, 9, 2, 3);
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExtractorBlockEntity entity) {
        if (world.isClient)
            return;
        entity.tickServer();
    }

    private void tickServer() {
        if (activeRecipe == null) {
            activeRecipe = findRecipe();
            if (activeRecipe == null)
                return;
            maxProgress = Math.max(1, activeRecipe.time());
            progress = 0;
        }

        if (progress <= 0 && !canInsertOutput(activeRecipe.output())) {
            activeRecipe = null;
            return;
        }

        if (energy.extractEnergy(activeRecipe.energyPerTick(), true) < activeRecipe.energyPerTick()) {
            activeRecipe = null;
            return;
        }

        energy.extractEnergy(activeRecipe.energyPerTick(), false);
        progress++;
        markDirty();

        if (progress >= maxProgress) {
            complete(activeRecipe);
            activeRecipe = null;
            progress = 0;
            maxProgress = 0;
        }
    }

    private ExtractorRecipe findRecipe() {
        for (int slot = 0; slot <= 1; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isEmpty())
                continue;
            ExtractorRecipe recipe = MachineRecipes.findExtractor(stack);
            if (recipe != null && canInsertOutput(recipe.output()))
                return recipe;
        }
        return null;
    }

    private boolean canInsertOutput(ItemStack output) {
        for (int slot = 2; slot <= 3; slot++) {
            ItemStack existing = inventory.getStack(slot);
            if (existing.isEmpty())
                return true;
            if (ItemStack.areItemsAndComponentsEqual(existing, output)
                    && existing.getCount() + output.getCount() <= existing.getMaxCount())
                return true;
        }
        return false;
    }

    private void complete(ExtractorRecipe recipe) {
        for (int slot = 0; slot <= 1; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!recipe.matches(stack))
                continue;
            stack.decrement(recipe.inputCount());
            if (stack.isEmpty())
                inventory.setStack(slot, ItemStack.EMPTY);
            break;
        }
        for (int slot = 2; slot <= 3; slot++) {
            ItemStack existing = inventory.getStack(slot);
            if (existing.isEmpty()) {
                inventory.setStack(slot, recipe.output().copy());
                return;
            }
            if (ItemStack.areItemsAndComponentsEqual(existing, recipe.output())) {
                existing.increment(recipe.output().getCount());
                return;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("Energy", energy.getEnergyStored());
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        energy.setEnergyStored(nbt.getInt("Energy"));
        progress = nbt.getInt("Progress");
        maxProgress = nbt.getInt("MaxProgress");
    }
}
