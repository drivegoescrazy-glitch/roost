package com.setycz.chickens;

import com.setycz.chickens.entity.ChickensChickenEntity;
import com.setycz.chickens.entity.ColoredEggEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Entity registrations: the chickens and the thrown colored egg. */
public final class ChickensEntities {
	public static final EntityType<ChickensChickenEntity> CHICKEN = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(ChickensMod.MOD_ID, "chickens_chicken"),
			EntityType.Builder.create(ChickensChickenEntity::new, SpawnGroup.CREATURE)
					.dimensions(0.4F, 0.7F)
					.eyeHeight(0.644F)
					.build());

	public static final EntityType<ColoredEggEntity> COLORED_EGG = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(ChickensMod.MOD_ID, "colored_egg"),
			EntityType.Builder.<ColoredEggEntity>create(ColoredEggEntity::new, SpawnGroup.MISC)
					.dimensions(0.25F, 0.25F)
					.maxTrackingRange(4)
					.trackingTickInterval(10)
					.build());

	private ChickensEntities() {
	}

	public static void init() {
		FabricDefaultAttributeRegistry.register(CHICKEN, ChickenEntity.createChickenAttributes());
	}
}
