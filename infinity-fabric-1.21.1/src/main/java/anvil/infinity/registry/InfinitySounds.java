package anvil.infinity.registry;

import anvil.infinity.Infinity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

/** Sound events. The original registered one {@code infinity:snap} event. */
public final class InfinitySounds {

    public static final SoundEvent SNAP = Registry.register(Registries.SOUND_EVENT,
            Infinity.id("snap"), SoundEvent.of(Infinity.id("snap")));

    private InfinitySounds() {
    }

    public static void register() {
        Infinity.LOGGER.debug("Registered sound event infinity:snap");
    }
}
