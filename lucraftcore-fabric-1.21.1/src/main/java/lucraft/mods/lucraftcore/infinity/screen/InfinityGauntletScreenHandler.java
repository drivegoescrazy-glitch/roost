package lucraft.mods.lucraftcore.infinity.screen;

import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import lucraft.mods.lucraftcore.infinity.items.InventoryInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * The original gauntlet container: six stone slots at the 1.12 coordinates plus
 * the full player inventory, with the held gauntlet's hotbar slot locked.
 *
 * <p>The client side builds its inventory from its own main-hand stack, which is
 * kept in sync as a data component, so no extra screen data is needed.</p>
 */
public class InfinityGauntletScreenHandler extends ScreenHandler {

    /** Original {@code ContainerInfinityGauntlet} slot positions, in slot order. */
    private static final int[] SLOT_X = {38, 59, 80, 101, 69, 129};
    private static final int[] SLOT_Y = {28, 28, 28, 28, 58, 38};

    private static final int STONE_SLOTS = 6;
    private static final int MAIN_START = 6;
    private static final int MAIN_END = 33;
    private static final int HOTBAR_START = 33;
    private static final int HOTBAR_END = 42;

    private final Inventory inventory;
    private final PlayerEntity player;

    public InfinityGauntletScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory,
                new InventoryInfinityGauntlet(playerInventory.player.getMainHandStack()));
    }

    public InfinityGauntletScreenHandler(int syncId, PlayerInventory playerInventory,
                                         InventoryInfinityGauntlet inventory) {
        super(ModuleInfinity.GAUNTLET_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.player = playerInventory.player;

        for (int i = 0; i < STONE_SLOTS; i++)
            addSlot(new SlotInfinityStone(inventory, player, InventoryInfinityGauntlet.SLOTS[i],
                    i, SLOT_X[i], SLOT_Y[i]));

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 129 + row * 18));

        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 187) {
                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    // The original prevented moving the gauntlet out of its own screen.
                    return getStack() != playerEntity.getMainHandStack();
                }
            });
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        if (index < 0 || index >= slots.size())
            return result;
        Slot slot = slots.get(index);
        if (!slot.hasStack())
            return result;

        ItemStack stack = slot.getStack();
        result = stack.copy();

        if (index < STONE_SLOTS) {
            if (!insertItem(stack, MAIN_START, HOTBAR_END, true))
                return ItemStack.EMPTY;
            slot.onQuickTransfer(stack, result);
        } else if (stack.getItem() instanceof ItemInfinityStone stone && !stone.isContainer()) {
            int target = stoneSlot(stone.getType());
            if (target < 0 || !insertItem(stack, target, target + 1, false))
                return ItemStack.EMPTY;
        } else if (index >= HOTBAR_START && index < HOTBAR_END) {
            if (!insertItem(stack, MAIN_START, MAIN_END, false))
                return ItemStack.EMPTY;
        } else if (!insertItem(stack, HOTBAR_START, HOTBAR_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty())
            slot.setStack(ItemStack.EMPTY);
        else
            slot.markDirty();

        if (stack.getCount() == result.getCount())
            return ItemStack.EMPTY;

        slot.onTakeItem(player, stack);
        return result;
    }

    private static int stoneSlot(lucraft.mods.lucraftcore.infinity.EnumInfinityStone type) {
        for (int i = 0; i < InventoryInfinityGauntlet.SLOTS.length; i++)
            if (InventoryInfinityGauntlet.SLOTS[i] == type)
                return i;
        return -1;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (inventory instanceof InventoryInfinityGauntlet gauntletInventory)
            gauntletInventory.save();
    }
}
