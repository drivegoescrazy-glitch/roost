package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityEnergy;
import net.minecraft.server.network.ServerPlayerEntity;

/** Generates FE into the player's energy items over time. */
public class GeneratorAbility extends Ability {

    private final AbilityConfig config;

    public GeneratorAbility(ServerPlayerEntity player, AbilityConfig config) {
        super(player);
        this.config = config;
    }

    @Override
    public String getId() {
        return config.abilityId();
    }

    @Override
    protected void onTick() {
        int interval = Math.max(1, config.getInt("interval", 20));
        if (ticks % interval == 0)
            AbilityEnergy.charge(player, config.getInt("amount", 20));
    }
}
