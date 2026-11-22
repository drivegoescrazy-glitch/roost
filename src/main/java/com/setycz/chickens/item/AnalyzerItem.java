package com.setycz.chickens.item;

import com.setycz.chickens.entity.ChickensChickenEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.List;

/** Right-click a chicken to reveal its stats. */
public class AnalyzerItem extends Item {
	public AnalyzerItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		tooltip.add(Text.translatable("item.analyzer.tooltip1"));
		tooltip.add(Text.translatable("item.analyzer.tooltip2"));
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (!(entity instanceof ChickensChickenEntity chicken)) {
			return ActionResult.PASS;
		}

		if (!user.getWorld().isClient()) {
			chicken.setStatsAnalyzed(true);

			if (user instanceof ServerPlayerEntity player) {
				player.sendMessage(chicken.getName().copy().formatted(Formatting.GOLD), false);
				player.sendMessage(Text.translatable("entity.ChickensChicken.tier", chicken.getTier()), false);
				player.sendMessage(Text.translatable("entity.ChickensChicken.growth", chicken.getGrowth()), false);
				player.sendMessage(Text.translatable("entity.ChickensChicken.gain", chicken.getGain()), false);
				player.sendMessage(Text.translatable("entity.ChickensChicken.strength", chicken.getStrength()), false);

				if (!chicken.isBaby()) {
					int progress = chicken.getLayProgress();
					if (progress <= 0) {
						player.sendMessage(Text.translatable("entity.ChickensChicken.nextEggSoon"), false);
					} else {
						player.sendMessage(Text.translatable("entity.ChickensChicken.layProgress", progress), false);
					}
				}
			}

			stack.damage(1, user, LivingEntity.getSlotForHand(hand));
		}
		return ActionResult.SUCCESS;
	}
}
