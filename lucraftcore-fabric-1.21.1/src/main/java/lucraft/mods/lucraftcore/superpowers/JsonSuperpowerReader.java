package lucraft.mods.lucraftcore.superpowers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConfig;
import lucraft.mods.lucraftcore.superpowers.abilities.conditions.ConditionReader;
import lucraft.mods.lucraftcore.superpowers.effects.Effect;
import lucraft.mods.lucraftcore.superpowers.effects.EffectReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads addon-pack superpowers:
 * <pre>
 * { "name": "...", "description": "...", "color": 16711680,
 *   "levels": [ {"xp": 0}, {"xp": 100} ],
 *   "abilities": [ {"ability": "lucraftcore:strength", "amount": 3, "energy": 0} ] }
 * </pre>
 */
public final class JsonSuperpowerReader {

    private JsonSuperpowerReader() {
    }

    public static Superpower read(Identifier id, JsonObject json) {
        Text name = readText(json.get("name"), defaultName(id));
        Text description = readText(json.get("description"), Text.empty());
        int color = readColor(json.get("color"));
        List<AbilityConfig> abilities = readAbilities(id, json.get("abilities"));

        if (abilities.isEmpty() && json.has("abilities"))
            LucraftCore.LOGGER.warn("Superpower '{}' has no readable abilities", id);

        int[] levels = readLevels(json.get("levels"));
        List<Effect> effects = EffectReader.read(json);
        return new Superpower(id, name, description, color, levels, abilities, effects);
    }

    private static List<AbilityConfig> readAbilities(Identifier powerId, JsonElement element) {
        List<AbilityConfig> abilities = new ArrayList<>();
        if (element == null || !element.isJsonArray())
            return abilities;

        JsonArray array = element.getAsJsonArray();
        for (JsonElement entry : array) {
            String rawId;
            JsonObject data;
            if (entry.isJsonPrimitive()) {
                rawId = entry.getAsString();
                data = new JsonObject();
            } else if (entry.isJsonObject()) {
                data = entry.getAsJsonObject();
                rawId = data.has("ability") ? data.get("ability").getAsString()
                        : data.has("id") ? data.get("id").getAsString() : null;
            } else {
                continue;
            }

            if (rawId == null) {
                LucraftCore.LOGGER.warn("Superpower '{}' has an ability without an id", powerId);
                continue;
            }
            String abilityId = AbilityRegistry.normalize(rawId);
            if (!AbilityRegistry.exists(abilityId)) {
                LucraftCore.LOGGER.warn("Superpower '{}' uses unknown ability '{}'", powerId, rawId);
                continue;
            }

            int level = data.has("level") ? data.get("level").getAsInt() : 1;
            int energy = data.has("energy") ? data.get("energy").getAsInt()
                    : data.has("energy_cost") ? data.get("energy_cost").getAsInt() : 0;
            int cooldown = data.has("cooldown") ? data.get("cooldown").getAsInt() : 0;
            List<AbilityCondition> conditions = ConditionReader.read(data);
            abilities.add(new AbilityConfig(abilityId, level, energy, cooldown, data, conditions));
        }
        return abilities;
    }

    private static int[] readLevels(JsonElement element) {
        if (element == null || !element.isJsonArray())
            return new int[]{0};
        JsonArray array = element.getAsJsonArray();
        if (array.isEmpty())
            return new int[]{0};
        int[] levels = new int[array.size()];
        for (int index = 0; index < array.size(); index++) {
            JsonElement entry = array.get(index);
            if (entry.isJsonPrimitive()) {
                levels[index] = entry.getAsInt();
            } else if (entry.isJsonObject() && entry.getAsJsonObject().has("xp")) {
                levels[index] = entry.getAsJsonObject().get("xp").getAsInt();
            } else {
                levels[index] = index == 0 ? 0 : levels[index - 1];
            }
        }
        levels[0] = 0;
        return levels;
    }

    private static Text readText(JsonElement element, Text fallback) {
        if (element == null || element.isJsonNull())
            return fallback;
        if (element.isJsonPrimitive())
            return Text.literal(element.getAsString());
        if (element.isJsonObject() && element.getAsJsonObject().has("text"))
            return Text.literal(element.getAsJsonObject().get("text").getAsString());
        return fallback;
    }

    private static int readColor(JsonElement element) {
        if (element == null || element.isJsonNull())
            return 0xFFFFFF;
        try {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String value = element.getAsString().replace("#", "");
                return (int) Long.parseLong(value, 16);
            }
            return element.getAsInt();
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    private static Text defaultName(Identifier id) {
        String path = id.getPath().replace('_', ' ');
        return Text.literal(Character.toUpperCase(path.charAt(0)) + path.substring(1));
    }
}
