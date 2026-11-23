package lucraft.mods.lucraftcore.util.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Inventory with optional output slots, used by machines and item-held
 * inventories. Insertion skips output slots; {@link #insert} returns the
 * remainder so callers never lose items.
 */
public class StackInventory extends SimpleInventory {

    private final int[] outputSlots;

    public StackInventory(int size) {
        this(size, new int[0]);
    }

    public StackInventory(int size, int... outputSlots) {
        super(size);
        this.outputSlots = outputSlots == null ? new int[0] : outputSlots.clone();
    }

    public boolean isOutput(int slot) {
        for (int output : outputSlots)
            if (output == slot)
                return true;
        return false;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return !isOutput(slot) && super.isValid(slot, stack);
    }

    /** Inserts a stack into the first available slots and returns the remainder. */
    public ItemStack insert(ItemStack stack) {
        return InventoryUtil.insert(this, stack, false);
    }

    public ItemStack extract(int slot, int amount) {
        return removeStack(slot, amount);
    }

    public int count(Item item) {
        int count = 0;
        for (int i = 0; i < size(); i++)
            if (getStack(i).isOf(item))
                count += getStack(i).getCount();
        return count;
    }

    public ItemStack findFirst(Predicate<ItemStack> predicate) {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty() && predicate.test(stack))
                return stack;
        }
        return ItemStack.EMPTY;
    }

    public interface Predicate<T> {
        boolean test(T value);
    }
}
