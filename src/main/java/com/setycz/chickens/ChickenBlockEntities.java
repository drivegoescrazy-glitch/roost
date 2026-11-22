package com.setycz.chickens;

import com.setycz.chickens.block.entity.HenhouseBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Block entity registration. */
public final class ChickenBlockEntities {
	public static final BlockEntityType<HenhouseBlockEntity> HENHOUSE = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			Identifier.of(ChickensMod.MOD_ID, "henhouse"),
			BlockEntityType.Builder.create(HenhouseBlockEntity::new,
					ChickenBlocks.HENHOUSE,
					ChickenBlocks.HENHOUSE_ACACIA,
					ChickenBlocks.HENHOUSE_BIRCH,
					ChickenBlocks.HENHOUSE_DARK_OAK,
					ChickenBlocks.HENHOUSE_JUNGLE,
					ChickenBlocks.HENHOUSE_SPRUCE).build());

	private ChickenBlockEntities() {
	}

	public static void init() {
		// Registration happens on class load.
	}
}
