package lucraft.mods.lucraftcore.superpowers.effects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition;
import lucraft.mods.lucraftcore.superpowers.abilities.conditions.ConditionReader;

import java.util.ArrayList;
import java.util.List;

/** Reads the {@code effects} array used by suits and superpowers. */
public final class EffectReader {

    private EffectReader() {
    }

    public static List<Effect> read(JsonObject parent) {
        if (parent == null || !parent.has("effects"))
            return List.of();
        JsonElement element = parent.get("effects");
        List<Effect> effects = new ArrayList<>();
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement entry : array) {
                Effect effect = readOne(entry);
                if (effect != null)
                    effects.add(effect);
            }
        } else {
            Effect effect = readOne(element);
            if (effect != null)
                effects.add(effect);
        }
        return effects;
    }

    private static Effect readOne(JsonElement element) {
        if (element == null || !element.isJsonObject())
            return null;
        JsonObject json = element.getAsJsonObject();
        String type = json.has("type") ? json.get("type").getAsString() : "";
        List<AbilityCondition> conditions = ConditionReader.read(json);

        return switch (type) {
            case "glow" -> new Effects.Glow(json, conditions);
            case "trail" -> new Effects.Trail(json, conditions);
            case "aura" -> new Effects.Aura(json, conditions);
            case "name_change", "name" -> new Effects.NameChange(json, conditions);
            default -> {
                LucraftCore.LOGGER.warn("Unsupported effect type '{}' (needs client rendering)", type);
                yield null;
            }
        };
    }
}
