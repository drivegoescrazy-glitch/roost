package anvil.infinity.items;

import anvil.infinity.compat.CompatHandler;
import anvil.infinity.config.InfinityConfig;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;

import java.util.List;

/** Mind Stone: flight and the ability to choose what the snap does. */
public class ItemMindStone extends AbstractInfinityStone {

    @Override
    public EnumInfinityStone getType() {
        return EnumInfinityStone.MIND;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public List<String> getGauntletAbilities() {
        List<String> ids = abilities("infinity_flight", "changesnap");
        if (CompatHandler.isHeroesExpansion() && InfinityConfig.heAbilities)
            ids.add("heroesexpansion:telekinesis");
        return withAddons(EnumInfinityStone.MIND, ids);
    }
}
