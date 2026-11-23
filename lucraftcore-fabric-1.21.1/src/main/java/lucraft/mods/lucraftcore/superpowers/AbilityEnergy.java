package lucraft.mods.lucraftcore.superpowers;

import lucraft.mods.lucraftcore.util.energy.EnergyItemStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Energy source for abilities: drains FE from items the player carries
 * (inventory, armour, off hand), the same pool the original mod used through
 * its energy items.
 */
public final class AbilityEnergy {

    private AbilityEnergy() {
    }

    public static int getStored(ServerPlayerEntity player) {
        int stored = 0;
        for (ItemStack stack : energyStacks(player))
            stored += EnergyItemStorage.getStored(stack);
        return stored;
    }

    /** Consumes the cost, returning false when the player cannot pay it. */
    public static boolean consume(ServerPlayerEntity player, int amount) {
        if (amount <= 0)
            return true;
        if (getStored(player) < amount)
            return false;
        int remaining = amount;
        for (ItemStack stack : energyStacks(player)) {
            if (remaining <= 0)
                break;
            EnergyItemStorage storage = new EnergyItemStorage(stack, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            remaining -= storage.extractEnergy(remaining, false);
        }
        return remaining <= 0;
    }

    /** Adds energy to the player's energy items, returning the amount accepted. */
    public static int charge(ServerPlayerEntity player, int amount) {
        if (amount <= 0)
            return 0;
        int accepted = 0;
        for (ItemStack stack : energyStacks(player)) {
            if (accepted >= amount)
                break;
            EnergyItemStorage storage = new EnergyItemStorage(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
            accepted += storage.receiveEnergy(amount - accepted, false);
        }
        return accepted;
    }

    private static List<ItemStack> energyStacks(ServerPlayerEntity player) {
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : player.getInventory().main)
            if (EnergyItemStorage.isEnergyItem(stack))
                stacks.add(stack);
        for (ItemStack stack : player.getInventory().armor)
            if (EnergyItemStorage.isEnergyItem(stack))
                stacks.add(stack);
        for (ItemStack stack : player.getInventory().offHand)
            if (EnergyItemStorage.isEnergyItem(stack))
                stacks.add(stack);
        return stacks;
    }
}
