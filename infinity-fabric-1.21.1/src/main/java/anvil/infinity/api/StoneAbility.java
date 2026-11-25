package anvil.infinity.api;

import net.minecraft.util.Identifier;

/**
 * One ability contributed to a stone. The original held a live ability
 * instance; the ported core creates abilities per player from registry ids, so
 * an id is all that is needed.
 */
public record StoneAbility(String abilityName) {

    public static StoneAbility of(String abilityId) {
        return new StoneAbility(abilityId);
    }

    public static StoneAbility of(Identifier abilityId) {
        return new StoneAbility(abilityId.toString());
    }
}
