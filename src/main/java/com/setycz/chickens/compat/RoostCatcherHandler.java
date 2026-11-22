package com.setycz.chickens.compat;

import com.setycz.chickens.entity.ChickensChickenEntity;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

/**
 * Roost's chicken catcher turns any ChickenEntity into a plain chicken item.
 * Our chickens are a ChickenEntity too, so they were always captured as a
 * generic chicken and lost their kind. This handles the capture for our
 * chickens first, with the item describing the actual kind.
 */
public final class RoostCatcherHandler {
	private static final Identifier CATCHER = Identifier.of("roost", "catcher");

	private RoostCatcherHandler() {
	}

	public static void init() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (world.isClient() || !(entity instanceof ChickensChickenEntity chicken)) {
				return ActionResult.PASS;
			}
			ItemStack held = player.getStackInHand(hand);
			if (!CATCHER.equals(Registries.ITEM.getId(held.getItem()))) {
				return ActionResult.PASS;
			}
			ItemStack captured = RoostCompat.captureStack(chicken.getChickenTypeInternal());
			if (captured.isEmpty()) {
				return ActionResult.PASS;
			}
			if (!world.isClient()) {
				ItemEntity drop = new ItemEntity(world, chicken.getX(), chicken.getY() + 0.5D, chicken.getZ(), captured);
				drop.setVelocity((chicken.getRandom().nextDouble() - 0.5D) * 0.2D, 0.2D,
						(chicken.getRandom().nextDouble() - 0.5D) * 0.2D);
				world.spawnEntity(drop);
				chicken.discard();
			}
			return ActionResult.SUCCESS;
		});
	}
}
