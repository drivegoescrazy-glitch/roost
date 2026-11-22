package com.setycz.chickens;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Chickens for Minecraft 1.21.1 on Fabric. */
public class ChickensMod implements ModInitializer {
	public static final String MOD_ID = "chickens";
	public static final String NAME = "Chickens";
	public static final String VERSION = "7.0.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
		ChickenComponents.init();
		ChickenItems.init();
		ChickenBlocks.init();

		ChickensConfig.load();
		for (ChickensRegistryItem chicken : DefaultChickens.create()) {
			ChickensRegistry.register(chicken);
		}
		ChickensConfig.apply();

		ChickensEntities.init();
		ChickenBlockEntities.init();
		ChickenScreenHandlers.init();
		ChickenItemGroup.init();
		ChickenTeachingHandler.init();
		ChickenSpawns.init();

		LOGGER.info("Chickens {} loaded with {} kinds", VERSION, ChickensRegistry.getItems().size());
	}
}
