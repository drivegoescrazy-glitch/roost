package com.setycz.chickens;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.mojang.serialization.Codec;

/** Item data components used by colored eggs, spawn eggs and liquid eggs. */
public final class ChickenComponents {
	public static final ComponentType<String> CHICKEN_TYPE = register("chicken_type", Codec.STRING);
	public static final ComponentType<String> LIQUID = register("liquid", Codec.STRING);

	private ChickenComponents() {
	}

	private static ComponentType<String> register(String name, Codec<String> codec) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE,
				Identifier.of(ChickensMod.MOD_ID, name),
				ComponentType.<String>builder().codec(codec).build());
	}

	public static void init() {
		// Static registration happens on class load.
	}
}
