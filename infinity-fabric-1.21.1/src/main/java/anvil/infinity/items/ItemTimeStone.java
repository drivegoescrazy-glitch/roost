package anvil.infinity.items;

import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;

import java.util.List;

/** Time Stone: fast forward and stop time. */
public class ItemTimeStone extends AbstractInfinityStone {

    @Override
    public EnumInfinityStone getType() {
        return EnumInfinityStone.TIME;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public List<String> getGauntletAbilities() {
        return withAddons(EnumInfinityStone.TIME, abilities("fast_forward", "stop_time"));
    }
}
