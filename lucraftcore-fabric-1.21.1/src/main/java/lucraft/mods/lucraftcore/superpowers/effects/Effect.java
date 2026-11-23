package lucraft.mods.lucraftcore.superpowers.effects;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * Cosmetic or state effect attached to a suit or superpower (glow, trail, name
 * change, aura). Effects are shared instances; anything player specific must be
 * keyed by the player's UUID.
 */
public interface Effect {

    String type();

    default void onApply(ServerPlayerEntity player) {
    }

    default void tick(ServerPlayerEntity player, int ticks) {
    }

    default void onRemove(ServerPlayerEntity player) {
    }

    List<lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition> conditions();

    default boolean areConditionsMet(ServerPlayerEntity player) {
        for (lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition condition : conditions())
            if (!condition.test(player))
                return false;
        return true;
    }
}
