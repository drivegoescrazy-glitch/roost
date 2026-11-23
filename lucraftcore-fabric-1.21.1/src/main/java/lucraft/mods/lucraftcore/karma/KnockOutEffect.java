package lucraft.mods.lucraftcore.karma;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class KnockOutEffect extends StatusEffect {

    public KnockOutEffect() {
        super(StatusEffectCategory.HARMFUL, 0x4d4d4d);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setVelocity(0, 0, 0);
        entity.velocityModified = true;
        entity.fallDistance = 0;

        StatusEffectInstance instance = entity.getStatusEffect(Registries.STATUS_EFFECT.getEntry(this));
        if (instance != null && instance.getDuration() <= 1 && entity instanceof ServerPlayerEntity player) {
            BlockPos spawn = player.getServerWorld().getSpawnPos();
            player.requestTeleport(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D);
        }
        return true;
    }
}
