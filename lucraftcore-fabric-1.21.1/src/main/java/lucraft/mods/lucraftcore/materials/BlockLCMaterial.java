package lucraft.mods.lucraftcore.materials;

import lucraft.mods.lucraftcore.LCConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BlockLCMaterial extends Block {

    private final Material material;
    private final Material.Component component;

    public BlockLCMaterial(Material material, Material.Component component) {
        super(AbstractBlock.Settings.create()
                .strength(material.getBlockHardness(), material.getResistance())
                .sounds(component == Material.Component.ORE ? BlockSoundGroup.STONE : BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly());
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
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!LCConfig.radiation || !material.isRadioactive())
            return;

        double radius = component == Material.Component.ORE ? 3.0D : 1.5D;
        int amplifier = component == Material.Component.ORE ? 1 : 0;
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, new net.minecraft.util.math.Box(pos).expand(radius), e -> true)) {
            entity.addStatusEffect(new StatusEffectInstance(ModuleMaterials.radiationEntry(), 100, amplifier));
        }
        if (world.isClient())
            return;
        world.spawnParticles(ParticleTypes.SNEEZE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 2, 0.3D, 0.3D, 0.3D, 0.0D);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (LCConfig.radiation && material.isRadioactive() && entity instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(ModuleMaterials.radiationEntry(), 100, 0));
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
