package com.timwoodcreates.roost;

import com.timwoodcreates.roost.block.BlockBreeder;
import com.timwoodcreates.roost.block.BlockCollector;
import com.timwoodcreates.roost.block.BlockRoost;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class RoostBlocks {
	public static final BlockRoost ROOST = new BlockRoost();
	public static final BlockBreeder BREEDER = new BlockBreeder();
	public static final BlockCollector COLLECTOR = new BlockCollector();

	private RoostBlocks() {
	}

	public static void init() {
		register("roost", ROOST);
		register("breeder", BREEDER);
		register("collector", COLLECTOR);
	}

	private static void register(String name, Block block) {
		Registry.register(Registries.BLOCK, Identifier.of(Roost.MODID, name), block);
	}
}
