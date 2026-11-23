package lucraft.mods.lucraftcore.infinity.screen;

import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import lucraft.mods.lucraftcore.util.sounds.LCSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;

/**
 * A gauntlet slot that only accepts its own stone type and plays the original
 * equip sound when a stone is placed, like the 1.12 {@code SlotInfinityStone}.
 */
public class SlotInfinityStone extends Slot {

    public final EnumInfinityStone type;
    private final PlayerEntity player;

    public SlotInfinityStone(Inventory inventory, PlayerEntity player, EnumInfinityStone type,
                             int index, int x, int y) {
        super(inventory, index, x, y);
        this.type = type;
        this.player = player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof ItemInfinityStone stone && !stone.isContainer() && stone.getType() == type;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!player.getWorld().isClient && getStack().getItem() instanceof ItemInfinityStone)
            player.getWorld().playSound(null, player.getBlockPos(), LCSounds.INFINITY_STONE_EQUIP,
                    SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
}
