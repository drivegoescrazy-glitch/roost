package lucraft.mods.lucraftcore.util;

import com.mojang.serialization.Codec;
import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** Data components shared by the engine layer. */
public final class LCComponents {

    /** Stored energy of an item, in FE. */
    public static final ComponentType<Integer> ENERGY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            LucraftCore.id("energy"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.VAR_INT)
                    .build());

    private LCComponents() {
    }

    /** Forces class initialisation so the components are registered. */
    public static void register() {
        LucraftCore.LOGGER.debug("Registered data components (energy)");
    }
}
