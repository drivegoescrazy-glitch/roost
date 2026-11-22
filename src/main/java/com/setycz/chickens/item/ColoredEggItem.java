package com.setycz.chickens.item;

import com.setycz.chickens.ChickenComponents;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.entity.ColoredEggEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/** Throwable colored egg, equivalent to the original metadata-based item. */
public class ColoredEggItem extends Item {
	public ColoredEggItem(Settings settings) {
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack) {
		ChickensRegistryItem chicken = getChicken(stack);
		if (chicken == null || chicken.getColorKey() == null) {
			return Text.translatable("item.chickens.colored_egg");
		}
		return Text.translatable("item.colored_egg." + chicken.getColorKey() + ".name");
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		tooltip.add(Text.translatable("item.colored_egg.tooltip"));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EGG_THROW,
				SoundCategory.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

		if (!world.isClient()) {
			ColoredEggEntity egg = new ColoredEggEntity(world, user, stack.copyWithCount(1));
			egg.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
			world.spawnEntity(egg);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			stack.decrement(1);
		}
		return TypedActionResult.success(stack, world.isClient());
	}

	public static ChickensRegistryItem getChicken(ItemStack stack) {
		String type = stack.get(ChickenComponents.CHICKEN_TYPE);
		return type == null ? null : ChickensRegistry.get(type);
	}
}
