package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import lucraft.mods.lucraftcore.util.network.LCNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Punch ability: hits the entity the player is aiming at. Covers the original
 * punch, fire punch, potion punch and set punch through settings
 * ({@code damage}, {@code knockback}, {@code fire_ticks}, {@code potion},
 * {@code duration}, {@code amplifier}).
 */
public class PunchAbility extends Ability {

    private final AbilityConfig config;

    public PunchAbility(ServerPlayerEntity player, AbilityConfig config) {
        super(player);
        this.config = config;
    }

    @Override
    public String getId() {
        return config.abilityId();
    }

    @Override
    public AbilityType getType() {
        return AbilityType.ACTION;
    }

    @Override
    protected boolean onUse() {
        if (!payEnergy())
            return false;

        ServerWorld world = player.getServerWorld();
        double reach = config.getDouble("reach", 3.5D);
        Vec3d eye = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0F);
        LivingEntity target = AbilityTargeting.findTarget(world, player, eye, direction, reach);
        if (target == null)
            return false;

        float damage = (float) config.getDouble("damage", 4.0D);
        target.damage(world.getDamageSources().playerAttack(player), damage);

        double knockback = config.getDouble("knockback", 0.4D);
        if (knockback > 0)
            target.takeKnockback(knockback, -direction.x, -direction.z);

        int fireTicks = config.getInt("fire_ticks", 0);
        if (fireTicks > 0)
            target.setOnFireForTicks(fireTicks);

        String potion = config.getString("potion", null);
        if (potion != null) {
            Identifier effectId = Identifier.tryParse(potion);
            StatusEffect effect = effectId == null ? null : Registries.STATUS_EFFECT.get(effectId);
            if (effect != null) {
                RegistryEntry<StatusEffect> entry = Registries.STATUS_EFFECT.getEntry(effect);
                if (entry != null)
                    target.addStatusEffect(new StatusEffectInstance(entry,
                            config.getInt("duration", 100), config.getInt("amplifier", 0)));
            }
        }

        player.swingHand(Hand.MAIN_HAND);
        LCNetworking.sendSwing(world, player.getId(), player.getPos(), Hand.MAIN_HAND);
        return true;
    }
}
