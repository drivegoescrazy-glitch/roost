package anvil.infinity.abilities;

import anvil.infinity.conditions.ICondition;
import anvil.infinity.registry.InfinityEffects;
import anvil.infinity.items.ItemSoulStone;
import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityTargeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * Kill: dusts the entity the player is looking at. The original asked the
 * client for its crosshair target; the port raycasts on the server with the
 * player's own interaction range, which reaches the same entity.
 */
public class AbilityKill extends Ability {

    private final ICondition<LivingEntity> condition;

    public AbilityKill(ServerPlayerEntity player) {
        this(player, ItemSoulStone::canKill);
    }

    public AbilityKill(ServerPlayerEntity player, ICondition<LivingEntity> condition) {
        super(player);
        this.condition = condition;
    }

    @Override
    public String getId() {
        return "kill";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.ACTION;
    }

    @Override
    protected boolean onUse() {
        if (!condition.isFulfilled(player))
            return false;

        ServerWorld world = player.getServerWorld();
        Vec3d eye = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0F);
        LivingEntity target = AbilityTargeting.findTarget(world, player, eye, direction,
                player.getEntityInteractionRange());
        if (target == null)
            return false;

        var entry = Registries.STATUS_EFFECT.getEntry(InfinityEffects.SNAP);
        if (entry == null)
            return false;
        target.addStatusEffect(new StatusEffectInstance(entry, 1));
        return true;
    }
}
