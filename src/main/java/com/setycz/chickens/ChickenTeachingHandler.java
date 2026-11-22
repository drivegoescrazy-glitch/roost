package com.setycz.chickens;

import com.setycz.chickens.entity.ChickensChickenEntity;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;

/** Right-clicking a vanilla chicken with an egg turns it into a smart chicken. */
public final class ChickenTeachingHandler {
	private ChickenTeachingHandler() {
	}

	public static void init() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (world.isClient() || !(entity instanceof ChickenEntity vanilla)
					|| entity instanceof ChickensChickenEntity) {
				return ActionResult.PASS;
			}
			if (!player.getStackInHand(hand).isOf(Items.EGG)) {
				return ActionResult.PASS;
			}

			ChickensRegistryItem smart = ChickensRegistry.getSmartChicken();
			if (smart == null || !smart.isEnabled()) {
				return ActionResult.PASS;
			}

			ChickensChickenEntity smartChicken = ChickensEntities.CHICKEN.create(world);
			if (smartChicken == null) {
				return ActionResult.PASS;
			}
			smartChicken.setChickenType(smart.getRegistryName().toString());
			smartChicken.refreshPositionAndAngles(vanilla.getX(), vanilla.getY(), vanilla.getZ(),
					vanilla.getYaw(), vanilla.getPitch());
			smartChicken.setBaby(vanilla.isBaby());
			if (vanilla.hasCustomName()) {
				smartChicken.setCustomName(vanilla.getCustomName());
			}
			vanilla.discard();
			world.spawnEntity(smartChicken);
			return ActionResult.SUCCESS;
		});
	}
}
