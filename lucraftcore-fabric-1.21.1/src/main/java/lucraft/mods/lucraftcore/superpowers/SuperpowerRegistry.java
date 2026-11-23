package lucraft.mods.lucraftcore.superpowers;

import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Registry of addon-pack superpowers. */
public final class SuperpowerRegistry {

    private static final Map<Identifier, Superpower> SUPERPOWERS = new LinkedHashMap<>();

    private SuperpowerRegistry() {
    }

    public static void clear() {
        SUPERPOWERS.clear();
    }

    public static void register(Superpower superpower) {
        SUPERPOWERS.put(superpower.getId(), superpower);
    }

    public static Superpower get(Identifier id) {
        return id == null ? null : SUPERPOWERS.get(id);
    }

    public static Superpower get(String id) {
        return get(Identifier.tryParse(id));
    }

    public static Collection<Superpower> getAll() {
        return SUPERPOWERS.values();
    }

    public static int size() {
        return SUPERPOWERS.size();
    }
}
