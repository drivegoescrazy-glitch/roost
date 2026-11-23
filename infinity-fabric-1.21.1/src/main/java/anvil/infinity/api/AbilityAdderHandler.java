package anvil.infinity.api;

import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import java.util.ArrayList;
import java.util.List;

/**
 * Addon API: other mods can add abilities to a stone without replacing it,
 * the same hook the original {@code AbilityAdderHandler} offered. Abilities are
 * referenced by the id used in Lucraft's ability registry; unknown ids are
 * skipped by the core, so a missing optional addon never breaks a stone.
 */
public final class AbilityAdderHandler {

    /** Implement this to contribute abilities to one or more stones. */
    public interface IAbilityAdder {

        List<StoneAbility> addStoneAbilities(EnumInfinityStone stone);
    }

    private static final List<IAbilityAdder> ADDERS = new ArrayList<>();

    private AbilityAdderHandler() {
    }

    public static void register(IAbilityAdder adder) {
        ADDERS.add(adder);
    }

    /**
     * Appends the ids contributed for this stone to the given list. Stones are
     * queried without a player in hand, so adders work from the stone alone.
     */
    public static void addAbilities(EnumInfinityStone stone, List<String> ids) {
        for (IAbilityAdder adder : ADDERS) {
            List<StoneAbility> added = adder.addStoneAbilities(stone);
            if (added == null)
                continue;
            for (StoneAbility ability : added)
                if (ability != null && ability.abilityName() != null)
                    ids.add(ability.abilityName());
        }
    }
}
