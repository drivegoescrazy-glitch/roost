package anvil.infinity.helpers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

/** Plays a sound at an entity's position, with the original's distance delay. */
public final class SoundHelper {

    private SoundHelper() {
    }

    public static void playSoundAtEntity(LivingEntity entity, SoundEvent sound) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound,
                SoundCategory.HOSTILE, 5.0F, 0.0F);
    }
}
