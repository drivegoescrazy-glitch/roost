package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.util.particles.LCParticles;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

/** Coloured cloud particle used by energy blasts and suit trails. */
public class LCColoredParticle extends SpriteBillboardParticle {

    private final int color;

    public LCColoredParticle(ClientWorld world, double x, double y, double z,
                             double velocityX, double velocityY, double velocityZ,
                             SpriteProvider provider, int color) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.color = color;
        this.setSprite(provider);
        this.maxAge = 20 + this.random.nextInt(15);
        this.scale = 0.45F + this.random.nextFloat() * 0.35F;
        this.gravityStrength = 0.0F;
        this.velocityMultiplier = 0.96F;
        this.alpha = 1.0F;
        this.red = ((color >> 16) & 0xFF) / 255.0F;
        this.green = ((color >> 8) & 0xFF) / 255.0F;
        this.blue = (color & 0xFF) / 255.0F;
    }

    public int getColor() {
        return color;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
