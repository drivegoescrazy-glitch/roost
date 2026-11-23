package anvil.infinity.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Locate: the original ability did nothing on its own; having it lets
 * {@code /generatedStones} print the coordinates of the generated stones.
 */
public class AbilityLocate extends Ability {

    public static final String ID = "locate";

    public AbilityLocate(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public AbilityType getType() {
        return AbilityType.CONSTANT;
    }
}
