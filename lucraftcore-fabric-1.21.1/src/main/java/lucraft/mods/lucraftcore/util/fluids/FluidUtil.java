package lucraft.mods.lucraftcore.util.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Bucket interaction helpers for tanks. */
public final class FluidUtil {

    public static final int BUCKET_VOLUME = 1000;

    private FluidUtil() {
    }

    /** Fluid held by a filled bucket, or {@link Fluids#EMPTY}. */
    public static Fluid fluidOfBucket(ItemStack stack) {
        if (stack.isEmpty())
            return Fluids.EMPTY;
        if (stack.isOf(Items.WATER_BUCKET))
            return Fluids.WATER;
        if (stack.isOf(Items.LAVA_BUCKET))
            return Fluids.LAVA;
        return Fluids.EMPTY;
    }

    public static boolean isFilledBucket(ItemStack stack) {
        return fluidOfBucket(stack) != Fluids.EMPTY;
    }

    public static boolean isEmptyBucket(ItemStack stack) {
        return !stack.isEmpty() && stack.isOf(Items.BUCKET);
    }

    /**
     * Empties a water or lava bucket into the tank. Returns the resulting
     * container stack, or an empty stack when the transfer is not possible.
     */
    public static ItemStack emptyBucketIntoTank(ItemStack bucket, FluidTank tank) {
        if (tank == null)
            return ItemStack.EMPTY;
        Fluid fluid = fluidOfBucket(bucket);
        if (fluid == Fluids.EMPTY)
            return ItemStack.EMPTY;
        if (tank.fill(fluid, BUCKET_VOLUME, true) < BUCKET_VOLUME)
            return ItemStack.EMPTY;
        tank.fill(fluid, BUCKET_VOLUME, false);
        return new ItemStack(Items.BUCKET);
    }

    /**
     * Fills an empty bucket from the tank. Returns the filled bucket, or an
     * empty stack when there is not enough fluid.
     */
    public static ItemStack fillBucketFromTank(ItemStack bucket, FluidTank tank) {
        if (tank == null || !isEmptyBucket(bucket) || tank.getAmount() < BUCKET_VOLUME)
            return ItemStack.EMPTY;
        Item bucketItem = tank.getFluid().getBucketItem();
        if (bucketItem == Items.AIR)
            return ItemStack.EMPTY;
        tank.drain(BUCKET_VOLUME, false);
        return new ItemStack(bucketItem);
    }

    public static boolean canDrainInto(ItemStack stack, FluidTank tank) {
        return isFilledBucket(stack) && tank.fill(fluidOfBucket(stack), BUCKET_VOLUME, true) == BUCKET_VOLUME;
    }

    public static boolean canFillFrom(FluidTank tank) {
        return tank != null && tank.getAmount() >= BUCKET_VOLUME && tank.getFluid().getBucketItem() != Items.AIR;
    }
}
