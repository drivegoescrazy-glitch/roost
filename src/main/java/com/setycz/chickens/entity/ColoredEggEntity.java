package com.setycz.chickens.entity;

import com.setycz.chickens.ChickenComponents;
import com.setycz.chickens.ChickenItems;
import com.setycz.chickens.ChickensEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

/** A thrown colored egg. On impact it has the old 1-in-8 chance to hatch chicks. */
public class ColoredEggEntity extends ThrownItemEntity {
	public ColoredEggEntity(EntityType<? extends ColoredEggEntity> entityType, World world) {
		super(entityType, world);
	}

	public ColoredEggEntity(World world, LivingEntity owner, ItemStack stack) {
		super(ChickensEntities.COLORED_EGG, owner, world);
		this.setItem(stack);
	}

	@Override
	protected Item getDefaultItem() {
		return ChickenItems.COLORED_EGG;
	}

	public String getChickenType() {
		return this.getStack().get(ChickenComponents.CHICKEN_TYPE);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (this.getWorld().isClient()) {
			return;
		}

		if (this.getWorld() instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()),
					this.getX(), this.getY(), this.getZ(), 8, 0.08, 0.08, 0.08, 0.05);
		}

		String type = this.getChickenType();
		if (type != null && this.random.nextInt(8) == 0) {
			int count = this.random.nextInt(32) == 0 ? 4 : 1;
			for (int i = 0; i < count; i++) {
				ChickensChickenEntity chicken = ChickensEntities.CHICKEN.create(this.getWorld());
				if (chicken == null) {
					continue;
				}
				chicken.setChickenType(type);
				chicken.setBreedingAge(-24000);
				chicken.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
				this.getWorld().spawnEntity(chicken);
			}
		}
		this.discard();
	}
}
