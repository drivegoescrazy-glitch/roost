package lucraft.mods.lucraftcore.util.energy;

import lucraft.mods.lucraftcore.util.LCComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Helpers for moving energy between buffers and items. */
public final class EnergyUtil {

    public static final String ENERGY_UNIT = "FE";

    private EnergyUtil() {
    }

    /**
     * Moves up to {@code amount} FE from one buffer to another. Simulates both
     * sides first so no energy is destroyed when the target is full.
     */
    public static int transfer(EnergyStorage from, EnergyStorage to, int amount) {
        if (from == null || to == null || amount <= 0)
            return 0;
        int available = from.extractEnergy(amount, true);
        if (available <= 0)
            return 0;
        int accepted = to.receiveEnergy(available, true);
        if (accepted <= 0)
            return 0;
        from.extractEnergy(accepted, false);
        to.receiveEnergy(accepted, false);
        return accepted;
    }

    /** Charges an item that carries the energy component. */
    public static int chargeItem(EnergyStorage source, ItemStack stack, int amount) {
        if (source == null || !EnergyItemStorage.isEnergyItem(stack) || amount <= 0)
            return 0;
        EnergyItemStorage itemStorage = new EnergyItemStorage(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        int available = source.extractEnergy(amount, true);
        int accepted = itemStorage.receiveEnergy(available, true);
        if (accepted <= 0)
            return 0;
        source.extractEnergy(accepted, false);
        itemStorage.receiveEnergy(accepted, false);
        return accepted;
    }

    /** Discharges an energy item into a buffer. */
    public static int dischargeItem(ItemStack stack, EnergyStorage target, int amount) {
        if (target == null || !EnergyItemStorage.isEnergyItem(stack) || amount <= 0)
            return 0;
        EnergyItemStorage itemStorage = new EnergyItemStorage(stack, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
        int available = itemStorage.extractEnergy(amount, true);
        int accepted = target.receiveEnergy(available, true);
        if (accepted <= 0)
            return 0;
        itemStorage.extractEnergy(accepted, false);
        target.receiveEnergy(accepted, false);
        return accepted;
    }

    public static String format(int energy, int capacity) {
        return energy + " / " + capacity + " " + ENERGY_UNIT;
    }

    public static Text formatted(int energy, int capacity) {
        return Text.literal(format(energy, capacity));
    }

    /** True when the stack can hold energy, used by machine charging slots. */
    public static boolean canHoldEnergy(ItemStack stack) {
        return stack.get(LCComponents.ENERGY) != null;
    }
}
