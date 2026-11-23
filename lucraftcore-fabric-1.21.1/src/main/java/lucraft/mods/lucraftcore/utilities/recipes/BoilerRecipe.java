package lucraft.mods.lucraftcore.utilities.recipes;

import net.minecraft.fluid.Fluid;

/** One boiler recipe: a fluid converted into another fluid using FE. */
public record BoilerRecipe(Fluid input, int inputAmount, Fluid output, int outputAmount, int energy, int time) {

    public boolean matches(Fluid fluid, int amount) {
        return fluid == input && amount >= inputAmount;
    }

    public int energyPerTick() {
        return time <= 0 ? energy : Math.max(1, energy / time);
    }
}
