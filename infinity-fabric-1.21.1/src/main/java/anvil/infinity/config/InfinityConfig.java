package anvil.infinity.config;

import anvil.infinity.Infinity;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Configuration of the original mod, as a properties file. Keys are kept
 * identical to the 1.12 config ("Power Stone color", ...) so existing values
 * carry over. Client keys are the sixteen stone colours; general keys mirror
 * the forge config categories.
 */
public final class InfinityConfig {

    // worldgen
    public static boolean worldgenEnabled = true;
    public static String powerDim = "0";
    public static String spaceDim = "0";
    public static String realityDim = "0";
    public static String soulDim = "0";
    public static String timeDim = "0";
    public static String mindDim = "0";
    public static boolean worldgenDebug = false;

    // general
    public static boolean soulHealthBoost = true;
    public static boolean useSHRealityAbilities = false;
    public static boolean sizeChanging = false;
    public static float size = 0.1F;
    public static boolean snapCreative = false;
    public static boolean heAbilities = true;
    public static boolean stAbilities = true;
    public static int powerCooldown = 100;

    // client colours
    public static int powerStoneColor = 12790481;
    public static int powerStoneShine = 15805183;
    public static int realityStoneColor = 16711984;
    public static int realityStoneShine = 16711984;
    public static int timeStoneColor = 1298261;
    public static int timeStoneShine = 1238898;
    public static int mindStoneColor = 16765696;
    public static int mindStoneShine = 14352138;
    public static int soulStoneColor = 14578432;
    public static int soulStoneShine = 16747264;
    public static int spaceStoneColor = 153087;
    public static int spaceStoneShine = 6997503;

    private InfinityConfig() {
    }

    public static int color(EnumInfinityStone stone) {
        return switch (stone) {
            case POWER -> powerStoneColor;
            case REALITY -> realityStoneColor;
            case TIME -> timeStoneColor;
            case MIND -> mindStoneColor;
            case SOUL -> soulStoneColor;
            case SPACE -> spaceStoneColor;
        };
    }

    public static int shine(EnumInfinityStone stone) {
        return switch (stone) {
            case POWER -> powerStoneShine;
            case REALITY -> realityStoneShine;
            case TIME -> timeStoneShine;
            case MIND -> mindStoneShine;
            case SOUL -> soulStoneShine;
            case SPACE -> spaceStoneShine;
        };
    }

    public static String dimension(EnumInfinityStone stone) {
        return switch (stone) {
            case POWER -> powerDim;
            case REALITY -> realityDim;
            case TIME -> timeDim;
            case MIND -> mindDim;
            case SOUL -> soulDim;
            case SPACE -> spaceDim;
        };
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("infinity.properties");
        Properties properties = new Properties();
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                properties.load(in);
            } catch (IOException e) {
                Infinity.LOGGER.error("Could not read infinity.properties", e);
            }
        }

        worldgenEnabled = bool(properties, "Enable Stone worldgen", true);
        powerDim = properties.getProperty("Power Stone Dimension", "0");
        spaceDim = properties.getProperty("Space Stone Dimension", "0");
        realityDim = properties.getProperty("Reality Stone Dimension", "0");
        soulDim = properties.getProperty("Soul Stone Dimension", "0");
        timeDim = properties.getProperty("Time Stone Dimension", "0");
        mindDim = properties.getProperty("Mind Stone Dimension", "0");
        worldgenDebug = bool(properties, "Worldgen Debug", false);

        soulHealthBoost = bool(properties, "Soul Stone health boost", true);
        useSHRealityAbilities = bool(properties, "SH Reality Stone abilities", false);
        sizeChanging = bool(properties, "Size changing", false);
        size = (float) number(properties, "Size", 0.1D);
        snapCreative = bool(properties, "Can Snap change Gamemode", false);
        heAbilities = bool(properties, "HeroesExpansion Abilities", true);
        stAbilities = bool(properties, "StarTech Abilities", true);
        powerCooldown = (int) number(properties, "Power Burst Cooldown", 100D);

        powerStoneColor = (int) number(properties, "Power Stone color", 12790481D);
        powerStoneShine = (int) number(properties, "Power Stone shine", 15805183D);
        realityStoneColor = (int) number(properties, "Reality Stone color", 16711984D);
        realityStoneShine = (int) number(properties, "Reality Stone shine", 16711984D);
        timeStoneColor = (int) number(properties, "Time Stone color", 1298261D);
        timeStoneShine = (int) number(properties, "Time Stone shine", 1238898D);
        mindStoneColor = (int) number(properties, "Mind Stone color", 16765696D);
        mindStoneShine = (int) number(properties, "Mind Stone shine", 14352138D);
        soulStoneColor = (int) number(properties, "Soul Stone color", 14578432D);
        soulStoneShine = (int) number(properties, "Soul Stone shine", 16747264D);
        spaceStoneColor = (int) number(properties, "Space Stone color", 153087D);
        spaceStoneShine = (int) number(properties, "Space Stone shine", 6997503D);

        properties.setProperty("Enable Stone worldgen", String.valueOf(worldgenEnabled));
        properties.setProperty("Power Stone Dimension", powerDim);
        properties.setProperty("Space Stone Dimension", spaceDim);
        properties.setProperty("Reality Stone Dimension", realityDim);
        properties.setProperty("Soul Stone Dimension", soulDim);
        properties.setProperty("Time Stone Dimension", timeDim);
        properties.setProperty("Mind Stone Dimension", mindDim);
        properties.setProperty("Worldgen Debug", String.valueOf(worldgenDebug));
        properties.setProperty("Soul Stone health boost", String.valueOf(soulHealthBoost));
        properties.setProperty("SH Reality Stone abilities", String.valueOf(useSHRealityAbilities));
        properties.setProperty("Size changing", String.valueOf(sizeChanging));
        properties.setProperty("Size", String.valueOf(size));
        properties.setProperty("Can Snap change Gamemode", String.valueOf(snapCreative));
        properties.setProperty("HeroesExpansion Abilities", String.valueOf(heAbilities));
        properties.setProperty("StarTech Abilities", String.valueOf(stAbilities));
        properties.setProperty("Power Burst Cooldown", String.valueOf(powerCooldown));
        properties.setProperty("Power Stone color", String.valueOf(powerStoneColor));
        properties.setProperty("Power Stone shine", String.valueOf(powerStoneShine));
        properties.setProperty("Reality Stone color", String.valueOf(realityStoneColor));
        properties.setProperty("Reality Stone shine", String.valueOf(realityStoneShine));
        properties.setProperty("Time Stone color", String.valueOf(timeStoneColor));
        properties.setProperty("Time Stone shine", String.valueOf(timeStoneShine));
        properties.setProperty("Mind Stone color", String.valueOf(mindStoneColor));
        properties.setProperty("Mind Stone shine", String.valueOf(mindStoneShine));
        properties.setProperty("Soul Stone color", String.valueOf(soulStoneColor));
        properties.setProperty("Soul Stone shine", String.valueOf(soulStoneShine));
        properties.setProperty("Space Stone color", String.valueOf(spaceStoneColor));
        properties.setProperty("Space Stone shine", String.valueOf(spaceStoneShine));

        try (OutputStream out = Files.newOutputStream(path)) {
            properties.store(out, "Infinityraft configuration (Fabric 1.21.1 port)");
        } catch (IOException e) {
            Infinity.LOGGER.error("Could not write infinity.properties", e);
        }
    }

    private static boolean bool(Properties properties, String key, boolean fallback) {
        String value = properties.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value);
    }

    private static double number(Properties properties, String key, double fallback) {
        String value = properties.getProperty(key);
        if (value == null)
            return fallback;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
