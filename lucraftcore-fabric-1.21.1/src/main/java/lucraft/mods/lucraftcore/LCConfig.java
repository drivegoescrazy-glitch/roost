package lucraft.mods.lucraftcore;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Same options as the original config, stored as a properties file. */
public class LCConfig {

    public static boolean radiation = false;
    public static boolean materials = true;
    public static boolean utilities = true;
    public static boolean karma = true;
    public static boolean sizeChanging = true;
    public static boolean superpowers = true;
    /** Energy, fluid, inventory and networking layer (needed by the machines). */
    public static boolean engine = true;

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("lucraftcore.properties");
        Properties properties = new Properties();
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                properties.load(in);
            } catch (IOException e) {
                LucraftCore.LOGGER.error("Could not read config", e);
            }
        }

        radiation = Boolean.parseBoolean(properties.getProperty("materials.radiation", "false"));
        materials = Boolean.parseBoolean(properties.getProperty("modules.materials", "true"));
        utilities = Boolean.parseBoolean(properties.getProperty("modules.utilities", "true"));
        karma = Boolean.parseBoolean(properties.getProperty("modules.karma", "true"));
        sizeChanging = Boolean.parseBoolean(properties.getProperty("modules.sizechanging", "true"));
        superpowers = Boolean.parseBoolean(properties.getProperty("modules.superpowers", "true"));
        engine = Boolean.parseBoolean(properties.getProperty("modules.engine", "true"));

        properties.setProperty("modules.materials", String.valueOf(materials));
        properties.setProperty("modules.utilities", String.valueOf(utilities));
        properties.setProperty("modules.karma", String.valueOf(karma));
        properties.setProperty("modules.sizechanging", String.valueOf(sizeChanging));
        properties.setProperty("modules.superpowers", String.valueOf(superpowers));
        properties.setProperty("modules.engine", String.valueOf(engine));
        properties.setProperty("materials.radiation", String.valueOf(radiation));
        try (OutputStream out = Files.newOutputStream(path)) {
            properties.store(out, "Lucraft: Core (Fabric) configuration");
        } catch (IOException e) {
            LucraftCore.LOGGER.error("Could not write config", e);
        }
    }
}
