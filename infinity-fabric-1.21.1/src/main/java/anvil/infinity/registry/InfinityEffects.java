package anvil.infinity.registry;

import anvil.infinity.Infinity;
import anvil.infinity.effects.EffectSnap;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** Status effect registry (the original's {@code effectSnap}). */
public final class InfinityEffects {

    public static final StatusEffect SNAP = Registry.register(Registries.STATUS_EFFECT,
            Infinity.id("effect_snap"), new EffectSnap());

    private InfinityEffects() {
    }

    public static void register() {
        Infinity.LOGGER.debug("Registered status effect infinity:effect_snap");
    }
}
