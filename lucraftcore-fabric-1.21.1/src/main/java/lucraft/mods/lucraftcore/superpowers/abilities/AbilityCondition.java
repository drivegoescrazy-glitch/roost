package lucraft.mods.lucraftcore.superpowers.abilities;

import net.minecraft.server.network.ServerPlayerEntity;

/** Gate that decides whether an ability may run for a player. */
public interface AbilityCondition {

    boolean test(ServerPlayerEntity player);

    /** JSON type name, used when reading addon pack data. */
    String type();
}
