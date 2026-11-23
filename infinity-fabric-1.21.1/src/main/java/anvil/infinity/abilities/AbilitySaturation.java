package anvil.infinity.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

/** Saturation: keeps the wearer fed, like the original constant ability. */
public class AbilitySaturation extends Ability {

    public AbilitySaturation(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public String getId() {
        return "saturation";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.CONSTANT;
    }

    @Override
    protected void onTick() {
        player.getHungerManager().setFoodLevel(20);
    }
}
