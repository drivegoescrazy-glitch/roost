package anvil.infinity.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Fast forward: advances the world by 100 ticks every tick while enabled.
 * The original was a held ability; the ported core's ability bar is press based,
 * so it is a toggle with the same effect.
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
        return AbilityType.TOGGLE;
    }

    @Override
    protected void onTick() {
        var world = player.getServerWorld();
        world.setTimeOfDay(world.getTimeOfDay() + 100L);
    }
}
