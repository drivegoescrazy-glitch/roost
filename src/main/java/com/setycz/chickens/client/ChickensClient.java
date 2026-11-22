package com.setycz.chickens.client;

import com.setycz.chickens.ChickenItemGroup;
import com.setycz.chickens.ChickenItems;
import com.setycz.chickens.ChickenScreenHandlers;
import com.setycz.chickens.ChickensEntities;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.item.ChickenSpawnEggItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.item.ItemStack;

/** Client-only renderers, GUI and item tinting. */
public class ChickensClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ChickensEntities.CHICKEN, ChickensChickenRenderer::new);
		EntityRendererRegistry.register(ChickensEntities.COLORED_EGG, FlyingItemEntityRenderer::new);

		HandledScreens.register(ChickenScreenHandlers.HENHOUSE, HenhouseScreen::new);

		ColorProviderRegistry.ITEM.register(ChickenSpawnEggItem::getColor, ChickenItems.SPAWN_EGG);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> coloredEggColor(stack), ChickenItems.COLORED_EGG);

		// Make sure the creative tab has its contents before the player can open
		// the inventory: an unbuilt group makes the creative screen throw.
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.world != null && client.player != null) {
				ChickenItemGroup.updateDisplay(client.world.getEnabledFeatures(),
						client.player.hasPermissionLevel(2), client.world.getRegistryManager());
			}
		});
	}

	private static int coloredEggColor(ItemStack stack) {
		ChickensRegistryItem chicken = ChickensRegistry.get(
				stack.getOrDefault(com.setycz.chickens.ChickenComponents.CHICKEN_TYPE, ""));
		return chicken == null ? 0xFFFFFF : chicken.getFgColor();
	}
}
