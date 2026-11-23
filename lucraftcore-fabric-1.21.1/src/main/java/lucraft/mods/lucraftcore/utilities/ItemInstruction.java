package lucraft.mods.lucraftcore.utilities;

import com.mojang.serialization.Codec;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.utilities.recipes.InstructionRecipe;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ItemInstruction extends Item {

    public static final ComponentType<List<String>> RECIPES = Registry.register(Registries.DATA_COMPONENT_TYPE,
            LucraftCore.id("instruction_recipes"),
            ComponentType.<List<String>>builder().codec(Codec.list(Codec.STRING)).build());

    public ItemInstruction() {
        super(new Item.Settings().maxCount(1));
    }

    public static ItemStack createStack(InstructionRecipe recipe) {
        return createStack(List.of(recipe.getId()));
    }

    public static ItemStack createStack(List<Identifier> ids) {
        ItemStack stack = new ItemStack(UtilitiesItems.INSTRUCTION);
        List<String> list = new ArrayList<>();
        for (Identifier id : ids)
            list.add(id.toString());
        stack.set(RECIPES, list);
        return stack;
    }

    public static List<InstructionRecipe> getRecipes(ItemStack stack) {
        List<String> ids = stack.get(RECIPES);
        List<InstructionRecipe> recipes = new ArrayList<>();
        if (ids == null)
            return recipes;
        for (String value : ids) {
            Identifier id = Identifier.tryParse(value);
            InstructionRecipe recipe = id == null ? null : InstructionRecipe.byId(id);
            if (recipe != null)
                recipes.add(recipe);
        }
        return recipes;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        List<InstructionRecipe> recipes = getRecipes(stack);
        if (recipes.isEmpty()) {
            tooltip.add(Text.translatable("lucraftcore.info.noinstructionrecipes").formatted(Formatting.GRAY));
            return;
        }
        for (InstructionRecipe recipe : recipes) {
            tooltip.add(recipe.getOutput().getName().copy().formatted(Formatting.BOLD));
            for (ItemStack requirement : recipe.getRequirements())
                tooltip.add(Text.literal(" - " + requirement.getCount() + "x ").append(requirement.getName()).formatted(Formatting.GRAY));
        }
    }
}
