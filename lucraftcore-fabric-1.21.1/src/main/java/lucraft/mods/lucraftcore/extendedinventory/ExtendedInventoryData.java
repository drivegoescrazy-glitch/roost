package lucraft.mods.lucraftcore.extendedinventory;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Five extra equipment slots: necklace, mantle, wrist, left weapon, right weapon. */
public record ExtendedInventoryData(List<ItemStack> items) {

    public static final int SIZE = 5;
    public static final int SLOT_NECKLACE = 0;
    public static final int SLOT_MANTLE = 1;
    public static final int SLOT_WRIST = 2;
    public static final int SLOT_LEFT_WEAPON = 3;
    public static final int SLOT_RIGHT_WEAPON = 4;

    public static final Codec<ExtendedInventoryData> CODEC =
            ItemStack.OPTIONAL_CODEC.listOf().xmap(ExtendedInventoryData::new, ExtendedInventoryData::items);

    public ExtendedInventoryData {
        items = List.copyOf(items);
    }

    public static ExtendedInventoryData empty() {
        return new ExtendedInventoryData(List.of());
    }

    public ItemStack get(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    public ExtendedInventoryData with(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE)
            return this;
        List<ItemStack> copy = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++)
            copy.add(get(i));
        copy.set(slot, stack);
        return new ExtendedInventoryData(copy);
    }
}
