package lucraft.mods.lucraftcore.extendedinventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/** Implemented by items that can be worn in the extended inventory. */
public interface IItemExtendedInventory {

    ExtendedInventoryItemType getEIItemType(ItemStack stack);

    default void onWornTick(ItemStack stack, PlayerEntity player) {
    }

    default void onEquipped(ItemStack stack, PlayerEntity player) {
    }

    default void onUnequipped(ItemStack stack, PlayerEntity player) {
    }

    default boolean useButton(ItemStack stack, PlayerEntity player) {
        return false;
    }

    default String getAbilityBarDescription(ItemStack stack, PlayerEntity player) {
        return "";
    }

    default void onPressedButton(ItemStack stack, PlayerEntity player, boolean pressed) {
    }

    default boolean canEquip(ItemStack stack, PlayerEntity player) {
        return true;
    }

    default boolean canUnequip(ItemStack stack, PlayerEntity player) {
        return true;
    }

    enum ExtendedInventoryItemType {
        NECKLACE, MANTLE, WRIST
    }
}
