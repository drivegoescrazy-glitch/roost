package lucraft.mods.lucraftcore.utilities.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class InstructionRecipeReader {

    /**
     * Format (both the original and the modern item syntax are accepted):
     * {"output": {"item": "lucraftcore:hammer", "amount": 1, "data": 0},
     *  "requirements": [{"item": "minecraft:iron_ingot", "amount": 2}]}
     */
    public static int read(Identifier id, JsonObject json) {
        try {
            ItemStack output = stack(json.getAsJsonObject("output"));
            if (output.isEmpty())
                return 0;
            List<ItemStack> requirements = new ArrayList<>();
            JsonArray array = json.has("requirements") ? json.getAsJsonArray("requirements") : new JsonArray();
            for (JsonElement element : array) {
                ItemStack stack = stack(element.getAsJsonObject());
                if (!stack.isEmpty())
                    requirements.add(stack);
            }
            InstructionRecipe.register(new InstructionRecipe(id, output, requirements));
            return 1;
        } catch (Exception e) {
            LucraftCore.LOGGER.error("Broken instruction recipe {}: {}", id, e.getMessage());
            return 0;
        }
    }

    private static ItemStack stack(JsonObject json) {
        if (json == null || !json.has("item") && !json.has("id"))
            return ItemStack.EMPTY;
        String name = json.has("id") ? json.get("id").getAsString() : json.get("item").getAsString();
        Identifier itemId = Identifier.tryParse(name);
        Item item = itemId == null ? null : Registries.ITEM.get(itemId);
        if (item == null)
            return ItemStack.EMPTY;
        int count = json.has("count") ? json.get("count").getAsInt()
                : json.has("amount") ? json.get("amount").getAsInt() : 1;
        return new ItemStack(item, Math.max(1, count));
    }
}
