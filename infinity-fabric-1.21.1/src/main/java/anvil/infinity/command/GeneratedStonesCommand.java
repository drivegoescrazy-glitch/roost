package anvil.infinity.command;

import anvil.infinity.abilities.AbilityLocate;
import anvil.infinity.compat.CompatHandler;
import anvil.infinity.config.InfinityConfig;
import anvil.infinity.worldgen.WorldData;
import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.PlayerAbilities;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

/** {@code /generatedStones}: which stones have generated, and where if permitted. */
public final class GeneratedStonesCommand {

    private GeneratedStonesCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("generatedStones")
                        .executes(context -> execute(context.getSource()))));
    }

    public static int execute(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        boolean locate = player != null && hasLocate(player);
        if (player != null && CompatHandler.isHeroesExpansion())
            locate = locate || CompatHandler.heroesExpansionCanLocate(player);

        WorldData data = WorldData.get(source.getWorld());
        MutableText message = Text.literal("Generated: ");
        message.append("\n Space Stone: " + data.space);
        if (locate)
            message.append("\n X: " + data.spacex + "\n Z: " + data.spacez + "\n Dim: " + InfinityConfig.spaceDim);
        message.append("\n Power Stone: " + data.power);
        if (locate)
            message.append("\n X: " + data.powerx + "\n Z: " + data.powerz + "\n Dim: " + InfinityConfig.powerDim);
        message.append("\n Mind Stone: " + data.mind);
        if (locate)
            message.append("\n X: " + data.mindx + "\n Z: " + data.mindz + "\n Dim: " + InfinityConfig.mindDim);
        message.append("\n Soul Stone: " + data.soul);
        if (locate)
            message.append("\n X: " + data.soulx + "\n Z: " + data.soulz + "\n Dim: " + InfinityConfig.soulDim);
        message.append("\n Time Stone: " + data.time);
        if (locate)
            message.append("\n X: " + data.timex + "\n Z: " + data.timez + "\n Dim: " + InfinityConfig.timeDim);
        message.append("\n Reality Stone: " + data.reality);
        if (locate)
            message.append("\n X: " + data.realityx + "\n Z: " + data.realityz
                    + "\n Dim: " + InfinityConfig.realityDim);

        MutableText result = message;
        source.sendFeedback(() -> result, false);
        return 1;
    }

    /** The original checked for the locate ability on the command sender. */
    private static boolean hasLocate(ServerPlayerEntity player) {
        Map<String, Ability> displayed = PlayerAbilities.getDisplayed(player);
        return displayed.containsKey(AbilityLocate.ID);
    }
}
