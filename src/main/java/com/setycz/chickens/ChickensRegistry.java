package com.setycz.chickens;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Registry of every chicken kind plus the breeding dice roll. */
public final class ChickensRegistry {
	private static final Map<Identifier, ChickensRegistryItem> ITEMS = new LinkedHashMap<>();
	private static final Random RANDOM = new Random();

	public static final Identifier SMART_CHICKEN_ID = Identifier.of(ChickensMod.MOD_ID, "smartchicken");

	private ChickensRegistry() {
	}

	public static void register(ChickensRegistryItem item) {
		ITEMS.put(item.getRegistryName(), item);
	}

	public static ChickensRegistryItem get(Identifier id) {
		return ITEMS.get(id);
	}

	public static ChickensRegistryItem get(String id) {
		Identifier identifier = Identifier.tryParse(id);
		return identifier == null ? null : get(identifier);
	}

	public static Collection<ChickensRegistryItem> all() {
		return ITEMS.values();
	}

	public static List<ChickensRegistryItem> getItems() {
		List<ChickensRegistryItem> result = new ArrayList<>();
		for (ChickensRegistryItem item : ITEMS.values()) {
			if (item.isEnabled()) {
				result.add(item);
			}
		}
		return result;
	}

	public static ChickensRegistryItem getSmartChicken() {
		return ITEMS.get(SMART_CHICKEN_ID);
	}

	public static SpawnType getSpawnType(World world) {
		if (world.getRegistryKey() == World.NETHER) {
			return SpawnType.HELL;
		}
		return SpawnType.NORMAL;
	}

	public static SpawnType getSpawnType(RegistryKey<Biome> biome, boolean snowy) {
		return snowy ? SpawnType.SNOW : SpawnType.NORMAL;
	}

	public static List<ChickensRegistryItem> getPossibleChickensToSpawn(SpawnType spawnType) {
		List<ChickensRegistryItem> result = new ArrayList<>();
		for (ChickensRegistryItem item : ITEMS.values()) {
			if (item.canSpawn() && item.getSpawnType() == spawnType && item.isEnabled()) {
				result.add(item);
			}
		}
		return result;
	}

	public static boolean isAnyIn(SpawnType spawnType) {
		for (ChickensRegistryItem item : ITEMS.values()) {
			if (item.canSpawn() && item.isEnabled() && item.getSpawnType() == spawnType) {
				return true;
			}
		}
		return false;
	}

	public static ChickensRegistryItem getRandomChild(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
		List<ChickensRegistryItem> possible = getChildren(parent1, parent2);
		if (possible.isEmpty()) {
			return null;
		}
		int maxChance = getMaxChance(possible);
		int maxDice = getMaxDiceValue(possible, maxChance);
		if (maxDice <= 0) {
			return null;
		}
		int roll = RANDOM.nextInt(maxDice);
		int current = 0;
		for (ChickensRegistryItem child : possible) {
			current += maxChance - child.getTier();
			if (roll < current) {
				return child;
			}
		}
		return possible.get(possible.size() - 1);
	}

	public static float getChildChance(ChickensRegistryItem child) {
		if (child.getTier() <= 1 || child.getParent1() == null) {
			return 0.0F;
		}
		List<ChickensRegistryItem> possible = getChildren(child.getParent1(), child.getParent2());
		int maxChance = getMaxChance(possible);
		int maxDice = getMaxDiceValue(possible, maxChance);
		return maxDice == 0 ? 0.0F : (maxChance - child.getTier()) * 100.0F / maxDice;
	}

	private static List<ChickensRegistryItem> getChildren(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
		List<ChickensRegistryItem> result = new ArrayList<>();
		if (parent1.isEnabled()) {
			result.add(parent1);
		}
		if (parent2.isEnabled()) {
			result.add(parent2);
		}
		for (ChickensRegistryItem item : ITEMS.values()) {
			if (item.isEnabled() && item.isChildOf(parent1, parent2)) {
				result.add(item);
			}
		}
		return result;
	}

	private static int getMaxChance(List<ChickensRegistryItem> possible) {
		int maxTier = 0;
		for (ChickensRegistryItem item : possible) {
			maxTier = Math.max(maxTier, item.getTier());
		}
		return maxTier + 1;
	}

	private static int getMaxDiceValue(List<ChickensRegistryItem> possible, int maxChance) {
		int total = 0;
		for (ChickensRegistryItem item : possible) {
			total += Math.max(maxChance - item.getTier(), 0);
		}
		return total;
	}
}
