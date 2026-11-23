package lucraft.mods.lucraftcore.util.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/** Insertion, extraction and counting helpers for any {@link Inventory}. */
public final class InventoryUtil {

    private InventoryUtil() {
    }

    /**
     * Inserts a stack, merging into existing stacks first and then filling
     * empty slots. Returns the remainder (empty when everything fitted).
     */
    public static ItemStack insert(Inventory inventory, ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty())
            return stack;
        ItemStack remaining = stack.copy();

        for (int pass = 0; pass < 2 && !remaining.isEmpty(); pass++) {
            for (int slot = 0; slot < inventory.size() && !remaining.isEmpty(); slot++) {
                if (!inventory.isValid(slot, remaining))
                    continue;
                ItemStack existing = inventory.getStack(slot);
                if (pass == 0) {
                    if (existing.isEmpty() || !ItemStack.areItemsAndComponentsEqual(existing, remaining))
                        continue;
                    int limit = Math.min(inventory.getMaxCountPerStack(), existing.getMaxCount());
                    int move = Math.min(remaining.getCount(), limit - existing.getCount());
                    if (move <= 0)
                        continue;
                    if (!simulate) {
                        existing.increment(move);
                        inventory.markDirty();
                    }
                    remaining.decrement(move);
                } else {
                    if (!existing.isEmpty())
                        continue;
                    int move = Math.min(remaining.getCount(), inventory.getMaxCount(remaining));
                    if (move <= 0)
                        continue;
                    if (!simulate) {
                        inventory.setStack(slot, remaining.copyWithCount(move));
                        inventory.markDirty();
                    }
                    remaining.decrement(move);
                }
            }
        }
        return remaining;
    }

    /** Removes up to {@code max} matching items and returns them as one stack. */
    public static ItemStack extract(Inventory inventory, Predicate<ItemStack> predicate, int max, boolean simulate) {
        if (inventory == null || max <= 0)
            return ItemStack.EMPTY;
        ItemStack result = ItemStack.EMPTY;
        int remaining = max;
        for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
            ItemStack existing = inventory.getStack(slot);
            if (existing.isEmpty() || !predicate.test(existing))
                continue;
            int take = Math.min(existing.getCount(), remaining);
            if (result.isEmpty())
                result = existing.copyWithCount(take);
            else if (ItemStack.areItemsAndComponentsEqual(result, existing))
                result.increment(take);
            else
                continue;
            remaining -= take;
            if (!simulate) {
                existing.decrement(take);
                if (existing.isEmpty())
                    inventory.setStack(slot, ItemStack.EMPTY);
                inventory.markDirty();
            }
        }
        return result;
    }

    /** Total number of the given stack's item, ignoring components. */
    public static int count(Inventory inventory, ItemStack stack) {
        int count = 0;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack existing = inventory.getStack(slot);
            if (!existing.isEmpty() && ItemStack.areItemsAndComponentsEqual(existing, stack))
                count += existing.getCount();
        }
        return count;
    }

    public static boolean hasSpaceFor(Inventory inventory, ItemStack stack) {
        return insert(inventory, stack, true).isEmpty();
    }

    public static boolean isEmpty(Inventory inventory) {
        for (int slot = 0; slot < inventory.size(); slot++)
            if (!inventory.getStack(slot).isEmpty())
                return false;
        return true;
    }
}
