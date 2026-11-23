package lucraft.mods.lucraftcore.utilities.blocks;

import lucraft.mods.lucraftcore.util.inventory.StackInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

/** Base for the mod's machines: carries an inventory that is saved to NBT. */
public abstract class UtilitiesBlockEntity extends BlockEntity implements Inventory {

    protected final StackInventory inventory;

    protected UtilitiesBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots, int... outputSlots) {
        this(type, pos, state, new StackInventory(slots, outputSlots));
    }

    protected UtilitiesBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, StackInventory inventory) {
        super(type, pos, state);
        this.inventory = inventory;
        this.inventory.addListener(inv -> this.markDirty());
    }

    public StackInventory getInventory() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.put("Inventory", inventory.toNbtList(registries));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        inventory.readNbtList(nbt.getList("Inventory", 10), registries);
    }

    // Inventory delegation so hoppers and other vanilla automation can use machines.
    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = inventory.removeStack(slot, amount);
        markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = inventory.removeStack(slot);
        markDirty();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
        markDirty();
    }

    @Override
    public int getMaxCountPerStack() {
        return inventory.getMaxCountPerStack();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return inventory.isValid(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }
}
