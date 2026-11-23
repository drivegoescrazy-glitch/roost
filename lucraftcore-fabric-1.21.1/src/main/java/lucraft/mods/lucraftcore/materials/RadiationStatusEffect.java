package lucraft.mods.lucraftcore.materials;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;


public class RadiationStatusEffect extends StatusEffect {

    public RadiationStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x4d4d4d);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int interval = 25 >> amplifier;
        return interval <= 0 || duration % interval == 0;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().magic(), 3.0F);
        return true;
    }
}
