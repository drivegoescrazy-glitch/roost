package lucraft.mods.lucraftcore.superpowers;

import lucraft.mods.lucraftcore.util.network.AbilityBarPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Builds and sends the player's ability bar. */
public final class AbilityBarSync {

    private AbilityBarSync() {
    }

    public static void send(ServerPlayerEntity player) {
        Map<String, Ability> abilities = PlayerAbilities.getDisplayed(player);
        List<AbilityBarPayload.Entry> entries = new ArrayList<>();
        for (Map.Entry<String, Ability> entry : abilities.entrySet()) {
            Ability ability = entry.getValue();
            if (!ability.showInAbilityBar())
                continue;
            entries.add(new AbilityBarPayload.Entry(entry.getKey(), ability.isEnabled(),
                    ability.getCooldown(), ability.getMaxCooldown(),
                    ability.getType() == AbilityType.HELD));
        }
        if (ServerPlayNetworking.canSend(player, AbilityBarPayload.ID))
            ServerPlayNetworking.send(player, AbilityBarPayload.of(entries));
    }
}
