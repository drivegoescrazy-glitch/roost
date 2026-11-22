package com.setycz.chickens.item;

import com.setycz.chickens.ChickenComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/** A one-shot bucket of water or lava, as in the original mod. */
public class LiquidEggItem extends Item {
	public LiquidEggItem(Settings settings) {
		super(settings);
	}

	private static String liquid(ItemStack stack) {
		String value = stack.get(ChickenComponents.LIQUID);
		return value == null ? "water" : value;
	}

	@Override
	public Text getName(ItemStack stack) {
		return Text.translatable("item.liquid_egg." + liquid(stack) + ".name");
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		tooltip.add(Text.translatable("item.liquid_egg.tooltip"));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos target = context.getBlockPos().offset(context.getSide());
		ItemStack stack = context.getStack();

		if (!world.getBlockState(target).isReplaceable()) {
			return ActionResult.FAIL;
		}

		BlockState state = "lava".equals(liquid(stack))
				? Blocks.LAVA.getDefaultState()
				: Blocks.WATER.getDefaultState();

		if (!world.isClient()) {
			world.setBlockState(target, state);
			world.playSound(null, target, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (context.getPlayer() == null || !context.getPlayer().getAbilities().creativeMode) {
				stack.decrement(1);
			}
		}
		return ActionResult.success(world.isClient());
	}
}
