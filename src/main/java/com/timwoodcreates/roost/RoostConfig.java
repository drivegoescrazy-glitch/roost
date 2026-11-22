package com.timwoodcreates.roost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RoostConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static RoostConfig instance = new RoostConfig();

	/** The speed multiplier for the roost. Higher is faster. */
	public double roostSpeed = 1.0d;

	/** The speed multiplier for the breeder. Higher is faster. */
	public double breederSpeed = 1.0d;

	/** Prevent vanilla chickens from laying eggs. Of interest to modpack makers only. */
	public boolean disableEggLaying = false;

	private RoostConfig() {
	}

	public static RoostConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("roost.json");

		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				RoostConfig loaded = GSON.fromJson(reader, RoostConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			} catch (IOException | RuntimeException exception) {
				Roost.LOGGER.warn("Could not read the Roost config, using defaults", exception);
			}
		}

		instance.roostSpeed = clampSpeed(instance.roostSpeed);
		instance.breederSpeed = clampSpeed(instance.breederSpeed);
		save(path);
	}

	private static double clampSpeed(double value) {
		if (Double.isNaN(value)) {
			return 1.0d;
		}
		return Math.max(0.01d, Math.min(100.0d, value));
	}

	private static void save(Path path) {
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException exception) {
			Roost.LOGGER.warn("Could not write the Roost config", exception);
		}
	}
}
