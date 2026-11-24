package anvil.infinity.effects;

import anvil.infinity.helpers.SnapMessageHelper;
import anvil.infinity.registry.InfinitySounds;
import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;


/**
 * "Dusted": one tick before the effect runs out the target disintegrates with
 * the snap sound and the dusting message, exactly like the original effect.
 */
public class EffectSnap extends StatusEffect {

    public EffectSnap() {
        super(StatusEffectCategory.HARMFUL, 0x4B0F4B);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // The original checked for a remaining duration of exactly one tick.
        return duration == 1;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        try {
            World world = entity.getWorld();
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), InfinitySounds.SNAP,
                    SoundCategory.HOSTILE, 5.0F, 0.0F);
            if (entity.hasCustomName() || entity instanceof PlayerEntity)
                SnapMessageHelper.deathMessage(entity);
            entity.kill();
            return true;
        } catch (Throwable t) {
            LucraftCore.reportError("applying the snap effect", t);
            return false;
        }
    }

}
