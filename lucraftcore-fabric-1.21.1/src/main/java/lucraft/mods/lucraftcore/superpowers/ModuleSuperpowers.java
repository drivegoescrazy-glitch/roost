package lucraft.mods.lucraftcore.superpowers;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.extendedinventory.ExtendedInventoryHandler;
import lucraft.mods.lucraftcore.superpowers.effects.EffectHandler;
import lucraft.mods.lucraftcore.superpowers.suitsets.SuitSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ModuleSuperpowers {

    public static void register() {
        // Touch the attachment type during mod initialization. Player data is
        // deserialized before the first join callback, so loading this class
        // lazily from the tick would make Fabric skip the saved ability map
        // with "Unknown attachment type lucraftcore:abilities".
        PlayerAbilities.registerAttachments();
        SuperpowerHandler.registerAttachments();
        ExtendedInventoryHandler.registerAttachments();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                String who = player.getName().getString();

                LucraftCore.guard("ability tick of " + who, () -> {
                    PlayerAbilities.tickSuit(player);
                    PlayerAbilities.tickPower(player);
                    PlayerAbilities.tick(player);
                    PlayerAbilities.tickGauntlet(player);
                });

                LucraftCore.guard("effect tick of " + who, () -> {
                    SuitSet worn = SuitSet.getWornSuit(player);
                    EffectHandler.tick(player, "suit", worn == null ? List.of() : worn.getEffects());
                    Superpower power = SuperpowerHandler.getPower(player);
                    EffectHandler.tick(player, "power", power == null ? List.of() : power.getEffects());
                });

                if (player.age % 20 == 0)
                    LucraftCore.guard("ability bar of " + who, () -> AbilityBarSync.send(player));
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                LucraftCore.guard("player ability sync for " + handler.player.getName().getString(),
                        () -> PlayerAbilities.sync(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            LucraftCore.guard("player ability cleanup for " + handler.player.getName().getString(),
                    () -> PlayerAbilities.clear(handler.player));
            LucraftCore.guard("effect cleanup for " + handler.player.getName().getString(),
                    () -> EffectHandler.clearAll(handler.player));
        });
        // An integrated server is stopped when leaving a world. Cached abilities
        // belong to the old server instance and must not leak into the next one.
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            PlayerAbilities.clearAll();
            EffectHandler.clearAll();
        });

        CommandAbility.register();
        CommandSuperpower.register();

        LucraftCore.LOGGER.info("Superpowers module registered: {} abilities", AbilityRegistry.ABILITIES.size());
    }
}
