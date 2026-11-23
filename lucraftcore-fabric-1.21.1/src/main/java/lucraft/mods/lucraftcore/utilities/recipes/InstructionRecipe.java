package lucraft.mods.lucraftcore.utilities.recipes;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InstructionRecipe {

    private static final Map<Identifier, InstructionRecipe> RECIPES = new LinkedHashMap<>();

    private final Identifier id;
    private final ItemStack output;
    private final List<ItemStack> requirements;

    public InstructionRecipe(Identifier id, ItemStack output, List<ItemStack> requirements) {
        this.id = id;
        this.output = output;
        this.requirements = requirements;
    }

    public Identifier getId() {
        return id;
    }

    public ItemStack getOutput() {
        return output;
    }

    public List<ItemStack> getRequirements() {
        return requirements;
    }

    public boolean hasRequirements(net.minecraft.entity.player.PlayerInventory inventory) {
        for (ItemStack requirement : requirements) {
            int found = 0;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, requirement))
                    found += stack.getCount();
            }
            if (found < requirement.getCount())
                return false;
        }
        return true;
    }

    public void consume(net.minecraft.entity.player.PlayerInventory inventory) {
        for (ItemStack requirement : requirements) {
            int remaining = requirement.getCount();
            for (int i = 0; i < inventory.size() && remaining > 0; i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isEmpty() || !ItemStack.areItemsAndComponentsEqual(stack, requirement))
                    continue;
                int taken = Math.min(remaining, stack.getCount());
                stack.decrement(taken);
                remaining -= taken;
            }
        }
        inventory.markDirty();
    }

    public static void register(InstructionRecipe recipe) {
        if (RECIPES.containsKey(recipe.getId())) {
            LucraftCore.LOGGER.error("Duplicate instruction recipe {}", recipe.getId());
            return;
        }
        RECIPES.put(recipe.getId(), recipe);
    }

    public static InstructionRecipe byId(Identifier id) {
        return RECIPES.get(id);
    }

    public static List<InstructionRecipe> getAll() {
        return new ArrayList<>(RECIPES.values());
    }

    public static int size() {
        return RECIPES.size();
    }
}
