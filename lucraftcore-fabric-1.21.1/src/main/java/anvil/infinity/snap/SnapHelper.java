package anvil.infinity.snap;

import anvil.infinity.capabilities.GauntletUserInformation;
import anvil.infinity.capabilities.SnapPlayerData;
import anvil.infinity.config.InfinityConfig;
import anvil.infinity.helpers.GauntletHelper;
import anvil.infinity.registry.InfinityEffects;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import lucraft.mods.lucraftcore.karma.KarmaHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** The snap: kills half of everything, or whatever the selected result says. */
public final class SnapHelper {

    private SnapHelper() {
    }

    public static boolean snap(ServerPlayerEntity entity) {
        if (!GauntletHelper.hasFullGauntlet(entity))
            return false;

        SnapPlayerData data = GauntletUserInformation.get(entity);
        MinecraftServer server = entity.getServer();
        if (server == null)
            return false;

        switch (data.snapResult()) {
            case KILLHALF -> {
                List<Entity> entities = new ArrayList<>();
                for (ServerWorld world : server.getWorlds())
                    world.iterateEntities().forEach(entities::add);

                MutableText message = Text.literal(entity.getName().getString() + ": ")
                        .append(Text.translatable("infinity.snap.text"))
                        .formatted(Formatting.DARK_PURPLE, Formatting.BOLD);
                server.getPlayerManager().broadcast(message, false);

                Random random = new Random();
                boolean kill = false;
                for (Entity other : entities) {
                    if (other != entity && other instanceof LivingEntity living && kill
                            && !(other instanceof RabbitEntity))
                        living.addStatusEffect(snapEffect(random.nextInt(1191) - 10));
                    kill = !kill;
                }
                return true;
            }
            case DESTROYSTONES -> {
                entity.setHealth(1.0F);
                ItemStack mainHand = entity.getMainHandStack();
                ItemStack offHand = entity.getOffHandStack();
                if (mainHand.getItem() instanceof ItemInfinityGauntlet)
                    entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(mainHand.getItem()));
                if (offHand.getItem() instanceof ItemInfinityGauntlet)
                    entity.setStackInHand(Hand.OFF_HAND, new ItemStack(offHand.getItem()));
                entity.damage(entity.getServerWorld().getDamageSources().outOfWorld(),
                        entity.getHealth() / 10.0F - 0.01F);
            }
            case CREATIVE -> {
                if (InfinityConfig.snapCreative && entity != null) {
                    if (entity.isCreative())
                        entity.changeGameMode(net.minecraft.world.GameMode.SURVIVAL);
                    else
                        entity.changeGameMode(net.minecraft.world.GameMode.CREATIVE);
                    return true;
                }
            }
            case KILLEVIL -> {
                List<Entity> entities = new ArrayList<>();
                for (ServerWorld world : server.getWorlds())
                    world.iterateEntities().forEach(entities::add);

                MutableText message = Text.literal(entity.getName().getString() + ": ")
                        .append(Text.translatable("infinity.snap.irontext"))
                        .formatted(Formatting.RED, Formatting.BOLD);
                server.getPlayerManager().broadcast(message, false);

                Random random = new Random();
                for (Entity other : entities) {
                    if (other == entity || !(other instanceof LivingEntity living))
                        continue;
                    if (other instanceof Monster) {
                        living.addStatusEffect(snapEffect(random.nextInt(1191) - 10));
                        continue;
                    }
                    if (other instanceof PlayerEntity player && KarmaHandler.getKarma(player) < 1)
                        living.addStatusEffect(snapEffect(random.nextInt(1191) - 10));
                }
            }
            case BRINGBACK, RECREATE -> {
                // Both were marked WIP in the original and did nothing.
            }
        }
        return false;
    }

    /**
     * The original used the rolled duration as-is; negative values never
     * expired. They are clamped to one tick here so a dusted entity always
     * finishes its snap (0.8% of the original's rolls were negative).
     */
    private static StatusEffectInstance snapEffect(int duration) {
        var entry = Registries.STATUS_EFFECT.getEntry(InfinityEffects.SNAP);
        return new StatusEffectInstance(entry, Math.max(1, duration));
    }
}
