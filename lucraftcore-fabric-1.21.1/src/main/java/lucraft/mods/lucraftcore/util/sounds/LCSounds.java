package lucraft.mods.lucraftcore.util.sounds;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/** Sound events used by energy blasts and the infinity gauntlet. */
public final class LCSounds {

    public static final SoundEvent ENERGY_BLAST = create("energy_blast");
    public static final SoundEvent INFINITY_STONE_EQUIP = create("infinity_stone_equip");
    public static final SoundEvent USE_INFINITY_GAUNTLET = create("use_infinity_gauntlet");

    private LCSounds() {
    }

    private static SoundEvent create(String name) {
        Identifier id = LucraftCore.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        LucraftCore.LOGGER.debug("Registered sound events: energy_blast, infinity_stone_equip, use_infinity_gauntlet");
    }
}
