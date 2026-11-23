package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

public class FlightAbility extends Ability {

    private final float speed;
    private final float sprintSpeed;

    public FlightAbility(ServerPlayerEntity player, float speed, float sprintSpeed) {
        super(player);
        this.speed = speed;
        this.sprintSpeed = sprintSpeed;
    }

    @Override
    public String getId() {
        return "flight";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.TOGGLE;
    }

    @Override
    protected void onEnabled() {
        player.getAbilities().allowFlying = true;
        player.sendAbilitiesUpdate();
    }

    @Override
    protected void onDisabled() {
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
        }
        player.sendAbilitiesUpdate();
    }

    @Override
    protected void onTick() {
        if (player.getAbilities().flying || player.isSprinting()) {
            player.getAbilities().setFlySpeed(player.isSprinting() ? sprintSpeed : speed);
        }
        player.fallDistance = 0.0F;
    }

    @Override
    protected void cleanup() {
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
        }
        player.getAbilities().setFlySpeed(0.05F);
        player.sendAbilitiesUpdate();
    }
}
