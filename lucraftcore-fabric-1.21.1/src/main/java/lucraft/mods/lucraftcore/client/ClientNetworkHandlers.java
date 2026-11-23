package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.util.network.EnergyBeamPayload;
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

/**
 * Client receivers for the mod's S2C payloads.
 *
 * <p>Fabric runs play-payload handlers on the network thread. Everything here
 * touches state the render and tick threads read — the particle manager, entity
 * swings, the beam list — so each handler is scheduled onto the client thread;
 * doing it from the network thread could corrupt a list or particle manager
 * while it is being rendered, which crashes the client and, in single player,
 * takes the integrated server down with it ("Server closed").</p>
 */
public final class ClientNetworkHandlers {

    private ClientNetworkHandlers() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(EnergyBeamPayload.ID,
                (payload, context) -> context.client().execute(() -> EnergyBeams.add(payload)));

        ClientPlayNetworking.registerGlobalReceiver(SpawnParticlePayload.ID, (payload, context) ->
                context.client().execute(() -> {
                    ClientWorld world = context.client().world;
                    if (world == null)
                        return;
                    ParticleType<?> type = Registries.PARTICLE_TYPE.get(payload.particle());
                    if (!(type instanceof ParticleEffect effect))
                        return;
                    Vec3d position = payload.position();
                    Vec3d velocity = payload.velocity();
                    for (int i = 0; i < payload.count(); i++)
                        world.addParticle(effect, true, position.x, position.y, position.z,
                                velocity.x, velocity.y, velocity.z);
                }));

        ClientPlayNetworking.registerGlobalReceiver(SwingHandPayload.ID, (payload, context) ->
                context.client().execute(() -> {
                    ClientWorld world = context.client().world;
                    if (world == null)
                        return;
                    if (world.getEntityById(payload.entityId()) instanceof LivingEntity entity)
                        entity.swingHand(payload.mainHand() ? Hand.MAIN_HAND : Hand.OFF_HAND, true);
                }));
    }
}
