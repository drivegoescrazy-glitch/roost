package anvil.infinity.abilities;

import anvil.infinity.snap.SnapHelper;
import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

/** Snap: needs the full gauntlet and kills according to the selected result. */
public class AbilitySnap extends Ability {

    public AbilitySnap(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public String getId() {
        return "snap";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.ACTION;
    }

    @Override
    protected boolean onUse() {
        return SnapHelper.snap(player);
    }
}
