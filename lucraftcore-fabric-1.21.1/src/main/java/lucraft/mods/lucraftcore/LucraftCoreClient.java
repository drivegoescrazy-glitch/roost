package lucraft.mods.lucraftcore;

import lucraft.mods.lucraftcore.client.AbilityBarClient;
import lucraft.mods.lucraftcore.client.ClientNetworkHandlers;
import lucraft.mods.lucraftcore.client.ExtendedInventoryClient;
import lucraft.mods.lucraftcore.client.LCColoredParticle;
import lucraft.mods.lucraftcore.util.particles.LCParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class LucraftCoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientNetworkHandlers.register();
        AbilityBarClient.register();
        ExtendedInventoryClient.register();
        ParticleFactoryRegistry.getInstance().register(LCParticles.COLORED_CLOUD, provider ->
                (type, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new LCColoredParticle(world, x, y, z, velocityX, velocityY, velocityZ, provider, 0xFFFFFF));
    }
}
