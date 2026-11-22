package com.timwoodcreates.roost.client;

import com.timwoodcreates.roost.RoostBlocks;
import com.timwoodcreates.roost.RoostScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class RoostClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(RoostBlocks.ROOST, RenderLayer.getCutout());

		HandledScreens.register(RoostScreenHandlers.ROOST, GuiRoost::new);
		HandledScreens.register(RoostScreenHandlers.BREEDER, GuiBreeder::new);
	}
}
