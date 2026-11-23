package anvil.infinity.helpers;

import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/** Whether a living entity wears a gauntlet that contains a given stone. */
public final class GauntletHelper {

    private GauntletHelper() {
    }

    public static boolean hasStone(LivingEntity entity, EnumInfinityStone stone) {
        return hasStoneIn(entity, Hand.MAIN_HAND, stone) || hasStoneIn(entity, Hand.OFF_HAND, stone);
    }

    private static boolean hasStoneIn(LivingEntity entity, Hand hand, EnumInfinityStone stone) {
        ItemStack stack = entity.getStackInHand(hand);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfinityGauntlet))
            return false;
        return ItemInfinityGauntlet.hasStone(stack, stone);
    }

    public static boolean hasPowerStone(LivingEntity entity) {
        return hasStone(entity, EnumInfinityStone.POWER);
    }

    public static boolean hasSpaceStone(LivingEntity entity) {
        return hasStone(entity, EnumInfinityStone.SPACE);
    }

    public static boolean hasRealityStone(LivingEntity entity) {
        return hasStone(entity, EnumInfinityStone.REALITY);
    }

    public static boolean hasSoulStone(LivingEntity entity) {
        return hasStone(entity, EnumInfinityStone.SOUL);
    }

    public static boolean hasTimeStone(LivingEntity entity) {
        return hasStone(entity, EnumInfinityStone.TIME);
    }

    public static boolean hasMindStone(LivingEntity entity) {
        return hasStone(entity, EnumInfinityStone.MIND);
    }

    public static boolean hasFullGauntlet(LivingEntity entity) {
        return hasPowerStone(entity) && hasSpaceStone(entity) && hasRealityStone(entity)
                && hasSoulStone(entity) && hasTimeStone(entity) && hasMindStone(entity);
    }
}
