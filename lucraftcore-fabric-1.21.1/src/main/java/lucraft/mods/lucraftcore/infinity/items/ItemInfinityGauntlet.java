package lucraft.mods.lucraftcore.infinity.items;

import com.mojang.serialization.Codec;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.screen.InfinityGauntletScreenHandler;
import lucraft.mods.lucraftcore.util.sounds.LCSounds;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/** Holds six infinity stones and opens the original stone screen on right-click. */
public class ItemInfinityGauntlet extends ItemIndestructible {

    /** Stone stacks by slot; empty entries are absent from the component. */
    public static final ComponentType<List<ItemStack>> STONES = Registry.register(Registries.DATA_COMPONENT_TYPE,
            LucraftCore.id("infinity_stones"),
            ComponentType.<List<ItemStack>>builder()
                    .codec(Codec.list(ItemStack.OPTIONAL_CODEC))
                    // Without this the gauntlet cannot be sent to the client and
                    // the whole stack fails to sync, which drops the connection
                    // as soon as a player carries one.
                    .packetCodec(PacketCodec.<RegistryByteBuf, List<ItemStack>>of(
                            (stacks, buf) -> {
                                buf.writeVarInt(stacks.size());
                                for (ItemStack stack : stacks)
                                    ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, stack);
                            },
                            buf -> {
                                int size = buf.readVarInt();
                                List<ItemStack> stacks = new ArrayList<>(size);
                                for (int i = 0; i < size; i++)
                                    stacks.add(ItemStack.OPTIONAL_PACKET_CODEC.decode(buf));
                                return stacks;
                            }))
                    .build());

    public ItemInfinityGauntlet() {
        super();
    }

    public static List<ItemStack> getStones(ItemStack gauntlet) {
        return gauntlet.getOrDefault(STONES, List.of());
    }

    public static boolean hasStone(ItemStack gauntlet, EnumInfinityStone stone) {
        for (ItemStack stack : getStones(gauntlet)) {
            if (stack.getItem() instanceof ItemInfinityStone infinityStone
                    && infinityStone.getType() == stone)
                return true;
        }
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack gauntlet = player.getStackInHand(hand);
        if (!(gauntlet.getItem() instanceof ItemInfinityGauntlet))
            return TypedActionResult.pass(gauntlet);

        // The whole interaction is guarded: this runs on the server thread, so a
        // throw here would drop the player out of the world ("Server closed").
        try {
            if (player.isSneaking()) {
                ItemStack other = player.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
                if (!world.isClient && other.getItem() instanceof ItemInfinityStone stone && !stone.isContainer()) {
                    InventoryInfinityGauntlet inventory = new InventoryInfinityGauntlet(gauntlet);
                    if (inventory.insert(other)) {
                        other.decrement(1);
                        world.playSound(null, player.getBlockPos(), LCSounds.INFINITY_STONE_EQUIP,
                                SoundCategory.PLAYERS, 1.0F, 1.0F);
                    } else {
                        player.sendMessage(Text.translatable("lucraftcore.info.gauntlet_slot_taken"), true);
                    }
                }
                return TypedActionResult.success(gauntlet);
            }

            if (!world.isClient) {
                InventoryInfinityGauntlet inventory = new InventoryInfinityGauntlet(gauntlet);
                player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                        (syncId, playerInventory, playerEntity) ->
                                new InfinityGauntletScreenHandler(syncId, playerInventory, inventory),
                        Text.translatable("container.lucraftcore.infinity_gauntlet")));
            }
            return TypedActionResult.success(gauntlet);
        } catch (Throwable t) {
            LucraftCore.reportError("using the infinity gauntlet", t);
            return TypedActionResult.pass(gauntlet);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        List<ItemStack> stones = getStones(stack);
        if (stones.isEmpty()) {
            tooltip.add(Text.translatable("lucraftcore.info.gauntlet_empty").formatted(Formatting.GRAY));
            return;
        }
        for (ItemStack stone : stones) {
            if (stone.getItem() instanceof ItemInfinityStone infinityStone)
                tooltip.add(Text.literal(" - ").append(stone.getName().copy()
                        .formatted(infinityStone.getType().getTextColor())).formatted(Formatting.GRAY));
        }
    }

    /** The gauntlet is indestructible, so it never takes damage. */
    public static boolean isStoneCompatible(EnumInfinityStone slot, ItemStack stack) {
        if (!(stack.getItem() instanceof ItemInfinityStone stone) || stone.isContainer())
            return false;
        return stone.getType() == slot;
    }

    /** Small holder used by lookups that do not need a full screen. */
    public static List<ItemStack> stoneList(SimpleInventory inventory) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.getStack(i).isEmpty())
                list.add(inventory.getStack(i));
        }
        return list;
    }
}
