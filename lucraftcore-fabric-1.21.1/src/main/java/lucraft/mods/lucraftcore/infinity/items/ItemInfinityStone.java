package lucraft.mods.lucraftcore.infinity.items;

import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * Base for infinity stones. Stones are addon-pack content: a pack subclasses
 * this item, returns its stone type and optionally adds gauntlet abilities.
 */
public abstract class ItemInfinityStone extends ItemIndestructible {

    public abstract EnumInfinityStone getType();

    /** Container stones (e.g. the empty gauntlet cast) cannot go into the gauntlet. */
    public boolean isContainer() {
        return false;
    }

    public int getEnergyPerTick(ItemStack stack) {
        return 100_000;
    }

    /** Called every tick while this stone sits in the selected gauntlet. */
    public void onGauntletTick(PlayerEntity player, World world, ItemStack gauntlet) {
    }

    /** Ability ids granted while the stone is in a held gauntlet. */
    public List<String> getGauntletAbilities() {
        return List.of();
    }

    /** Ability ids granted while the stone itself is held, outside a gauntlet. */
    public List<String> getDefaultAbilities() {
        return List.of();
    }
}
