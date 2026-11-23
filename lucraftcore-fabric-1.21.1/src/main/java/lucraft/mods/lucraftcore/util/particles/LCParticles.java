package lucraft.mods.lucraftcore.util.particles;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** Particle types used by abilities and suits. */
public final class LCParticles {

    /** Soft coloured cloud used by energy blasts and trails. */
    public static final SimpleParticleType COLORED_CLOUD = Registry.register(
            Registries.PARTICLE_TYPE,
            LucraftCore.id("colored_cloud"),
            new SimpleParticleType(false) {
            });

    private LCParticles() {
    }

    public static void register() {
        LucraftCore.LOGGER.debug("Registered particle type colored_cloud");
    }
}
