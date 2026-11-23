package lucraft.mods.lucraftcore;

import lucraft.mods.lucraftcore.client.AbilityBarClient;
import lucraft.mods.lucraftcore.client.ClientNetworkHandlers;
import lucraft.mods.lucraftcore.client.EnergyBeams;
import lucraft.mods.lucraftcore.client.ExtendedInventoryClient;
import lucraft.mods.lucraftcore.client.LCColoredParticle;
import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import lucraft.mods.lucraftcore.infinity.render.GauntletItemRenderer;
import lucraft.mods.lucraftcore.infinity.screen.InfinityGauntletScreen;
import lucraft.mods.lucraftcore.util.particles.LCParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class LucraftCoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientNetworkHandlers.register();
        AbilityBarClient.register();
        EnergyBeams.register();
        ExtendedInventoryClient.register();

        // The gauntlet is a 3D model, not a flat sprite, and has its own screen.
        if (ModuleInfinity.INFINITY_GAUNTLET != null)
            BuiltinItemRendererRegistry.INSTANCE.register(ModuleInfinity.INFINITY_GAUNTLET,
                    new GauntletItemRenderer());
        if (ModuleInfinity.GAUNTLET_SCREEN_HANDLER != null)
            HandledScreens.register(ModuleInfinity.GAUNTLET_SCREEN_HANDLER, InfinityGauntletScreen::new);
        ParticleFactoryRegistry.getInstance().register(LCParticles.COLORED_CLOUD, provider ->
                (type, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new LCColoredParticle(world, x, y, z, velocityX, velocityY, velocityZ, provider, 0xFFFFFF));
    }
}
