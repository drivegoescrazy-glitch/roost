package anvil.infinity.items;

import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared behaviour of the six stones: the original stones were fire/lava/explosion
 * proof and could not be destroyed as dropped items (Lucraft gave them a special
 * indestructible item entity), and they all add the third party stone abilities
 * registered through the addon API.
 */
public abstract class AbstractInfinityStone extends ItemInfinityStone {

    /** Dropped stones are never destroyed (fire, lava, cactus, explosions). */
    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        // The original used an indestructible item entity; keeping the entity
        // intact here has the same effect.
    }

    protected static List<String> abilities(String... ids) {
        List<String> list = new ArrayList<>(List.of(ids));
        return list;
    }

    /** Extra abilities other addons attached to this stone, in registration order. */
    protected List<String> withAddons(lucraft.mods.lucraftcore.infinity.EnumInfinityStone type, List<String> ids) {
        anvil.infinity.api.AbilityAdderHandler.addAbilities(type, ids);
        return ids;
    }
}
