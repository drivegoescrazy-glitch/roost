package lucraft.mods.lucraftcore.superpowers.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One ability as configured by an addon pack: which ability, the level it
 * unlocks at, its energy cost and cooldown, its conditions, and the raw JSON
 * used for ability specific settings (damage, range, commands, ...).
 */
public final class AbilityConfig {

    private final String abilityId;
    private final int level;
    private final int energyCost;
    private final int cooldown;
    private final JsonObject data;
    private final List<AbilityCondition> conditions;

    public AbilityConfig(String abilityId, int level, int energyCost, int cooldown,
                         JsonObject data, List<AbilityCondition> conditions) {
        this.abilityId = abilityId;
        this.level = Math.max(1, level);
        this.energyCost = Math.max(0, energyCost);
        this.cooldown = Math.max(0, cooldown);
        this.data = data == null ? new JsonObject() : data;
        this.conditions = conditions == null ? Collections.emptyList() : List.copyOf(conditions);
    }

    public static AbilityConfig of(String abilityId) {
        return new AbilityConfig(abilityId, 1, 0, 0, new JsonObject(), Collections.emptyList());
    }

    public String abilityId() {
        return abilityId;
    }

    public int level() {
        return level;
    }

    public int energyCost() {
        return energyCost;
    }

    public int cooldown() {
        return cooldown;
    }

    public JsonObject data() {
        return data;
    }

    public List<AbilityCondition> conditions() {
        return conditions;
    }

    public double getDouble(String key, double fallback) {
        JsonElement element = data.get(key);
        return element == null || !element.isJsonPrimitive() ? fallback : element.getAsDouble();
    }

    public int getInt(String key, int fallback) {
        JsonElement element = data.get(key);
        return element == null || !element.isJsonPrimitive() ? fallback : element.getAsInt();
    }

    public boolean getBoolean(String key, boolean fallback) {
        JsonElement element = data.get(key);
        return element == null || !element.isJsonPrimitive() ? fallback : element.getAsBoolean();
    }

    public String getString(String key, String fallback) {
        JsonElement element = data.get(key);
        return element == null || !element.isJsonPrimitive() ? fallback : element.getAsString();
    }

    public Identifier getId(String key, Identifier fallback) {
        String value = getString(key, null);
        if (value == null)
            return fallback;
        Identifier id = Identifier.tryParse(value);
        return id == null ? fallback : id;
    }

    /** Reads a string list from the first key that is present. */
    public List<String> getStringList(String... keys) {
        for (String key : keys) {
            JsonElement element = data.get(key);
            if (element == null)
                continue;
            if (element.isJsonPrimitive())
                return List.of(element.getAsString());
            if (element.isJsonArray()) {
                List<String> values = new ArrayList<>();
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array)
                    if (entry.isJsonPrimitive())
                        values.add(entry.getAsString());
                return values;
            }
        }
        return Collections.emptyList();
    }
}
