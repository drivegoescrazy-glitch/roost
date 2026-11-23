package lucraft.mods.lucraftcore.util.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Forge's {@code FluidTank} equivalent. Minecraft 1.21 no longer has
 * {@code FluidStack}, so a tank stores the fluid and its amount directly.
 * Amounts are in millibuckets (1000 mB = one bucket).
 */
public class FluidTank {

    private Fluid fluid;
    private int amount;
    private final int capacity;
    private Runnable listener = () -> {
    };

    public FluidTank(int capacity) {
        this(Fluids.EMPTY, 0, capacity);
    }

    public FluidTank(Fluid fluid, int amount, int capacity) {
        this.capacity = Math.max(0, capacity);
        this.fluid = normalize(fluid);
        this.amount = Math.max(0, Math.min(this.capacity, amount));
    }

    public FluidTank setListener(Runnable listener) {
        this.listener = listener == null ? () -> {
        } : listener;
        return this;
    }

    public Fluid getFluid() {
        return amount <= 0 ? Fluids.EMPTY : fluid;
    }

    public int getAmount() {
        return amount;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return amount <= 0;
    }

    public boolean isFull() {
        return amount >= capacity;
    }

    public boolean contains(Fluid fluid) {
        return amount > 0 && this.fluid.matchesType(normalize(fluid));
    }

    /** Fills the tank, returning the amount accepted. */
    public int fill(Fluid fluid, int maxAmount, boolean simulate) {
        Fluid incoming = normalize(fluid);
        if (incoming == Fluids.EMPTY || maxAmount <= 0)
            return 0;
        if (amount > 0 && !this.fluid.matchesType(incoming))
            return 0;
        int filled = Math.min(capacity - amount, maxAmount);
        if (!simulate && filled > 0) {
            this.fluid = incoming;
            this.amount += filled;
            listener.run();
        }
        return filled;
    }

    /** Drains up to {@code maxAmount}, returning the amount removed. */
    public int drain(int maxAmount, boolean simulate) {
        int drained = Math.min(amount, Math.max(0, maxAmount));
        if (!simulate && drained > 0) {
            amount -= drained;
            if (amount == 0)
                fluid = Fluids.EMPTY;
            listener.run();
        }
        return drained;
    }

    /** Drains and returns the fluid that was removed. */
    public Fluid drainFluid(int maxAmount, boolean simulate) {
        if (amount <= 0 || maxAmount <= 0)
            return Fluids.EMPTY;
        Fluid drainedFluid = fluid;
        if (drain(maxAmount, simulate) <= 0)
            return Fluids.EMPTY;
        return simulate ? drainedFluid : amount > 0 ? this.fluid : drainedFluid;
    }

    public void setFluid(Fluid fluid) {
        this.fluid = normalize(fluid);
        if (this.fluid == Fluids.EMPTY)
            this.amount = 0;
        else
            this.amount = Math.min(amount, capacity);
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, Math.min(capacity, amount));
        if (this.amount == 0)
            this.fluid = Fluids.EMPTY;
    }

    public void setFluidAmount(Fluid fluid, int amount) {
        this.fluid = normalize(fluid);
        this.amount = Math.max(0, Math.min(capacity, amount));
        if (this.amount == 0)
            this.fluid = Fluids.EMPTY;
    }

    public void clear() {
        this.fluid = Fluids.EMPTY;
        this.amount = 0;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("Fluid", Registries.FLUID.getId(fluid).toString());
        nbt.putInt("Amount", amount);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        Identifier id = Identifier.tryParse(nbt.getString("Fluid"));
        Fluid read = id == null ? Fluids.EMPTY : Registries.FLUID.get(id);
        this.fluid = normalize(read);
        this.amount = Math.max(0, Math.min(capacity, nbt.getInt("Amount")));
        if (this.amount == 0)
            this.fluid = Fluids.EMPTY;
    }

    private static Fluid normalize(Fluid fluid) {
        return fluid == null ? Fluids.EMPTY : fluid;
    }
}
