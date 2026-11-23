package lucraft.mods.lucraftcore.util.triggers;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** Registers the mod's advancement criteria. */
public final class LCriteria {

    public static final AbilityUsedCriterion ABILITY_USED = new AbilityUsedCriterion();

    private LCriteria() {
    }

    public static void register() {
        Registry.register(Registries.CRITERION, LucraftCore.id("ability_used"), ABILITY_USED);
        LucraftCore.LOGGER.debug("Registered advancement criterion ability_used");
    }
}
