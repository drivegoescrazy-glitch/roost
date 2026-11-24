package anvil.infinity.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Fast forward: advances the world by 100 ticks every tick while the use key
 * is held, matching the original {@code AbilityHeld} ability.
 */
public class AbilityFastForward extends Ability {

    public AbilityFastForward(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public String getId() {
        return "fast_forward";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.HELD;
    }

    @Override
    protected void onTick() {
        var world = player.getServerWorld();
        world.setTimeOfDay(world.getTimeOfDay() + 100L);
    }
}
