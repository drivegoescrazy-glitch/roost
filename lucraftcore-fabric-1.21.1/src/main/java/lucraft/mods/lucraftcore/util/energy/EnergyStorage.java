package lucraft.mods.lucraftcore.util.energy;

/**
 * Forge's {@code EnergyStorage} equivalent. Energy unit is FE, one unit per
 * operation; values are integers so they can be stored in item components and
 * block entity NBT without loss.
 */
public class EnergyStorage {

    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public EnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = Math.max(0, capacity);
        this.maxReceive = Math.max(0, maxReceive);
        this.maxExtract = Math.max(0, maxExtract);
        // Assign directly: subclasses override setEnergyStored and their fields
        // are not initialised yet while this constructor runs.
        this.energy = Math.max(0, Math.min(this.capacity, energy));
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0)
            return 0;
        int received = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(this.maxReceive, maxReceive));
        if (!simulate && received > 0)
            setEnergyStored(getEnergyStored() + received);
        return Math.max(0, received);
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0)
            return 0;
        int extracted = Math.min(getEnergyStored(), Math.min(this.maxExtract, maxExtract));
        if (!simulate && extracted > 0)
            setEnergyStored(getEnergyStored() - extracted);
        return Math.max(0, extracted);
    }

    public int getEnergyStored() {
        return energy;
    }

    public int getMaxEnergyStored() {
        return capacity;
    }

    public boolean canExtract() {
        return maxExtract > 0;
    }

    public boolean canReceive() {
        return maxReceive > 0;
    }

    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(0, capacity);
        setEnergyStored(energy);
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public boolean isFull() {
        return energy >= capacity;
    }

    public boolean isEmpty() {
        return energy <= 0;
    }
}
