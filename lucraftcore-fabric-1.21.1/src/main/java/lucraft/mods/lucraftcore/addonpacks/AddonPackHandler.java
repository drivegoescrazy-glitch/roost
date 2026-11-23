package lucraft.mods.lucraftcore.addonpacks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.JsonSuperpowerReader;
import lucraft.mods.lucraftcore.superpowers.Superpower;
import lucraft.mods.lucraftcore.superpowers.SuperpowerRegistry;
import lucraft.mods.lucraftcore.superpowers.suitsets.JsonSuitSetReader;
import lucraft.mods.lucraftcore.utilities.recipes.InstructionRecipeReader;
import lucraft.mods.lucraftcore.utilities.recipes.MachineRecipes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Port of the addon pack system. Packs live in config/lucraftcore/addonpacks as
 * folders or zip/jar files and carry an "addonpack.mcmeta" file.
 */
public class AddonPackHandler {

    private static final String META_FILE = "addonpack.mcmeta";
    private static final List<AddonPackInfo> PACKS = new ArrayList<>();
    private static int instructionRecipes;
    private static int suitSets;
    private static int superpowers;

    public static Path getAddonPacksDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("lucraftcore").resolve("addonpacks");
    }

    public static List<AddonPackInfo> getPacks() {
        return PACKS;
    }

    public static int getInstructionRecipeCount() {
        return instructionRecipes;
    }

    public static int getSuitSetCount() {
        return suitSets;
    }

    public static int getSuperpowerCount() {
        return superpowers;
    }

    public static void load() {
        Path dir = getAddonPacksDir();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            LucraftCore.LOGGER.error("Could not create the addon pack folder", e);
            return;
        }

        instructionRecipes = 0;
        suitSets = 0;
        superpowers = 0;
        SuperpowerRegistry.clear();
        try (var entries = Files.list(dir)) {
            for (Path path : entries.toList()) {
                if (Files.isDirectory(path))
                    processFolder(path);
                else if (isArchive(path.getFileName().toString()))
                    processArchive(path);
            }
        } catch (IOException e) {
            LucraftCore.LOGGER.error("Could not read the addon pack folder", e);
        }

        LucraftCore.LOGGER.info("Loaded {} addon pack(s) with {} instruction recipe(s), {} suit set(s) and {} superpower(s)",
                PACKS.size(), instructionRecipes, suitSets, superpowers);
    }

    private static boolean isArchive(String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".zip") || lower.endsWith(".jar");
    }

    private static void processFolder(Path folder) {
        Path meta = folder.resolve(META_FILE);
        if (!Files.exists(meta)) {
            LucraftCore.LOGGER.warn("Addon pack '{}' is missing {}", folder.getFileName(), META_FILE);
            return;
        }
        try (Reader reader = Files.newBufferedReader(meta, StandardCharsets.UTF_8)) {
            PACKS.add(readInfo(reader, folder.getFileName().toString()));
        } catch (Exception e) {
            LucraftCore.LOGGER.error("Could not read addon pack '{}'", folder.getFileName(), e);
            return;
        }

        try (var files = Files.walk(folder)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                String relative = folder.relativize(file).toString().replace('\\', '/');
                if (!relative.toLowerCase().endsWith(".json"))
                    return;
                String id = relative.substring(0, relative.length() - 5);
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    readFile(relative, id, reader, folder.getFileName().toString());
                } catch (Exception e) {
                    LucraftCore.LOGGER.error("Could not read {}", relative, e);
                }
            });
        } catch (IOException e) {
            LucraftCore.LOGGER.error("Could not scan addon pack '{}'", folder.getFileName(), e);
        }
    }

    private static void processArchive(Path archive) {
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            ZipEntry meta = zip.getEntry(META_FILE);
            if (meta == null) {
                LucraftCore.LOGGER.warn("Addon pack '{}' is missing {}", archive.getFileName(), META_FILE);
                return;
            }
            try (InputStream in = zip.getInputStream(meta);
                 Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                PACKS.add(readInfo(reader, archive.getFileName().toString()));
            }

            var zipEntries = zip.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry entry = zipEntries.nextElement();
                if (entry.isDirectory() || !entry.getName().toLowerCase().endsWith(".json"))
                    continue;
                String name = entry.getName();
                String id = name.substring(0, name.length() - 5);
                try (InputStream in = zip.getInputStream(entry);
                     Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    readFile(name, id, reader, archive.getFileName().toString());
                }
            }
        } catch (IOException e) {
            LucraftCore.LOGGER.error("Could not read addon pack '{}'", archive.getFileName(), e);
        }
    }

    private static void readFile(String relative, String id, Reader reader, String pack) {
        if (relative.startsWith("instructionrecipes/")) {
            String namespace = sanitize(pack);
            Identifier recipeId = Identifier.tryParse(namespace + ":" + id.substring("instructionrecipes/".length()));
            if (recipeId == null)
                return;
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            instructionRecipes += InstructionRecipeReader.read(recipeId, json);
        } else if (relative.startsWith("superpowers/") || relative.startsWith("powers/")) {
            String prefix = relative.startsWith("superpowers/") ? "superpowers/" : "powers/";
            Identifier powerId = Identifier.tryParse(sanitize(pack) + ":" + id.substring(prefix.length()));
            if (powerId == null)
                return;
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            Superpower superpower = JsonSuperpowerReader.read(powerId, json);
            if (superpower != null) {
                SuperpowerRegistry.register(superpower);
                superpowers++;
                LucraftCore.LOGGER.info("Registered superpower {} ({} level(s), {} ability config(s))",
                        powerId, superpower.getMaxLevel(), superpower.getAbilities().size());
            }
        } else if (relative.startsWith("extractor/") || relative.startsWith("boiler/")) {
            String type = relative.startsWith("extractor/") ? "extractor" : "boiler";
            String prefix = type + "/";
            Identifier recipeId = Identifier.tryParse(sanitize(pack) + ":" + id.substring(prefix.length()));
            if (recipeId == null)
                return;
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            MachineRecipes.add(type, recipeId, json);
        } else if (relative.startsWith("suitsets/")) {
            Identifier suitId = Identifier.tryParse(sanitize(pack) + ":" + id.substring("suitsets/".length()));
            if (suitId == null)
                return;
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (JsonSuitSetReader.read(suitId, json) != null)
                suitSets++;
        }
    }

    private static AddonPackInfo readInfo(Reader reader, String fileName) {
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        String name = json.has("name") ? json.get("name").getAsString() : fileName;
        String author = json.has("author") ? json.get("author").getAsString() : "unknown";
        String description = json.has("description") ? json.get("description").getAsString() : "";
        return new AddonPackInfo(name, author, description, fileName);
    }

    private static String sanitize(String input) {
        String cleaned = input.toLowerCase().replaceAll("[^a-z0-9_.-]", "_");
        return cleaned.isEmpty() ? "addonpack" : cleaned;
    }
}
