package anvil.infinity.items;

import anvil.infinity.compat.CompatHandler;
import anvil.infinity.config.InfinityConfig;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Power Stone: unlimited energy, infinite strength, destructive punches and
 * damage resistance, plus the StarTech abilities when that addon is installed.
 */
public class ItemPowerStone extends AbstractInfinityStone {

    @Override
    public EnumInfinityStone getType() {
        return EnumInfinityStone.POWER;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    /** The Power Stone fills the infinity generator instantly. */
    @Override
    public int getEnergyPerTick(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public List<String> getDefaultAbilities() {
        List<String> ids = abilities("infinity_blast", "infinity_strength", "infinity_punch",
                "infinity_resistance");
        if (CompatHandler.isStarTech() && InfinityConfig.stAbilities)
            ids.add("star-tech:power_tendrils");
        return withAddons(EnumInfinityStone.POWER, ids);
    }

    @Override
    public List<String> getGauntletAbilities() {
        List<String> ids = abilities("infinity_blast", "infinity_strength", "infinity_punch",
                "infinity_resistance", "saturation");
        if (CompatHandler.isStarTech() && InfinityConfig.stAbilities) {
            ids.add("star-tech:power_blast");
            ids.add("star-tech:power_impower");
            ids.add("star-tech:power_rocket_burst");
            ids.add("star-tech:power_tendrils");
        }
        return withAddons(EnumInfinityStone.POWER, ids);
    }
}
