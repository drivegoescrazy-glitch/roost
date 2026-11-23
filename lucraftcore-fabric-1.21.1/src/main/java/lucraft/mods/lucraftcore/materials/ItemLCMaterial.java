package lucraft.mods.lucraftcore.materials;

import lucraft.mods.lucraftcore.LCConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLCMaterial extends Item {

    private final Material material;
    private final Material.Component component;

    public ItemLCMaterial(Material material, Material.Component component) {
        super(new Item.Settings().maxCount(64));
        this.material = material;
        this.component = component;
    }

    public Material getMaterial() {
        return material;
    }

    public Material.Component getComponent() {
        return component;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && LCConfig.radiation && material.isRadioactive() && entity instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(ModuleMaterials.radiationEntry(), 100, 1));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
