package lucraft.mods.lucraftcore.util.energy;

import lucraft.mods.lucraftcore.util.LCComponents;
import net.minecraft.item.ItemStack;

/** Energy buffer backed by the {@code lucraftcore:energy} item component. */
public class EnergyItemStorage extends EnergyStorage {

    private final ItemStack stack;

    public EnergyItemStorage(ItemStack stack, int capacity) {
        this(stack, capacity, capacity, capacity);
    }

    public EnergyItemStorage(ItemStack stack, int capacity, int maxTransfer) {
        this(stack, capacity, maxTransfer, maxTransfer);
    }

    public EnergyItemStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, getStored(stack));
        this.stack = stack;
        setEnergyStored(getStored(stack));
    }

    public static boolean isEnergyItem(ItemStack stack) {
        return !stack.isEmpty() && stack.get(LCComponents.ENERGY) != null;
    }

    public static int getStored(ItemStack stack) {
        if (stack.isEmpty())
            return 0;
        Integer stored = stack.get(LCComponents.ENERGY);
        return stored == null ? 0 : stored;
    }

    public static void setStored(ItemStack stack, int energy) {
        if (!stack.isEmpty())
            stack.set(LCComponents.ENERGY, Math.max(0, energy));
    }

    @Override
    public int getEnergyStored() {
        return getStored(stack);
    }

    @Override
    public void setEnergyStored(int energy) {
        setStored(stack, Math.min(getMaxEnergyStored(), Math.max(0, energy)));
    }
}
