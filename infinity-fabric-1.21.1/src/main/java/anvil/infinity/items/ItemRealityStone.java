package anvil.infinity.items;

import anvil.infinity.compat.CompatHandler;
import anvil.infinity.config.InfinityConfig;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;

import java.util.List;

/** Reality Stone: turns the user invisible and changes their size. */
public class ItemRealityStone extends AbstractInfinityStone {

    @Override
    public EnumInfinityStone getType() {
        return EnumInfinityStone.REALITY;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public List<String> getGauntletAbilities() {
        List<String> ids = abilities();
        if (InfinityConfig.sizeChanging)
            ids.add("infinity_size");
        ids.add("infinity_invisibility");
        if (CompatHandler.isSpeedsterHeroes() && InfinityConfig.useSHRealityAbilities) {
            ids.add("speedsterheroes:save_block");
            ids.add("speedsterheroes:change_block");
            ids.add("speedsterheroes:turn_into_bubbles");
        }
        return withAddons(EnumInfinityStone.REALITY, ids);
    }
}
