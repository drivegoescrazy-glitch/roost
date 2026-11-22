package com.timwoodcreates.roost;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class RoostComponents {
	/** Registry id of the chicken type, for example "minecraft:chicken". */
	public static final ComponentType<String> CHICKEN = register("chicken",
			ComponentType.<String>builder().codec(Codec.STRING).packetCodec(PacketCodecs.STRING).build());

	public static final ComponentType<Integer> GROWTH = register("growth",
			ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).build());

	public static final ComponentType<Integer> GAIN = register("gain",
			ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).build());

	public static final ComponentType<Integer> STRENGTH = register("strength",
			ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).build());

	private RoostComponents() {
	}

	private static <T> ComponentType<T> register(String name, ComponentType<T> type) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Roost.MODID, name), type);
	}

	public static void init() {
	}
}
