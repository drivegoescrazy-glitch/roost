package lucraft.mods.lucraftcore.utilities.blocks;

import lucraft.mods.lucraftcore.util.energy.EnergyItemStorage;
import lucraft.mods.lucraftcore.util.energy.EnergyStorage;
import lucraft.mods.lucraftcore.util.energy.EnergyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Burns furnace fuel into FE and charges energy items in its second slot. */
public class FurnaceGeneratorBlockEntity extends UtilitiesBlockEntity {

    public static final int CAPACITY = 100_000;
    public static final int MAX_TRANSFER = 1_000;
    public static final int ENERGY_PER_TICK = 10;

    private final EnergyStorage energy = new EnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    private int fuelTicks;
    private int totalFuelTicks;

    public FurnaceGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(UtilitiesBlocks.FURNACE_GENERATOR_ENTITY, pos, state, 9);
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public int getFuelTicks() {
        return fuelTicks;
    }

    public int getTotalFuelTicks() {
        return totalFuelTicks;
    }

    public static void tick(World world, BlockPos pos, BlockState state, FurnaceGeneratorBlockEntity entity) {
        if (world.isClient)
            return;
        entity.tickServer();
    }

    private void tickServer() {
        boolean changed = false;
        ItemStack fuelStack = inventory.getStack(0);

        if (fuelTicks <= 0 && !fuelStack.isEmpty() && energy.getEnergyStored() < CAPACITY) {
            int burnTime = FuelValues.burnTime(fuelStack);
            if (burnTime > 0) {
                fuelTicks = burnTime;
                totalFuelTicks = burnTime;
                if (fuelStack.isOf(Items.LAVA_BUCKET))
                    inventory.setStack(0, new ItemStack(Items.BUCKET));
                else
                    fuelStack.decrement(1);
                changed = true;
            }
        }

        if (fuelTicks > 0) {
            fuelTicks--;
            energy.receiveEnergy(ENERGY_PER_TICK, false);
            changed = true;
        }

        ItemStack chargeSlot = inventory.getStack(1);
        if (EnergyItemStorage.isEnergyItem(chargeSlot) && !energy.isEmpty()) {
            if (EnergyUtil.chargeItem(energy, chargeSlot, MAX_TRANSFER) > 0)
                changed = true;
        }

        if (changed)
            markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("Energy", energy.getEnergyStored());
        nbt.putInt("FuelTicks", fuelTicks);
        nbt.putInt("TotalFuelTicks", totalFuelTicks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        energy.setEnergyStored(nbt.getInt("Energy"));
        fuelTicks = nbt.getInt("FuelTicks");
        totalFuelTicks = nbt.getInt("TotalFuelTicks");
    }
}
