package lucraft.mods.lucraftcore.utilities.recipes;

import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.LucraftCore;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Extractor and boiler recipes. Bundled recipes live in
 * {@code data/lucraftcore/machines/<type>/*.json} and addon packs can add more
 * with {@code extractor/*.json} and {@code boiler/*.json}.
 */
public final class MachineRecipes {

    private static final List<ExtractorRecipe> EXTRACTOR = new ArrayList<>();
    private static final List<BoilerRecipe> BOILER = new ArrayList<>();

    private MachineRecipes() {
    }

    public static List<ExtractorRecipe> extractorRecipes() {
        return List.copyOf(EXTRACTOR);
    }

    public static List<BoilerRecipe> boilerRecipes() {
        return List.copyOf(BOILER);
    }

    public static void load() {
        EXTRACTOR.clear();
        BOILER.clear();

        FabricLoader.getInstance().getModContainer(LucraftCore.MODID).ifPresent(container ->
                container.findPath("data/lucraftcore/machines").ifPresent(root -> {
                    readFolder(root.resolve("extractor"), "extractor");
                    readFolder(root.resolve("boiler"), "boiler");
                }));

        LucraftCore.LOGGER.info("Loaded {} extractor recipe(s) and {} boiler recipe(s)",
                EXTRACTOR.size(), BOILER.size());
    }

    private static void readFolder(Path folder, String type) {
        if (!Files.isDirectory(folder))
            return;
        try (var files = Files.list(folder)) {
            for (Path file : files.toList()) {
                if (!file.getFileName().toString().endsWith(".json"))
                    continue;
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    JsonObject json = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
                    Identifier id = Identifier.of(LucraftCore.MODID,
                            file.getFileName().toString().replace(".json", ""));
                    add(type, id, json);
                } catch (Exception e) {
                    LucraftCore.LOGGER.error("Could not read machine recipe {}", file, e);
                }
            }
        } catch (IOException e) {
            LucraftCore.LOGGER.error("Could not scan machine recipes in {}", folder, e);
        }
    }

    public static boolean add(String type, Identifier id, JsonObject json) {
        try {
            if ("extractor".equals(type)) {
                Item input = Registries.ITEM.get(Identifier.tryParse(json.get("input").getAsString()));
                if (input == null)
                    return false;
                int inputCount = json.has("count") ? json.get("count").getAsInt() : 1;
                JsonObject output = json.getAsJsonObject("output");
                Item outputItem = Registries.ITEM.get(Identifier.tryParse(output.get("item").getAsString()));
                if (outputItem == null)
                    return false;
                int outputCount = output.has("count") ? output.get("count").getAsInt() : 1;
                EXTRACTOR.add(new ExtractorRecipe(input, Math.max(1, inputCount),
                        new ItemStack(outputItem, Math.max(1, outputCount)),
                        json.has("energy") ? json.get("energy").getAsInt() : 1000,
                        json.has("time") ? json.get("time").getAsInt() : 100));
                return true;
            }
            if ("boiler".equals(type)) {
                Fluid input = Registries.FLUID.get(Identifier.tryParse(json.get("input").getAsString()));
                Fluid output = Registries.FLUID.get(Identifier.tryParse(json.get("output").getAsString()));
                if (input == null || input == Fluids.EMPTY || output == null || output == Fluids.EMPTY)
                    return false;
                BOILER.add(new BoilerRecipe(input,
                        json.has("input_amount") ? json.get("input_amount").getAsInt() : 1000,
                        output,
                        json.has("output_amount") ? json.get("output_amount").getAsInt() : 1000,
                        json.has("energy") ? json.get("energy").getAsInt() : 500,
                        json.has("time") ? json.get("time").getAsInt() : 100));
                return true;
            }
            LucraftCore.LOGGER.warn("Unknown machine recipe type '{}' for {}", type, id);
            return false;
        } catch (Exception e) {
            LucraftCore.LOGGER.error("Invalid {} recipe {}", type, id, e);
            return false;
        }
    }

    public static ExtractorRecipe findExtractor(ItemStack stack) {
        for (ExtractorRecipe recipe : EXTRACTOR)
            if (recipe.matches(stack))
                return recipe;
        return null;
    }

    public static BoilerRecipe findBoiler(Fluid fluid, int amount) {
        for (BoilerRecipe recipe : BOILER)
            if (recipe.matches(fluid, amount))
                return recipe;
        return null;
    }
}
