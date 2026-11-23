package lucraft.mods.lucraftcore.superpowers;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.effects.EffectHandler;
import lucraft.mods.lucraftcore.superpowers.suitsets.SuitSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ModuleSuperpowers {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerAbilities.tickSuit(player);
                PlayerAbilities.tickPower(player);
                PlayerAbilities.tick(player);
                PlayerAbilities.tickGauntlet(player);

                SuitSet worn = SuitSet.getWornSuit(player);
                EffectHandler.tick(player, "suit", worn == null ? List.of() : worn.getEffects());
                Superpower power = SuperpowerHandler.getPower(player);
                EffectHandler.tick(player, "power", power == null ? List.of() : power.getEffects());

                if (player.age % 20 == 0)
                    AbilityBarSync.send(player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PlayerAbilities.sync(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerAbilities.clear(handler.player);
            EffectHandler.clearAll(handler.player);
        });

        CommandAbility.register();
        CommandSuperpower.register();

        LucraftCore.LOGGER.info("Superpowers module registered: {} abilities", AbilityRegistry.ABILITIES.size());
    }
}
