package lucraft.mods.lucraftcore.superpowers.suitsets;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class SuitSetArmorItem extends ArmorItem {

    private final SuitSet suitSet;
    private final boolean glow;

    public SuitSetArmorItem(RegistryEntry<net.minecraft.item.ArmorMaterial> material, Type type, Settings settings,
                            SuitSet suitSet, boolean glow) {
        super(material, type, settings);
        this.suitSet = suitSet;
        this.glow = glow;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return glow || super.hasGlint(stack);
    }

    public SuitSet getSuitSet() {
        return suitSet;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(suitSet.getName()).formatted(Formatting.GOLD));
        if (!suitSet.getAbilities().isEmpty()) {
            tooltip.add(Text.translatable("lucraftcore.info.abilities").formatted(Formatting.RED));
            for (String ability : suitSet.getAbilities())
                tooltip.add(Text.literal("- ").formatted(Formatting.RED)
                        .append(Text.translatable("ability.lucraftcore." + ability).formatted(Formatting.GRAY)));
        }
    }
}
