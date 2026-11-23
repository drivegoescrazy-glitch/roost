package lucraft.mods.lucraftcore.utilities.recipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** One extractor recipe: item in, item out, paid for with FE over time. */
public record ExtractorRecipe(Item input, int inputCount, ItemStack output, int energy, int time) {

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && stack.isOf(input) && stack.getCount() >= inputCount;
    }

    public int energyPerTick() {
        return time <= 0 ? energy : Math.max(1, energy / time);
    }
}
