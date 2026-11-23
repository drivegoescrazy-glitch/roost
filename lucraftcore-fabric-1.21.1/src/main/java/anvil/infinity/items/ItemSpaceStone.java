package anvil.infinity.items;

import anvil.infinity.compat.CompatHandler;
import anvil.infinity.config.InfinityConfig;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;

import java.util.List;

/**
 * Space Stone: locating generated stones, plus the HeroesExpansion teleport
 * abilities (portal, grab, forcefield, black hole) when that addon is present.
 */
public class ItemSpaceStone extends AbstractInfinityStone {

    @Override
    public EnumInfinityStone getType() {
        return EnumInfinityStone.SPACE;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public List<String> getGauntletAbilities() {
        List<String> ids = abilities("locate");
        if (CompatHandler.isHeroesExpansion() && InfinityConfig.heAbilities) {
            ids.add("heroesexpansion:portal");
            ids.add("heroesexpansion:grab_entity");
            ids.add("heroesexpansion:forcefield");
            ids.add("heroesexpansion:blackhole");
        }
        return withAddons(EnumInfinityStone.SPACE, ids);
    }
}
