package com.setycz.chickens;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;

/** Adds the modded chicken to biome spawn lists, matching the original rates. */
public final class ChickenSpawns {
	private ChickenSpawns() {
	}

	public static void init() {
		int weight = Math.max(1, ChickensConfig.spawnProbability);
		int min = Math.max(1, ChickensConfig.minBroodSize);
		int max = Math.max(min, ChickensConfig.maxBroodSize);

		BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.CREATURE,
				ChickensEntities.CHICKEN, weight, min, max);

		int netherWeight = Math.round(weight * ChickensConfig.netherSpawnChanceMultiplier);
		if (netherWeight > 0) {
			BiomeModifications.addSpawn(BiomeSelectors.foundInTheNether(), SpawnGroup.CREATURE,
					ChickensEntities.CHICKEN, netherWeight, min, max);
		}
	}
}
