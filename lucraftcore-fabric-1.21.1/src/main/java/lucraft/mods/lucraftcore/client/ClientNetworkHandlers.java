package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.util.network.SpawnParticlePayload;
import lucraft.mods.lucraftcore.util.network.SwingHandPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/** Client receivers for the mod's S2C payloads. */
public final class ClientNetworkHandlers {

    private ClientNetworkHandlers() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(SpawnParticlePayload.ID, (payload, context) -> {
            ClientWorld world = context.client().world;
            if (world == null)
                return;
            ParticleType<?> type = Registries.PARTICLE_TYPE.get(payload.particle());
            if (!(type instanceof ParticleEffect effect))
                return;
            Vec3d position = payload.position();
            Vec3d velocity = payload.velocity();
            for (int i = 0; i < payload.count(); i++)
                world.addParticle(effect, true, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        });

        ClientPlayNetworking.registerGlobalReceiver(SwingHandPayload.ID, (payload, context) -> {
            ClientWorld world = context.client().world;
            if (world == null)
                return;
            if (world.getEntityById(payload.entityId()) instanceof LivingEntity entity)
                entity.swingHand(payload.mainHand() ? Hand.MAIN_HAND : Hand.OFF_HAND, true);
        });
    }
}
