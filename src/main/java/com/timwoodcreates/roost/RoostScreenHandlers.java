package com.timwoodcreates.roost;

import com.timwoodcreates.roost.block.entity.BreederBlockEntity;
import com.timwoodcreates.roost.block.entity.RoostBlockEntity;
import com.timwoodcreates.roost.screen.BreederScreenHandler;
import com.timwoodcreates.roost.screen.RoostScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class RoostScreenHandlers {
	public static final PacketCodec<RegistryByteBuf, BlockPos> BLOCK_POS = PacketCodec.of(
			(pos, buffer) -> buffer.writeBlockPos(pos),
			buffer -> buffer.readBlockPos());

	public static final ExtendedScreenHandlerType<RoostScreenHandler, BlockPos> ROOST =
			Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Roost.MODID, "roost"),
					new ExtendedScreenHandlerType<>((syncId, playerInventory, pos) -> {
						World world = playerInventory.player.getWorld();
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity instanceof RoostBlockEntity roost) {
							return new RoostScreenHandler(syncId, playerInventory, roost, roost.createPropertyDelegate());
						}
						return new RoostScreenHandler(syncId, playerInventory, new SimpleInventory(5), new ArrayPropertyDelegate(1));
					}, BLOCK_POS));

	public static final ExtendedScreenHandlerType<BreederScreenHandler, BlockPos> BREEDER =
			Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Roost.MODID, "breeder"),
					new ExtendedScreenHandlerType<>((syncId, playerInventory, pos) -> {
						World world = playerInventory.player.getWorld();
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity instanceof BreederBlockEntity breeder) {
							return new BreederScreenHandler(syncId, playerInventory, breeder, breeder.createPropertyDelegate());
						}
						return new BreederScreenHandler(syncId, playerInventory, new SimpleInventory(6), new ArrayPropertyDelegate(1));
					}, BLOCK_POS));

	private RoostScreenHandlers() {
	}

	public static void init() {
	}
}
