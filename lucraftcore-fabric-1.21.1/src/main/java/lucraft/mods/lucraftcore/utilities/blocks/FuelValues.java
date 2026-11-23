package lucraft.mods.lucraftcore.utilities.blocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

/**
 * Fuel burn times for the furnace generator. Vanilla's fuel map is not exposed
 * through a public API, so common fuels are listed explicitly and tags cover
 * the rest.
 */
public final class FuelValues {

    private FuelValues() {
    }

    public static int burnTime(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return 0;
        Item item = stack.getItem();
        if (item == Items.LAVA_BUCKET)
            return 20000;
        if (item == Items.COAL || item == Items.CHARCOAL)
            return 1600;
        if (item == Items.COAL_BLOCK)
            return 16000;
        if (item == Items.BLAZE_ROD)
            return 2400;
        if (item == Items.DRIED_KELP_BLOCK)
            return 4001;
        if (item == Items.MAGMA_CREAM)
            return 800;
        if (item == Items.STICK)
            return 100;
        if (stack.isIn(ItemTags.LOGS))
            return 300;
        if (stack.isIn(ItemTags.PLANKS))
            return 300;
        if (stack.isIn(ItemTags.WOODEN_SLABS))
            return 150;
        if (stack.isIn(ItemTags.SAPLINGS))
            return 100;
        if (item == Items.CRAFTING_TABLE || item == Items.BOOKSHELF || item == Items.CHEST)
            return 300;

        Identifier id = Registries.ITEM.getId(item);
        if ("lucraftcore".equals(id.getNamespace()))
            return 0;
        return 0;
    }

    public static boolean isFuel(ItemStack stack) {
        return burnTime(stack) > 0;
    }
}
