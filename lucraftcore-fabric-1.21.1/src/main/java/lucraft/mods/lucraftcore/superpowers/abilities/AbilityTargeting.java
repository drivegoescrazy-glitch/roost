package lucraft.mods.lucraftcore.superpowers.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/** Shared target selection for punch and beam abilities. */
public final class AbilityTargeting {

    private AbilityTargeting() {
    }

    /**
     * Finds the closest living entity in the direction the player is looking,
     * within the given distance and cone.
     */
    public static LivingEntity findTarget(ServerWorld world, PlayerEntity player, Vec3d eye, Vec3d direction, double maxDistance) {
        LivingEntity best = null;
        double bestDistance = maxDistance;
        Box box = player.getBoundingBox().expand(maxDistance);
        for (Entity entity : world.getOtherEntities(player, box)) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive() || entity.isSpectator())
                continue;
            Vec3d to = living.getBoundingBox().getCenter().subtract(eye);
            double distance = to.length();
            if (distance < 0.001D || distance > maxDistance)
                continue;
            if (to.normalize().dotProduct(direction) < 0.75D)
                continue;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = living;
            }
        }
        return best;
    }
}
