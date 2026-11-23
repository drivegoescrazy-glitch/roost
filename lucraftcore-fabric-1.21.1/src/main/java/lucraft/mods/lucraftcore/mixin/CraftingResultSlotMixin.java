package lucraft.mods.lucraftcore.mixin;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.utilities.ItemHammer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Shadow
    @Final
    private RecipeInputInventory input;

    @Unique
    private final List<ItemStack> lucraftcore$tools = new ArrayList<>();

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void lucraftcore$collectTools(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        // A throw from a mixin is not caught by anything and would kill the
        // server thread, so the crafting-grid bookkeeping is exception safe.
        LucraftCore.guard("crafting tool collection", () -> {
            lucraftcore$tools.clear();
            for (int i = 0; i < input.size(); i++) {
                ItemStack slot = input.getStack(i);
                if (!slot.isEmpty() && slot.getItem() instanceof ItemHammer)
                    lucraftcore$tools.add(new ItemStack(slot.getItem()));
            }
        });
    }

    @Inject(method = "onTakeItem", at = @At("RETURN"))
    private void lucraftcore$restoreTools(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        LucraftCore.guard("crafting tool restore", () -> {
            for (ItemStack tool : lucraftcore$tools) {
                for (int i = 0; i < input.size(); i++) {
                    if (input.getStack(i).isEmpty()) {
                        input.setStack(i, tool);
                        break;
                    }
                }
            }
            lucraftcore$tools.clear();
        });
    }
}
