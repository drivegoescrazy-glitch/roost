package anvil.infinity.items;

import anvil.infinity.config.InfinityConfig;
import anvil.infinity.helpers.GauntletHelper;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import net.minecraft.entity.LivingEntity;

import java.util.List;

/**
 * Soul Stone: immortality, constant healing and the kill ability. Killing is
 * only possible with Power, Space, Reality and Soul together.
 */
public class ItemSoulStone extends AbstractInfinityStone {

    @Override
    public EnumInfinityStone getType() {
        return EnumInfinityStone.SOUL;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    /** Power + Space + Reality + Soul must all be worn for the kill ability. */
    public static boolean canKill(LivingEntity entity) {
        return GauntletHelper.hasStone(entity, EnumInfinityStone.POWER)
                && GauntletHelper.hasStone(entity, EnumInfinityStone.SPACE)
                && GauntletHelper.hasStone(entity, EnumInfinityStone.REALITY)
                && GauntletHelper.hasStone(entity, EnumInfinityStone.SOUL);
    }

    @Override
    public List<String> getGauntletAbilities() {
        List<String> ids = abilities();
        if (InfinityConfig.soulHealthBoost)
            ids.add("immortality");
        ids.add("infinity_healing");
        ids.add("kill");
        ids.add("snap");
        return withAddons(EnumInfinityStone.SOUL, ids);
    }
}
