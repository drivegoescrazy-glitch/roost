package lucraft.mods.lucraftcore.infinity.items;

import net.minecraft.item.Item;

/** Base for infinity equipment: one per stack and immune to fire and lava. */
public class ItemIndestructible extends Item {

    public ItemIndestructible() {
        super(new Item.Settings().maxCount(1).fireproof());
    }
}
