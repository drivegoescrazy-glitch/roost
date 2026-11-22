package com.timwoodcreates.roost;

import com.timwoodcreates.roost.block.entity.BreederBlockEntity;
import com.timwoodcreates.roost.block.entity.CollectorBlockEntity;
import com.timwoodcreates.roost.block.entity.RoostBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class RoostBlockEntities {
	public static final BlockEntityType<RoostBlockEntity> ROOST =
			FabricBlockEntityTypeBuilder.create(RoostBlockEntity::new, RoostBlocks.ROOST).build();
	public static final BlockEntityType<BreederBlockEntity> BREEDER =
			FabricBlockEntityTypeBuilder.create(BreederBlockEntity::new, RoostBlocks.BREEDER).build();
	public static final BlockEntityType<CollectorBlockEntity> COLLECTOR =
			FabricBlockEntityTypeBuilder.create(CollectorBlockEntity::new, RoostBlocks.COLLECTOR).build();

	private RoostBlockEntities() {
	}

	public static void init() {
		register("roost", ROOST);
		register("breeder", BREEDER);
		register("collector", COLLECTOR);
	}

	private static void register(String name, BlockEntityType<?> type) {
		Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Roost.MODID, name), type);
	}
}
