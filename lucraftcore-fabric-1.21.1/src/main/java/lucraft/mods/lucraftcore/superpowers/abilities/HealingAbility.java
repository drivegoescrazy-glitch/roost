package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import net.minecraft.server.network.ServerPlayerEntity;

public class HealingAbility extends Ability {

    private final int frequency;
    private final float amount;

    public HealingAbility(ServerPlayerEntity player, int frequency, float amount) {
        super(player);
        this.frequency = frequency;
        this.amount = amount;
    }

    @Override
    public String getId() {
        return "healing";
    }

    @Override
    protected void onTick() {
        if (frequency > 0 && ticks % frequency == 0)
            player.heal(amount);
    }
}
