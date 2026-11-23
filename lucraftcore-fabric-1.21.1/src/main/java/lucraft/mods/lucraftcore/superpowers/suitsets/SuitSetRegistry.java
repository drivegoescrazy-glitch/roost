package lucraft.mods.lucraftcore.superpowers.suitsets;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuitSetRegistry {

    private static final Map<Identifier, SuitSet> SUITS = new LinkedHashMap<>();

    public static void register(SuitSet suit) {
        SUITS.put(suit.getId(), suit);
    }

    public static SuitSet get(Identifier id) {
        return SUITS.get(id);
    }

    public static List<SuitSet> getAll() {
        return new ArrayList<>(SUITS.values());
    }

    public static int size() {
        return SUITS.size();
    }
}
