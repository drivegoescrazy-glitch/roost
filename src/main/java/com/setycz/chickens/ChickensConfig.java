package com.setycz.chickens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Reads and writes {@code config/chickens.json}. Keeps the original knobs
 * (spawn rate, brood sizes, nether multiplier, always-show-stats) and lets
 * individual chicken kinds be disabled by id.
 */
public final class ChickensConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("chickens.json");

	public static int spawnProbability = 10;
	public static int minBroodSize = 3;
	public static int maxBroodSize = 5;
	public static float netherSpawnChanceMultiplier = 1.0F;
	public static boolean alwaysShowStats = false;
	private static final Set<String> DISABLED = new HashSet<>();

	private ChickensConfig() {
	}

	public static void load() {
		if (!Files.exists(PATH)) {
			save();
			return;
		}
		try (Reader reader = Files.newBufferedReader(PATH)) {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);
			if (root == null) {
				return;
			}
			spawnProbability = getInt(root, "spawn_probability", spawnProbability);
			minBroodSize = getInt(root, "min_brood_size", minBroodSize);
			maxBroodSize = getInt(root, "max_brood_size", maxBroodSize);
			netherSpawnChanceMultiplier = root.has("nether_spawn_chance_multiplier")
					? root.get("nether_spawn_chance_multiplier").getAsFloat() : netherSpawnChanceMultiplier;
			alwaysShowStats = root.has("always_show_stats") && root.get("always_show_stats").getAsBoolean();
			DISABLED.clear();
			if (root.has("disabled_chickens") && root.get("disabled_chickens").isJsonArray()) {
				JsonArray array = root.getAsJsonArray("disabled_chickens");
				for (JsonElement element : array) {
					DISABLED.add(element.getAsString());
				}
			}
		} catch (Exception exception) {
			ChickensMod.LOGGER.warn("Could not read {}", PATH, exception);
		}
	}

	private static int getInt(JsonObject root, String key, int fallback) {
		return root.has(key) ? root.get(key).getAsInt() : fallback;
	}

	public static void save() {
		JsonObject root = new JsonObject();
		root.addProperty("spawn_probability", spawnProbability);
		root.addProperty("min_brood_size", minBroodSize);
		root.addProperty("max_brood_size", maxBroodSize);
		root.addProperty("nether_spawn_chance_multiplier", netherSpawnChanceMultiplier);
		root.addProperty("always_show_stats", alwaysShowStats);
		JsonArray disabled = new JsonArray();
		DISABLED.stream().sorted().forEach(disabled::add);
		root.add("disabled_chickens", disabled);
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH)) {
				GSON.toJson(root, writer);
			}
		} catch (IOException exception) {
			ChickensMod.LOGGER.warn("Could not write {}", PATH, exception);
		}
	}

	public static void apply() {
		for (ChickensRegistryItem item : ChickensRegistry.all()) {
			item.setEnabled(!DISABLED.contains(item.getRegistryName().toString()));
		}
	}

	public static boolean isDisabled(Identifier id) {
		return DISABLED.contains(id.toString());
	}

	public static void setDisabled(Identifier id, boolean disabled) {
		if (disabled) {
			DISABLED.add(id.toString());
		} else {
			DISABLED.remove(id.toString());
		}
	}
}
