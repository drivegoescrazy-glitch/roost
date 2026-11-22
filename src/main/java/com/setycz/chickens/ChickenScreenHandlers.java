package com.setycz.chickens;

import com.setycz.chickens.screen.HenhouseScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/** Screen handler registration. */
public final class ChickenScreenHandlers {
	public static final ScreenHandlerType<HenhouseScreenHandler> HENHOUSE =
			Registry.register(Registries.SCREEN_HANDLER,
					Identifier.of(ChickensMod.MOD_ID, "henhouse"),
					new ExtendedScreenHandlerType<>(HenhouseScreenHandler::new, net.minecraft.util.math.BlockPos.PACKET_CODEC));

	private ChickenScreenHandlers() {
	}

	public static void init() {
		// Registration happens on class load.
	}
}
