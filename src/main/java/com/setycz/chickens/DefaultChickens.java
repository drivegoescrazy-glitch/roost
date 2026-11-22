package com.setycz.chickens;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The 46 chicken kinds shipped by Chickens 6.1.0, rebuilt with 1.21.1 items.
 * Identifiers, tiers, parents, colours and textures match the original mod.
 */
public final class DefaultChickens {
	private DefaultChickens() {
	}

	private static Identifier texture(String file) {
		return Identifier.of(ChickensMod.MOD_ID, "textures/entity/" + file + ".png");
	}

	private static Identifier id(String name) {
		// Modern resource identifiers must be lowercase; the display name and
		// translation key keep the original capitalisation.
		return Identifier.of(ChickensMod.MOD_ID, name.toLowerCase(java.util.Locale.ROOT));
	}

	private static ChickensRegistryItem dye(String name, String colorKey, int fgColor, ItemStack layItem) {
		return new ChickensRegistryItem(id(name), name, texture(colorKey), layItem, 0xF2F2F2, fgColor)
				.setColorKey(colorKey)
				.setSpawnType(SpawnType.NONE);
	}

	public static List<ChickensRegistryItem> create() {
		List<ChickensRegistryItem> chickens = new ArrayList<>();

		chickens.add(new ChickensRegistryItem(ChickensRegistry.SMART_CHICKEN_ID, "SmartChicken",
				texture("smart_chicken"), new ItemStack(Items.BOOK), 0xFFFFFF, 0xFFFF00)
				.setSpawnType(SpawnType.NONE));

		ChickensRegistryItem white = dye("WhiteChicken", "white_chicken", 0xFFFFFF, new ItemStack(Items.BONE))
				.setSpawnType(SpawnType.NORMAL);
		chickens.add(white);
		ChickensRegistryItem yellow = dye("YellowChicken", "yellow_chicken", 0xFED83D, new ItemStack(Items.YELLOW_DYE));
		chickens.add(yellow);
		ChickensRegistryItem blue = dye("BlueChicken", "blue_chicken", 0x3C44AA, new ItemStack(Items.LAPIS_LAZULI));
		chickens.add(blue);
		ChickensRegistryItem green = dye("GreenChicken", "green_chicken", 0x5E7C16, new ItemStack(Items.GREEN_DYE));
		chickens.add(green);
		ChickensRegistryItem red = dye("RedChicken", "red_chicken", 0xB02E26, new ItemStack(Items.RED_DYE));
		chickens.add(red);
		ChickensRegistryItem black = dye("BlackChicken", "black_chicken", 0x1D1D21, new ItemStack(Items.INK_SAC));
		chickens.add(black);

		ChickensRegistryItem pink = dye("PinkChicken", "pink_chicken", 0xF38BAA, new ItemStack(Items.PINK_DYE))
				.setParentsNew(red, white);
		chickens.add(pink);
		ChickensRegistryItem purple = dye("PurpleChicken", "purple_chicken", 0x8932B8, new ItemStack(Items.PURPLE_DYE))
				.setParentsNew(blue, red);
		chickens.add(purple);
		chickens.add(dye("OrangeChicken", "orange_chicken", 0xFF681F, new ItemStack(Items.ORANGE_DYE))
				.setParentsNew(red, yellow));
		chickens.add(dye("LightBlueChicken", "light_blue_chicken", 0x3AB3DA, new ItemStack(Items.LIGHT_BLUE_DYE))
				.setParentsNew(white, blue));
		chickens.add(dye("LimeChicken", "lime_chicken", 0x80C71F, new ItemStack(Items.LIME_DYE))
				.setParentsNew(green, white));
		ChickensRegistryItem gray = dye("GrayChicken", "gray_chicken", 0x474F52, new ItemStack(Items.GRAY_DYE))
				.setParentsNew(black, white);
		chickens.add(gray);
		chickens.add(dye("CyanChicken", "cyan_chicken", 0x169C9C, new ItemStack(Items.CYAN_DYE))
				.setParentsNew(blue, green));
		chickens.add(dye("SilverDyeChicken", "silver_dye_chicken", 0x9D9D97, new ItemStack(Items.LIGHT_GRAY_DYE))
				.setParentsNew(gray, white));
		chickens.add(dye("MagentaChicken", "magenta_chicken", 0xFF00FF, new ItemStack(Items.MAGENTA_DYE))
				.setParentsNew(purple, pink));

		ChickensRegistryItem flint = new ChickensRegistryItem(id("FlintChicken"), "FlintChicken",
				texture("flint_chicken"), new ItemStack(Items.FLINT), 0x6B6B47, 0xA3A375);
		chickens.add(flint);

		ChickensRegistryItem quartz = new ChickensRegistryItem(id("QuartzChicken"), "QuartzChicken",
				texture("quartz_chicken"), new ItemStack(Items.QUARTZ), 0x4D0000, 0x1A0000)
				.setSpawnType(SpawnType.HELL);
		chickens.add(quartz);

		ChickensRegistryItem log = new ChickensRegistryItem(id("LogChicken"), "LogChicken",
				texture("log_chicken"), new ItemStack(Items.OAK_LOG), 0x98846D, 0x528358);
		chickens.add(log);

		ChickensRegistryItem sand = new ChickensRegistryItem(id("SandChicken"), "SandChicken",
				texture("sand_chicken"), new ItemStack(Items.SAND), 0xECE5B1, 0xA7A06C);
		chickens.add(sand);

		chickens.add(new ChickensRegistryItem(id("StringChicken"), "StringChicken",
				texture("string_chicken"), new ItemStack(Items.STRING), 0x331A00, 0x800000, black, log)
				.setDropItem(new ItemStack(Items.SPIDER_EYE)));

		chickens.add(new ChickensRegistryItem(id("GlowstoneChicken"), "GlowstoneChicken",
				texture("glowstone_chicken"), new ItemStack(Items.GLOWSTONE_DUST), 0xFFFF66, 0xFFFF00, quartz, yellow));

		chickens.add(new ChickensRegistryItem(id("GunpowderChicken"), "GunpowderChicken",
				texture("gunpowder_chicken"), new ItemStack(Items.GUNPOWDER), 0x999999, 0x404040, sand, flint));

		chickens.add(new ChickensRegistryItem(id("RedstoneChicken"), "RedstoneChicken",
				texture("redstone_chicken"), new ItemStack(Items.REDSTONE), 0xE60000, 0x800000, red, sand));

		ChickensRegistryItem glass = new ChickensRegistryItem(id("GlassChicken"), "GlassChicken",
				texture("glass_chicken"), new ItemStack(Items.GLASS), 0xFFFFFF, 0xEEEEFF,
				quartz, find(chickens, "RedstoneChicken"));
		chickens.add(glass);

		chickens.add(new ChickensRegistryItem(id("IronChicken"), "IronChicken",
				texture("iron_chicken"), new ItemStack(Items.IRON_INGOT), 0xFFFFCC, 0xFFCCCC, flint, white));

		chickens.add(new ChickensRegistryItem(id("CoalChicken"), "CoalChicken",
				texture("coal_chicken"), new ItemStack(Items.COAL), 0x262626, 0x000000, flint, log));

		ChickensRegistryItem brown = dye("BrownChicken", "brown_chicken", 0x835432, new ItemStack(Items.COCOA_BEANS))
				.setParentsNew(red, green);
		chickens.add(brown);

		chickens.add(new ChickensRegistryItem(id("GoldChicken"), "GoldChicken",
				texture("gold_chicken"), new ItemStack(Items.GOLD_INGOT), 0xCCCC00, 0xFFFF80,
				find(chickens, "IronChicken"), yellow));

		ChickensRegistryItem snowball = new ChickensRegistryItem(id("SnowballChicken"), "SnowballChicken",
				texture("snowball_chicken"), new ItemStack(Items.SNOWBALL), 0x33BBFF, 0x0088CC, blue, log)
				.setSpawnType(SpawnType.SNOW);
		chickens.add(snowball);

		chickens.add(new ChickensRegistryItem(id("WaterChicken"), "WaterChicken",
				texture("water_chicken"), ChickenItems.liquidEgg("water"), 0x000099, 0x8080FF,
				find(chickens, "GunpowderChicken"), snowball));

		ChickensRegistryItem lava = new ChickensRegistryItem(id("LavaChicken"), "LavaChicken",
				texture("lava_chicken"), ChickenItems.liquidEgg("lava"), 0xCC3300, 0xFFFF00,
				find(chickens, "CoalChicken"), quartz).setSpawnType(SpawnType.HELL);
		chickens.add(lava);

		ChickensRegistryItem clay = new ChickensRegistryItem(id("ClayChicken"), "ClayChicken",
				texture("clay_chicken"), new ItemStack(Items.CLAY_BALL), 0xCCCCCC, 0xBFBFBF, snowball, sand);
		chickens.add(clay);

		ChickensRegistryItem leather = new ChickensRegistryItem(id("LeatherChicken"), "LeatherChicken",
				texture("leather_chicken"), new ItemStack(Items.LEATHER), 0xA7A06C, 0x918E81,
				find(chickens, "StringChicken"), brown);
		chickens.add(leather);

		ChickensRegistryItem netherwart = new ChickensRegistryItem(id("NetherwartChicken"), "NetherwartChicken",
				texture("netherwart_chicken"), new ItemStack(Items.NETHER_WART), 0x800000, 0x331A00,
				brown, find(chickens, "GlowstoneChicken"));
		chickens.add(netherwart);

		ChickensRegistryItem diamond = new ChickensRegistryItem(id("DiamondChicken"), "DiamondChicken",
				texture("diamond_chicken"), new ItemStack(Items.DIAMOND), 0x99CCFF, 0xE6E6FF,
				glass, find(chickens, "GoldChicken"));
		chickens.add(diamond);

		chickens.add(new ChickensRegistryItem(id("BlazeChicken"), "BlazeChicken",
				texture("blaze_chicken"), new ItemStack(Items.BLAZE_ROD), 0xFFFF66, 0xFF3300,
				find(chickens, "GoldChicken"), lava));

		ChickensRegistryItem slime = new ChickensRegistryItem(id("SlimeChicken"), "SlimeChicken",
				texture("slime_chicken"), new ItemStack(Items.SLIME_BALL), 0x009933, 0x9A00BB, clay, green);
		chickens.add(slime);

		chickens.add(new ChickensRegistryItem(id("EnderChicken"), "EnderChicken",
				texture("ender_chicken"), new ItemStack(Items.ENDER_PEARL), 0x001A00, 0x001A33,
				diamond, netherwart));

		chickens.add(new ChickensRegistryItem(id("GhastChicken"), "GhastChicken",
				texture("ghast_chicken"), new ItemStack(Items.GHAST_TEAR), 0xFFFFCC, 0xFFFFFF,
				white, find(chickens, "BlazeChicken")));

		chickens.add(new ChickensRegistryItem(id("EmeraldChicken"), "EmeraldChicken",
				texture("emerald_chicken"), new ItemStack(Items.EMERALD), 0x00CC00, 0x003300,
				diamond, green));

		chickens.add(new ChickensRegistryItem(id("MagmaChicken"), "MagmaChicken",
				texture("magma_chicken"), new ItemStack(Items.MAGMA_CREAM), 0x1A0500, 0x000000,
				slime, find(chickens, "BlazeChicken")));

		chickens.add(new ChickensRegistryItem(id("pShardChicken"), "pShardChicken",
				texture("pshard_chicken"), new ItemStack(Items.PRISMARINE_SHARD), 0x4380AE, 0x9FC2BC,
				find(chickens, "WaterChicken"), blue));

		chickens.add(new ChickensRegistryItem(id("pCrystalChicken"), "pCrystalChicken",
				texture("pcrystal_chicken"), new ItemStack(Items.PRISMARINE_CRYSTALS), 0x4E6B61, 0xDFE6DC,
				find(chickens, "WaterChicken"), find(chickens, "EmeraldChicken")));

		chickens.add(new ChickensRegistryItem(id("obsidianChicken"), "obsidianChicken",
				texture("obsidian_chicken"), new ItemStack(Items.OBSIDIAN), 0x08080E, 0x463C60,
				find(chickens, "WaterChicken"), lava));

		chickens.add(new ChickensRegistryItem(id("soulSandChicken"), "soulSandChicken",
				texture("soulsand_chicken"), new ItemStack(Items.SOUL_SAND), 0x453345, 0xD53E88)
				.setSpawnType(SpawnType.HELL));

		return chickens;
	}

	private static ChickensRegistryItem find(List<ChickensRegistryItem> chickens, String name) {
		Identifier wanted = id(name);
		for (ChickensRegistryItem chicken : chickens) {
			if (chicken.getRegistryName().equals(wanted)) {
				return chicken;
			}
		}
		throw new IllegalStateException("Chicken " + name + " must be created before it is used as a parent");
	}
}
