package com.setycz.chickens.item;

import com.setycz.chickens.ChickenComponents;
import com.setycz.chickens.ChickensEntities;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.entity.ChickensChickenEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

/** One spawn egg item covering every chicken kind via a data component. */
public class ChickenSpawnEggItem extends Item {
	public ChickenSpawnEggItem(Settings settings) {
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack) {
		ChickensRegistryItem chicken = getChicken(stack);
		return chicken == null ? Text.translatable("item.chickens.spawn_egg")
				: Text.translatable("entity." + chicken.getEntityName() + ".name");
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		ChickensRegistryItem chicken = getChicken(stack);
		if (chicken != null) {
			tooltip.add(Text.translatable("entity.ChickensChicken.tier", chicken.getTier()));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (world.isClient()) {
			return ActionResult.SUCCESS;
		}

		ItemStack stack = context.getStack();
		ChickensRegistryItem description = getChicken(stack);
		if (description == null) {
			return ActionResult.FAIL;
		}

		BlockPos pos = context.getBlockPos().offset(context.getSide());
		ChickensChickenEntity chicken = ChickensEntities.CHICKEN.create(world);
		if (chicken == null) {
			return ActionResult.FAIL;
		}
		chicken.setChickenType(description.getRegistryName().toString());
		chicken.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
				context.getPlayer() == null ? 0.0F : context.getPlayer().getYaw(), 0.0F);
		if (world instanceof ServerWorld serverWorld) {
			chicken.initialize(serverWorld, world.getLocalDifficulty(pos), SpawnReason.SPAWN_EGG, null);
			serverWorld.spawnEntity(chicken);
			serverWorld.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
		}

		if (context.getPlayer() == null || !context.getPlayer().getAbilities().creativeMode) {
			stack.decrement(1);
		}
		return ActionResult.CONSUME;
	}

	public static ChickensRegistryItem getChicken(ItemStack stack) {
		String type = stack.get(ChickenComponents.CHICKEN_TYPE);
		return type == null ? null : ChickensRegistry.get(type);
	}

	public static int getColor(ItemStack stack, int tintIndex) {
		ChickensRegistryItem chicken = getChicken(stack);
		if (chicken == null) {
			return 0xFFFFFF;
		}
		return 0xFF000000 | (tintIndex == 0 ? chicken.getBgColor() : chicken.getFgColor());
	}
}
