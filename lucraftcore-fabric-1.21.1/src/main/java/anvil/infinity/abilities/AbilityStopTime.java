package anvil.infinity.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

/** Stop time: freezes the day cycle by stepping the world time back a tick. */
public class AbilityStopTime extends Ability {

    public AbilityStopTime(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public String getId() {
        return "stop_time";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.TOGGLE;
    }

    @Override
    protected void onTick() {
        var world = player.getServerWorld();
        world.setTimeOfDay(world.getTimeOfDay() - 1L);
    }
}
