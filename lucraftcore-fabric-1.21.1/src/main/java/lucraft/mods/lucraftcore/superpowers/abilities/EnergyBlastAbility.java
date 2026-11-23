package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import lucraft.mods.lucraftcore.util.network.LCNetworking;
import lucraft.mods.lucraftcore.util.particles.LCParticles;
import lucraft.mods.lucraftcore.util.sounds.LCSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/**
 * Energy blast: a hitscan beam that damages the first entity in the player's
 * line of sight and draws a particle trail. Costs energy per shot.
 */
public class EnergyBlastAbility extends Ability {

    private final AbilityConfig config;

    public EnergyBlastAbility(ServerPlayerEntity player, AbilityConfig config) {
        super(player);
        this.config = config;
    }

    @Override
    public String getId() {
        return config.abilityId();
    }

    @Override
    public AbilityType getType() {
        return AbilityType.ACTION;
    }

    @Override
    protected boolean onUse() {
        if (!payEnergy())
            return false;

        ServerWorld world = player.getServerWorld();
        double range = config.getDouble("range", 24.0D);
        float damage = (float) config.getDouble("damage", 4.0D);
        Vec3d eye = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0F);
        Vec3d end = eye.add(direction.multiply(range));

        BlockHitResult blockHit = world.raycast(new RaycastContext(eye, end,
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        double maxDistance = blockHit.getType() == HitResult.Type.MISS ? range : eye.distanceTo(blockHit.getPos());
        LivingEntity target = AbilityTargeting.findTarget(world, player, eye, direction, maxDistance);

        Vec3d hitPos;
        if (target != null)
            hitPos = target.getBoundingBox().getCenter();
        else if (blockHit.getType() == HitResult.Type.MISS)
            hitPos = end;
        else
            hitPos = blockHit.getPos();

        Vec3d delta = hitPos.subtract(eye);
        int steps = (int) Math.max(2, Math.min(80, delta.length() * 2));
        for (int step = 1; step <= steps; step++)
            LCNetworking.sendParticle(world, eye.add(delta.multiply(step / (double) steps)),
                    LCParticles.COLORED_CLOUD, Vec3d.ZERO, 1);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                LCSounds.ENERGY_BLAST, SoundCategory.PLAYERS, 1.0F, 1.0F);
        if (target != null)
            target.damage(world.getDamageSources().indirectMagic(player, player), damage);
        return true;
    }
}
