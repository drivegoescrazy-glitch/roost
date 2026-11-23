package lucraft.mods.lucraftcore.utilities.blocks;

import lucraft.mods.lucraftcore.utilities.ItemInstruction;
import lucraft.mods.lucraftcore.utilities.recipes.InstructionRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Holds an instruction item and prepares recipes. Crafting consumes the
 * requirements from the player's inventory.
 */
public class ConstructionTableBlockEntity extends UtilitiesBlockEntity {

    private int selectedRecipe = -1;

    public ConstructionTableBlockEntity(BlockPos pos, BlockState state) {
        super(UtilitiesBlocks.CONSTRUCTION_TABLE_ENTITY, pos, state, 9, 1);
    }

    public List<InstructionRecipe> getRecipes() {
        return getRecipes(inventory.getStack(0));
    }

    public List<InstructionRecipe> getRecipes(ItemStack instruction) {
        return instruction.isEmpty() ? List.of() : ItemInstruction.getRecipes(instruction);
    }

    public InstructionRecipe getSelectedRecipe() {
        List<InstructionRecipe> recipes = getRecipes();
        if (recipes.isEmpty())
            return null;
        if (selectedRecipe < 0 || selectedRecipe >= recipes.size())
            selectedRecipe = 0;
        return recipes.get(selectedRecipe);
    }

    public void selectNextRecipe() {
        List<InstructionRecipe> recipes = getRecipes();
        if (recipes.isEmpty()) {
            selectedRecipe = -1;
            return;
        }
        selectedRecipe = (Math.max(0, selectedRecipe) + 1) % recipes.size();
        markDirty();
    }

    public boolean craft(PlayerEntity player) {
        return craftFrom(player, inventory.getStack(0));
    }

    /**
     * Crafts the first recipe from the given instruction stack that the player
     * can pay for, consuming the requirements and giving the output.
     */
    public boolean craftFrom(PlayerEntity player, ItemStack instruction) {
        for (InstructionRecipe recipe : getRecipes(instruction)) {
            if (!recipe.hasRequirements(player.getInventory()))
                continue;
            recipe.consume(player.getInventory());
            ItemStack output = recipe.getOutput().copy();
            if (!player.getInventory().insertStack(output))
                player.dropItem(output, false);
            markDirty();
            return true;
        }
        return false;
    }
}
