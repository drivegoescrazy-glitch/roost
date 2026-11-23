package lucraft.mods.lucraftcore.superpowers.abilities.conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Reads condition JSON used by superpowers and ability configs. */
public final class ConditionReader {

    private ConditionReader() {
    }

    public static List<AbilityCondition> read(JsonObject parent) {
        if (parent == null)
            return Collections.emptyList();
        if (parent.has("conditions"))
            return readAll(parent.get("conditions"));
        if (parent.has("condition"))
            return readAll(parent.get("condition"));
        return Collections.emptyList();
    }

    public static List<AbilityCondition> readAll(JsonElement element) {
        if (element == null || element.isJsonNull())
            return Collections.emptyList();
        if (element.isJsonArray()) {
            List<AbilityCondition> conditions = new ArrayList<>();
            JsonArray array = element.getAsJsonArray();
            for (JsonElement entry : array) {
                AbilityCondition condition = readOne(entry);
                if (condition != null)
                    conditions.add(condition);
            }
            return conditions;
        }
        AbilityCondition single = readOne(element);
        return single == null ? Collections.emptyList() : List.of(single);
    }

    private static AbilityCondition readOne(JsonElement element) {
        if (element == null || !element.isJsonObject())
            return null;
        JsonObject json = element.getAsJsonObject();
        String type = json.has("type") ? json.get("type").getAsString() : "";

        switch (type) {
            case "held_item":
                return new Conditions.HeldItem(id(json, "item"));
            case "superpower":
                return new Conditions.HasSuperpower(id(json, "superpower"));
            case "level":
                return new Conditions.Level(json.has("level") ? json.get("level").getAsInt() : 1);
            case "ability":
                return new Conditions.AbilityEnabled(string(json, "ability"));
            case "scoreboard_tag":
                return new Conditions.ScoreboardTag(string(json, "tag"));
            case "potion":
                return new Conditions.HasEffect(id(json, "potion"));
            case "addonpack":
                return new Conditions.AddonPack(string(json, "pack"));
            case "and":
                return new Conditions.All(readAll(json.get("conditions")));
            case "or":
                return new Conditions.Any(readAll(json.get("conditions")));
            case "not": {
                List<AbilityCondition> nested = readAll(json.get("condition"));
                if (nested.isEmpty())
                    nested = readAll(json.get("conditions"));
                return nested.isEmpty() ? null : new Conditions.Not(nested.get(0));
            }
            default:
                LucraftCore.LOGGER.warn("Unknown ability condition type '{}'", type);
                return null;
        }
    }

    private static Identifier id(JsonObject json, String key) {
        String value = string(json, key);
        Identifier parsed = Identifier.tryParse(value);
        return parsed == null ? LucraftCore.id("missing") : parsed;
    }

    private static String string(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsString() : "";
    }
}
