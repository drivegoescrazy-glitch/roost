package com.timwoodcreates.roost.item;

import com.timwoodcreates.roost.data.DataChicken;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Random;

public class ItemCatcher extends Item {
	public ItemCatcher() {
		super(new Item.Settings().maxCount(1).maxDamage(64));
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
		DataChicken chickenData = DataChicken.getDataFromEntity(entity);
		if (chickenData == null) {
			return ActionResult.PASS;
		}

		World world = player.getWorld();

		if (entity.isBaby()) {
			if (world.isClient) {
				spawnParticles(world, entity, ParticleTypes.SMOKE);
			}
			world.playSound(player, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_CHICKEN_HURT, entity.getSoundCategory(), 1.0f, 1.0f);
		} else {
			if (world.isClient) {
				spawnParticles(world, entity, ParticleTypes.EXPLOSION);
			} else {
				ItemEntity item = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(),
						chickenData.buildChickenStack(), 0.0d, 0.2d, 0.0d);
				world.spawnEntity(item);
				entity.discard();
				stack.damage(1, player, EquipmentSlot.MAINHAND);
			}
			world.playSound(player, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, entity.getSoundCategory(), 1.0f, 1.0f);
		}

		return ActionResult.SUCCESS;
	}

	private void spawnParticles(World world, LivingEntity entity, ParticleEffect particle) {
		Random rand = new Random();

		for (int i = 0; i < 20; ++i) {
			double x = entity.getX() + (rand.nextDouble() * 0.6d) - 0.3d;
			double y = entity.getY() + (rand.nextDouble() * 0.6d);
			double z = entity.getZ() + (rand.nextDouble() * 0.6d) - 0.3d;
			double xSpeed = rand.nextGaussian() * 0.02d;
			double ySpeed = rand.nextGaussian() * 0.2d;
			double zSpeed = rand.nextGaussian() * 0.02d;
			world.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}
