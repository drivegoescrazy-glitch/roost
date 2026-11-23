package lucraft.mods.lucraftcore.utilities;

import net.minecraft.item.Item;

/**
 * Crafting tool. The original kept itself in the grid through Forge's container-item
 * mechanic; here {@link lucraft.mods.lucraftcore.mixin.CraftingResultSlotMixin} does that.
 */
public class ItemHammer extends Item {

    public ItemHammer() {
        super(new Item.Settings().maxCount(1));
    }
}
