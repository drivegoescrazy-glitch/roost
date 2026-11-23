package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class TeleportAbility extends Ability {

    private final double distance;

    public TeleportAbility(ServerPlayerEntity player, double distance) {
        super(player);
        this.distance = distance;
        setMaxCooldown(60);
    }

    @Override
    public String getId() {
        return "teleport";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.ACTION;
    }

    @Override
    protected boolean onUse() {
        Vec3d direction = player.getRotationVec(1.0F);
        Vec3d eye = player.getEyePos();
        HitResult hit = player.raycast(distance, 0.0F, false);
        Vec3d target = hit.getType() == HitResult.Type.MISS
                ? eye.add(direction.multiply(distance))
                : hit.getPos().subtract(direction.multiply(0.5D));
        player.requestTeleport(target.x, target.y, target.z);
        player.fallDistance = 0.0F;
        return true;
    }
}
