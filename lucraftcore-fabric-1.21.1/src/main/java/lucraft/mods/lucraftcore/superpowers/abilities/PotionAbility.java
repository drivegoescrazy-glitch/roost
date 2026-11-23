package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class PotionAbility extends Ability {

    private final String id;
    private final RegistryEntry<StatusEffect> effect;
    private final int amplifier;

    public PotionAbility(ServerPlayerEntity player, String id, RegistryEntry<StatusEffect> effect, int amplifier) {
        super(player);
        this.id = id;
        this.effect = effect;
        this.amplifier = amplifier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    protected void onTick() {
        if (ticks % 20 == 0)
            player.addStatusEffect(new StatusEffectInstance(effect, 60, amplifier, true, false, false));
    }

    @Override
    protected void onDisabled() {
        player.removeStatusEffect(effect);
    }

    @Override
    protected void cleanup() {
        player.removeStatusEffect(effect);
    }
}
