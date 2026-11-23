package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.Ability;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/** Extra movement speed that only applies while the player is sprinting. */
public class SprintAbility extends Ability {

    private final AbilityConfig config;
    private final Identifier modifierId;
    private boolean applied;

    public SprintAbility(ServerPlayerEntity player, AbilityConfig config) {
        super(player);
        this.config = config;
        this.modifierId = LucraftCore.id("ability_sprint_" + config.abilityId().replace(':', '_'));
    }

    @Override
    public String getId() {
        return config.abilityId();
    }

    @Override
    protected void onTick() {
        if (player.isSprinting()) {
            if (!applied) {
                EntityAttributeInstance instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (instance != null) {
                    instance.removeModifier(modifierId);
                    instance.addTemporaryModifier(new EntityAttributeModifier(modifierId,
                            config.getDouble("amount", 0.3D), EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    applied = true;
                }
            }
        } else {
            removeModifier();
        }
    }

    @Override
    protected void cleanup() {
        removeModifier();
    }

    private void removeModifier() {
        if (!applied)
            return;
        EntityAttributeInstance instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (instance != null)
            instance.removeModifier(modifierId);
        applied = false;
    }
}
