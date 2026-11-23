package anvil.infinity.abilities;

import anvil.infinity.capabilities.GauntletUserInformation;
import anvil.infinity.capabilities.SnapPlayerData;
import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** Change Snap: cycles the selected snap result and shows it above the hotbar. */
public class AbilityChangeSnap extends Ability {

    public AbilityChangeSnap(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public String getId() {
        return "changesnap";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.ACTION;
    }

    @Override
    protected boolean onUse() {
        SnapPlayerData data = GauntletUserInformation.get(player);
        SnapPlayerData changed = data.withSnapResult(data.snapResult().next());
        GauntletUserInformation.set(player, changed);
        player.sendMessage(Text.literal("Selected ability is: " + changed.snapResult()), true);
        return true;
    }
}
