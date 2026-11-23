package lucraft.mods.lucraftcore.infinity.items;

import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Six stone slots stored in the gauntlet's data component. */
public class InventoryInfinityGauntlet extends SimpleInventory {

    public static final EnumInfinityStone[] SLOTS = {
            EnumInfinityStone.SOUL,
            EnumInfinityStone.REALITY,
            EnumInfinityStone.SPACE,
            EnumInfinityStone.POWER,
            EnumInfinityStone.MIND,
            EnumInfinityStone.TIME
    };

    /** Chest screens require a multiple of nine slots; the last three stay locked. */
    public static final int SCREEN_SIZE = 9;

    private final ItemStack gauntlet;

    public InventoryInfinityGauntlet(ItemStack gauntlet) {
        super(SCREEN_SIZE);
        this.gauntlet = gauntlet;
        List<ItemStack> stored = ItemInfinityGauntlet.getStones(gauntlet);
        for (int i = 0; i < SLOTS.length; i++)
            super.setStack(i, i < stored.size() ? stored.get(i) : ItemStack.EMPTY);
        for (int i = SLOTS.length; i < SCREEN_SIZE; i++)
            super.setStack(i, ItemStack.EMPTY);
        addListener(inventory -> save());
    }

    public ItemStack getGauntlet() {
        return gauntlet;
    }

    /** Places one stone of the matching type into its slot. */
    public boolean insert(ItemStack stone) {
        if (!(stone.getItem() instanceof ItemInfinityStone infinityStone) || infinityStone.isContainer())
            return false;
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i] != infinityStone.getType())
                continue;
            if (getStack(i).isEmpty()) {
                setStack(i, stone.copyWithCount(1));
                return true;
            }
            return false;
        }
        return false;
    }

    private void save() {
        List<ItemStack> list = new ArrayList<>(SLOTS.length);
        for (int i = 0; i < SLOTS.length; i++)
            list.add(getStack(i));
        gauntlet.set(ItemInfinityGauntlet.STONES, list);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SLOTS.length)
            return false;
        return ItemInfinityGauntlet.isStoneCompatible(SLOTS[slot], stack);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }
}
