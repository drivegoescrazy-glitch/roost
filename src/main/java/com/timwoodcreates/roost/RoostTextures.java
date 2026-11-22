package com.timwoodcreates.roost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated from the textures shipped in assets/roost/textures/block/chicken.
 * The order is also the block state and item model override index, so keep them in sync.
 */
public final class RoostTextures {
	public static final List<String> STOCK_TEXTURES = List.of(
			"advancedprocessorchicken",
			"aluminumchicken",
			"amberchicken",
			"amethystchicken",
			"arditechicken",
			"basalzrodchicken",
			"basicprocessorchicken",
			"blackchicken",
			"blackquartzchicken",
			"blazechicken",
			"blitzrodchicken",
			"blizzrodchicken",
			"bloodslime",
			"bluechicken",
			"blueslime",
			"blutoniumchicken",
			"boopbeepchick",
			"brasschicken",
			"bronzechicken",
			"brownchicken",
			"bulletchicken",
			"cablechicken",
			"chickenosto",
			"chromechicken",
			"cinnabarchicken",
			"claychicken",
			"coalchicken",
			"cobaltchicken",
			"conductiveironchicken",
			"constantanchicken",
			"constructioncorechicken",
			"copperchicken",
			"cupronickelchicken",
			"cyanchicken",
			"cyanitechicken",
			"darkgemchicken",
			"darksteelchicken",
			"demonmetalchicken",
			"destructioncorechicken",
			"diamantinecrystalchicken",
			"diamondchicken",
			"draconiumawakenedchicken",
			"draconiumchicken",
			"electricalsteelchicken",
			"electrumchicken",
			"elementiumchicken",
			"emeradiccrystalchicken",
			"emeraldchicken",
			"enderchicken",
			"enderiumchicken",
			"energeticalloychicken",
			"enoricrystalchicken",
			"flintchicken",
			"funwaychick",
			"garnetchicken",
			"ghastchicken",
			"glasschicken",
			"glowstonechicken",
			"goldchicken",
			"graphitechicken",
			"graychicken",
			"greenchicken",
			"gunpowderchicken",
			"improvedprocessorchicken",
			"invarchicken",
			"iridiumchicken",
			"ironchicken",
			"knightslimechicken",
			"lavachicken",
			"leadchicken",
			"leatherchicken",
			"lightbluechicken",
			"limechicken",
			"logchicken",
			"ludicritechicken",
			"lumiumchicken",
			"lunarreactivedustchicken",
			"magentachicken",
			"magicalwoodchicken",
			"magmachicken",
			"magmaslime",
			"malachitechicken",
			"manasteelchicken",
			"manyullynchicken",
			"mithrilchicken",
			"moonstonechicken",
			"mramericanchick",
			"netherwartchicken",
			"nickelchicken",
			"obsidianchicken",
			"orangechicken",
			"osmiumchicken",
			"paliscrystalchicken",
			"pcrystalchicken",
			"peridotchicken",
			"pigironchicken",
			"pinkchicken",
			"pinkslimechicken",
			"platinumchicken",
			"pshardchicken",
			"pulsatingironchicken",
			"purplechicken",
			"purpleslime",
			"quartzchicken",
			"quartzenrichedironchicken",
			"redchicken",
			"redstonealloychicken",
			"redstonechicken",
			"redstonecrystalchicken",
			"refinedironchicken",
			"restoniacrystalchicken",
			"richslagchicken",
			"rubberchicken",
			"rubychicken",
			"saltchicken",
			"saltpeterchicken",
			"sandchicken",
			"sapphirechicken",
			"signalumchicken",
			"siliconchicken",
			"silverdyechicken",
			"silverorechicken",
			"slagchicken",
			"slimechicken",
			"smartchicken",
			"snowballchicken",
			"soulariumchicken",
			"soulsandchicken",
			"steelchicken",
			"stoneburntchicken",
			"stringchicken",
			"sulfurchicken",
			"tanzanitechicken",
			"terrasteelchicken",
			"tinchicken",
			"titaniumchicken",
			"topazchicken",
			"tungstenchicken",
			"tungstensteelchicken",
			"unknownchicken",
			"uraniumchicken",
			"vanilla",
			"vibrantalloychicken",
			"vinteumchicken",
			"voidcrystalchicken",
			"waterchicken",
			"whitechicken",
			"xpchicken",
			"yelloriumchicken",
			"yellowchicken",
			"yellowgarnetchicken",
			"zincchicken");

	public static final int MAX_MODEL_INDEX = STOCK_TEXTURES.size();

	private static final Map<String, Integer> MODEL_INDICES = new HashMap<>();

	static {
		for (int i = 0; i < STOCK_TEXTURES.size(); i++) {
			MODEL_INDICES.put(STOCK_TEXTURES.get(i), i + 1);
		}
	}

	private RoostTextures() {
	}

	public static int modelIndex(String texture) {
		if (texture != null) {
			Integer index = MODEL_INDICES.get(texture);
			if (index != null) {
				return index;
			}
		}
		Integer fallback = MODEL_INDICES.get("unknownchicken");
		return fallback == null ? 0 : fallback;
	}

	public static String textureAt(int index) {
		return index > 0 && index <= STOCK_TEXTURES.size() ? STOCK_TEXTURES.get(index - 1) : null;
	}
}
