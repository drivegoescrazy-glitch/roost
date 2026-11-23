package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class TogglePotionAbility extends PotionAbility {

    public TogglePotionAbility(ServerPlayerEntity player, String id, RegistryEntry<StatusEffect> effect, int amplifier) {
        super(player, id, effect, amplifier);
    }

    @Override
    public AbilityType getType() {
        return AbilityType.TOGGLE;
    }
}
