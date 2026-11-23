package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.AbilityBarSync;
import lucraft.mods.lucraftcore.superpowers.PlayerAbilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Registration and sending helpers for the mod's play-channel payloads.
 * Replaces the original Forge SimpleNetworkWrapper messages: spawn particle and
 * swing arm.
 */
public final class LCNetworking {

    private static final double PARTICLE_RANGE = 64.0D;

    private LCNetworking() {
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(SpawnParticlePayload.ID, SpawnParticlePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SwingHandPayload.ID, SwingHandPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AbilityBarPayload.ID, AbilityBarPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(UseAbilityPayload.ID, UseAbilityPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(OpenExtendedInventoryPayload.ID, OpenExtendedInventoryPayload.CODEC);
    }

    /** Server receivers for client keybinds. */
    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(UseAbilityPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            player.getServer().execute(() -> {
                PlayerAbilities.use(player, payload.abilityId());
                AbilityBarSync.send(player);
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(OpenExtendedInventoryPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            player.getServer().execute(() -> lucraft.mods.lucraftcore.extendedinventory.ExtendedInventoryHandler.open(player));
        });
    }

    /** Spawns particles for one player. */
    public static void sendParticle(ServerPlayerEntity player, ParticleEffect effect, Vec3d position, Vec3d velocity, int count) {
        Identifier id = Registries.PARTICLE_TYPE.getId(effect.getType());
        if (id == null)
            return;
        if (ServerPlayNetworking.canSend(player, SpawnParticlePayload.ID))
            ServerPlayNetworking.send(player, SpawnParticlePayload.create(id, position, velocity, count));
    }

    /** Spawns particles for every player within range of the position. */
    public static void sendParticle(ServerWorld world, Vec3d position, ParticleEffect effect, Vec3d velocity, int count) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.squaredDistanceTo(position) <= PARTICLE_RANGE * PARTICLE_RANGE)
                sendParticle(player, effect, position, velocity, count);
        }
    }

    /** Shows an arm swing on an entity to nearby players. */
    public static void sendSwing(ServerWorld world, int entityId, Vec3d position, Hand hand) {
        SwingHandPayload payload = new SwingHandPayload(entityId, hand == Hand.MAIN_HAND);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.squaredDistanceTo(position) <= PARTICLE_RANGE * PARTICLE_RANGE
                    && ServerPlayNetworking.canSend(player, SwingHandPayload.ID))
                ServerPlayNetworking.send(player, payload);
        }
    }

    /** Logged once at startup so a missing registration is easy to spot. */
    public static void logRegistration() {
        LucraftCore.LOGGER.debug("Registered payloads: {}, {}",
                SpawnParticlePayload.ID.id(), SwingHandPayload.ID.id());
    }
}
