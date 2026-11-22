package com.timwoodcreates.roost;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Roost implements ModInitializer {
	public static final String MODID = "roost";
	public static final String NAME = "Roost";
	public static final String VERSION = "3.0.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
		RoostConfig.load();
		RoostChickens.bootstrap();
		RoostComponents.init();
		RoostBlocks.init();
		RoostItems.init();
		RoostBlockEntities.init();
		RoostScreenHandlers.init();
		RoostItemGroup.init();
	}
}
