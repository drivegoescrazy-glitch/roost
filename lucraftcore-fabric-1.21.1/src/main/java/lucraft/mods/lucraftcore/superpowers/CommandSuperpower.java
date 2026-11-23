package lucraft.mods.lucraftcore.superpowers;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/** Player and operator commands for data-driven superpowers. */
public final class CommandSuperpower {

    private CommandSuperpower() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
                dispatcher.register(CommandManager.literal("superpower")
                        .then(CommandManager.literal("list").executes(CommandSuperpower::list))
                        .then(CommandManager.literal("info")
                                .then(CommandManager.argument("power", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(
                                                SuperpowerRegistry.getAll().stream()
                                                        .map(power -> power.getId().toString()).toList(), builder))
                                        .executes(CommandSuperpower::info)))
                        .then(CommandManager.literal("set")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("power", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> CommandSource.suggestMatching(
                                                        SuperpowerRegistry.getAll().stream()
                                                                .map(power -> power.getId().toString()).toList(), builder))
                                                .executes(CommandSuperpower::set))))
                        .then(CommandManager.literal("remove")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(CommandSuperpower::remove)))
                        .then(CommandManager.literal("xp")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                                .executes(CommandSuperpower::addXp))))));
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        List<Superpower> powers = List.copyOf(SuperpowerRegistry.getAll());
        context.getSource().sendFeedback(() -> Text.literal("[Lucraft] " + powers.size() + " superpower(s)"), false);
        for (Superpower power : powers)
            context.getSource().sendFeedback(() -> Text.literal(" - " + power.getId()
                    + ": " + power.getName().getString() + " (" + power.getMaxLevel() + " level(s), "
                    + power.getAbilities().size() + " ability config(s))"), false);
        return powers.size();
    }

    private static int info(CommandContext<ServerCommandSource> context) {
        Identifier id = Identifier.tryParse(StringArgumentType.getString(context, "power"));
        Superpower power = SuperpowerRegistry.get(id);
        if (power == null) {
            context.getSource().sendError(Text.literal("Unknown superpower " + id));
            return 0;
        }
        context.getSource().sendFeedback(() -> Text.literal(power.getId() + " — " + power.getName().getString()
                + " — " + power.getDescription().getString()), false);
        int[] thresholds = power.getLevelXp();
        StringBuilder levels = new StringBuilder();
        for (int index = 0; index < thresholds.length; index++)
            levels.append(index == 0 ? "" : ", ").append("L").append(index + 1).append("@").append(thresholds[index]).append("xp");
        String levelLine = levels.toString();
        context.getSource().sendFeedback(() -> Text.literal("levels: " + levelLine), false);
        for (AbilityConfig config : power.getAbilities())
            context.getSource().sendFeedback(() -> Text.literal("  ability " + config.abilityId()
                    + " (level " + config.level() + ", energy " + config.energyCost()
                    + ", cooldown " + config.cooldown()
                    + (config.conditions().isEmpty() ? "" : ", conditions " + config.conditions().size()) + ")"), false);
        return 1;
    }

    private static int set(CommandContext<ServerCommandSource> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Identifier id = Identifier.tryParse(StringArgumentType.getString(context, "power"));
        Superpower power = SuperpowerRegistry.get(id);
        if (power == null) {
            context.getSource().sendError(Text.literal("Unknown superpower " + id));
            return 0;
        }
        int count = 0;
        for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            SuperpowerHandler.setPower(player, power);
            count++;
        }
        int applied = count;
        context.getSource().sendFeedback(() -> Text.literal("Set " + power.getId() + " on " + applied + " player(s)"), true);
        return count;
    }

    private static int remove(CommandContext<ServerCommandSource> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        int count = 0;
        for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            SuperpowerHandler.removePower(player);
            count++;
        }
        int removed = count;
        context.getSource().sendFeedback(() -> Text.literal("Removed superpower from " + removed + " player(s)"), true);
        return count;
    }

    private static int addXp(CommandContext<ServerCommandSource> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        int levels = 0;
        int players = 0;
        for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            levels += SuperpowerHandler.addXp(player, amount);
            players++;
        }
        int gained = levels;
        int affected = players;
        context.getSource().sendFeedback(() -> Text.literal("Gave " + amount + " xp to " + affected
                + " player(s), " + gained + " level(s) gained"), true);
        return levels;
    }
}
