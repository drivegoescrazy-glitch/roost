package anvil.infinity.helpers;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.PlayerAbilities;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * The Soul Stone made its wearer effectively immortal by setting a huge maximum
 * health (the original used {@code Float.MAX_VALUE}). The modern health bar
 * draws one heart per two health points, so the port keeps a normal sized
 * health boost and simply ignores damage that would be lethal instead.
 */
public final class ImmortalityHandler {

    public static final String ABILITY_ID = "immortality";

    private ImmortalityHandler() {
    }

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            try {
                if (!(entity instanceof ServerPlayerEntity player))
                    return true;
                if (!PlayerAbilities.isActive(player, ABILITY_ID))
                    return true;
                // /kill and other admin damage still works.
                if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY))
                    return true;
                return amount < player.getHealth();
            } catch (Throwable t) {
                LucraftCore.reportError("immortality damage check", t);
                return true;
            }
        });
    }
}
