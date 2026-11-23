package lucraft.mods.lucraftcore.superpowers;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CommandAbility {

    private static final SuggestionProvider<ServerCommandSource> ABILITY_IDS =
            (context, builder) -> CommandSource.suggestMatching(AbilityRegistry.ABILITIES.keySet(), builder);

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
                dispatcher.register(CommandManager.literal("ability")
                        .then(CommandManager.literal("list").executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("Abilities: " + String.join(", ", AbilityRegistry.ABILITIES.keySet())), false);
                            return AbilityRegistry.ABILITIES.size();
                        }))
                        .then(CommandManager.literal("grant")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("ability", StringArgumentType.word())
                                                .suggests(ABILITY_IDS)
                                                .executes(context -> {
                                                    String id = StringArgumentType.getString(context, "ability");
                                                    int count = 0;
                                                    for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                                                        if (PlayerAbilities.grant(player, id))
                                                            count++;
                                                    }
                                                    int granted = count;
                                                    context.getSource().sendFeedback(() -> Text.literal("Granted " + id + " to " + granted + " player(s)"), true);
                                                    return count;
                                                }))))
                        .then(CommandManager.literal("revoke")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("ability", StringArgumentType.word())
                                                .suggests(ABILITY_IDS)
                                                .executes(context -> {
                                                    String id = StringArgumentType.getString(context, "ability");
                                                    int count = 0;
                                                    for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                                                        if (PlayerAbilities.revoke(player, id))
                                                            count++;
                                                    }
                                                    int revoked = count;
                                                    context.getSource().sendFeedback(() -> Text.literal("Revoked " + id + " from " + revoked + " player(s)"), true);
                                                    return count;
                                                }))))
                        .then(CommandManager.literal("use")
                                .then(CommandManager.argument("ability", StringArgumentType.word())
                                        .suggests(ABILITY_IDS)
                                        .executes(context -> use(context.getSource(), context.getSource().getPlayerOrThrow(),
                                                StringArgumentType.getString(context, "ability")))))
                        .then(CommandManager.literal("status")
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                                            context.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + ": " + PlayerAbilities.getGranted(player)), false);
                                            return 1;
                                        })))));
    }

    private static int use(ServerCommandSource source, ServerPlayerEntity player, String id) {
        Ability ability = PlayerAbilities.ability(player, id);
        if (ability == null) {
            source.sendError(Text.literal("You do not have the ability " + id));
            return 0;
        }
        boolean used = ability.use();
        if (used)
            source.sendFeedback(() -> Text.literal(id + " -> " + (ability.isEnabled() ? "enabled" : "disabled")), false);
        return used ? 1 : 0;
    }
}
