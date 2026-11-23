package anvil.infinity.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Optional addon integration. The original supported HeroesExpansion,
 * SpeedsterHeroes and StarTech through Forge proxies; none of them exist for
 * Fabric 1.21.1, so detection is by mod id and every hook degrades to "absent".
 * When a Fabric port of one of them registers the abilities under the same ids,
 * the stones pick them up without further changes.
 */
public final class CompatHandler {

    public static final String HEROES_EXPANSION = "heroesexpansion";
    public static final String SPEEDSTER_HEROES = "speedsterheroes";
    public static final String STAR_TECH = "star-tech";

    private static boolean heroesExpansion;
    private static boolean speedsterHeroes;
    private static boolean starTech;

    private CompatHandler() {
    }

    public static void check() {
        FabricLoader loader = FabricLoader.getInstance();
        heroesExpansion = loader.isModLoaded(HEROES_EXPANSION);
        speedsterHeroes = loader.isModLoaded(SPEEDSTER_HEROES);
        starTech = loader.isModLoaded(STAR_TECH);
    }

    public static boolean isHeroesExpansion() {
        return heroesExpansion;
    }

    public static boolean isSpeedsterHeroes() {
        return speedsterHeroes;
    }

    public static boolean isStarTech() {
        return starTech;
    }

    /** HeroesExpansion's "god of thunder" located the stones in the original. */
    public static boolean heroesExpansionCanLocate(ServerPlayerEntity player) {
        return false;
    }
}
