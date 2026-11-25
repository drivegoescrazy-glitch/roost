package lucraft.mods.lucraftcore.extendedinventory;

import lucraft.mods.lucraftcore.LucraftCore;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** Persistent extended inventory and its chest-style screen. */
public final class ExtendedInventoryHandler {

    public static final AttachmentType<ExtendedInventoryData> EXTENDED = AttachmentRegistry.createPersistent(
            LucraftCore.id("extended_inventory"), ExtendedInventoryData.CODEC);

    private ExtendedInventoryHandler() {
    }

    /** Forces the attachment type to register before player data is read. */
    public static void registerAttachments() {
        java.util.Objects.requireNonNull(EXTENDED);
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
                LucraftCore.guard("extended inventory tick of " + player.getName().getString(),
                        () -> tick(player));
        });
    }

    public static ExtendedInventoryData get(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(EXTENDED, ExtendedInventoryData::empty);
    }

    public static ItemStack getStack(ServerPlayerEntity player, int slot) {
        return get(player).get(slot);
    }

    public static void setStack(ServerPlayerEntity player, int slot, ItemStack stack) {
        player.setAttached(EXTENDED, get(player).with(slot, stack));
    }

    /** Ticks worn necklaces, mantles and wrist items. */
    public static void tick(ServerPlayerEntity player) {
        for (int slot = 0; slot <= ExtendedInventoryData.SLOT_WRIST; slot++) {
            ItemStack stack = getStack(player, slot);
            if (!stack.isEmpty() && stack.getItem() instanceof IItemExtendedInventory item)
                item.onWornTick(stack, player);
        }
    }

    /** Opens the five extra slots in a vanilla chest-style screen. */
    public static void open(ServerPlayerEntity player) {
        SimpleInventory inventory = createInventory(player);
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) ->
                new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory,
                        inventory, 1), Text.translatable("container.lucraftcore.extended_inventory")));
    }

    /** Cheat-friendly direct access for commands and self tests. */
    public static SimpleInventory createInventory(ServerPlayerEntity player) {
        ExtendedInventoryData data = get(player);
        SimpleInventory inventory = new SimpleInventory(9) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return slot >= 0 && slot < ExtendedInventoryData.SIZE;
            }
        };
        for (int i = 0; i < ExtendedInventoryData.SIZE; i++)
            inventory.setStack(i, data.get(i));
        inventory.addListener(changed -> {
            ExtendedInventoryData current = get(player);
            for (int i = 0; i < ExtendedInventoryData.SIZE; i++)
                current = current.with(i, changed.getStack(i));
            player.setAttached(EXTENDED, current);
        });
        return inventory;
    }

    /** Called when a player equips or removes an extended inventory item. */
    public static void notifyChanged(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        if (oldStack.getItem() instanceof IItemExtendedInventory item)
            item.onUnequipped(oldStack, player);
        if (newStack.getItem() instanceof IItemExtendedInventory item)
            item.onEquipped(newStack, player);
    }
}
