package lucraft.mods.lucraftcore.karma;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.karma.KarmaStat.KarmaClass;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class ModuleKarma {

    public static StatusEffect KNOCKOUT;
    public static RegistryEntry<StatusEffect> KNOCKOUT_ENTRY;

    public static void register() {
        KNOCKOUT = Registry.register(Registries.STATUS_EFFECT, LucraftCore.id("knock_out"), new KnockOutEffect());
        KNOCKOUT_ENTRY = Registries.STATUS_EFFECT.getEntry(KNOCKOUT);

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (source.getAttacker() instanceof ServerPlayerEntity player)
                KarmaHandler.onKill(player, entity);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
                dispatcher.register(CommandManager.literal("karma")
                        .executes(context -> info(context.getSource(), context.getSource().getPlayerOrThrow()))
                        .then(CommandManager.literal("set")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    KarmaHandler.setKarmaStat(target, KarmaStat.MODIFIED, amount);
                                                    context.getSource().sendFeedback(() -> Text.literal("Set karma of " + target.getName().getString() + " to " + KarmaHandler.getKarma(target)), true);
                                                    return KarmaHandler.getKarma(target);
                                                }))))
                        .then(CommandManager.literal("add")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    KarmaHandler.increaseKarmaStat(target, KarmaStat.MODIFIED, amount);
                                                    context.getSource().sendFeedback(() -> Text.literal("Karma of " + target.getName().getString() + " is now " + KarmaHandler.getKarma(target)), true);
                                                    return KarmaHandler.getKarma(target);
                                                }))))
                        .then(CommandManager.literal("knockout")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                                            target.addStatusEffect(new StatusEffectInstance(KNOCKOUT_ENTRY, 600, 0));
                                            context.getSource().sendFeedback(() -> Text.literal("Knocked out " + target.getName().getString()), true);
                                            return 1;
                                        })))
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                .executes(context -> info(context.getSource(), EntityArgumentType.getPlayer(context, "target"))))));

        LucraftCore.LOGGER.info("Karma module registered");
    }

    private static int info(ServerCommandSource source, ServerPlayerEntity player) {
        int karma = KarmaHandler.getKarma(player);
        KarmaClass karmaClass = KarmaHandler.getKarmaClass(player);
        source.sendFeedback(() -> Text.literal(player.getName().getString() + "'s Karma").formatted(Formatting.BOLD), false);
        Map<String, Integer> stats = KarmaHandler.getStats(player);
        for (KarmaStat stat : KarmaStat.ALL) {
            int count = stats.getOrDefault(stat.getName(), 0);
            source.sendFeedback(() -> Text.literal(" " + stat.getName() + ": " + count + " (" + stat.getAmount() + " each = " + (count * stat.getAmount()) + ")"), false);
        }
        source.sendFeedback(() -> Text.literal(" Karma: " + karma), false);
        source.sendFeedback(() -> Text.literal(" Class: ").append(Text.translatable(karmaClass.getTranslationKey())), false);
        return karma;
    }
}
