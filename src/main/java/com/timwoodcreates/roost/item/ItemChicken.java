package com.timwoodcreates.roost.item;

import com.timwoodcreates.roost.RoostComponents;
import com.timwoodcreates.roost.block.entity.RoostBlockEntity;
import com.timwoodcreates.roost.data.DataChicken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemChicken extends Item {
	public ItemChicken() {
		super(new Item.Settings().maxCount(16));
	}

	@Override
	public Text getName(ItemStack stack) {
		DataChicken data = DataChicken.getDataFromStack(stack);
		return data == null ? Text.translatable("entity.minecraft.chicken") : data.getDisplayName();
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		DataChicken data = DataChicken.getDataFromStack(stack);
		if (data != null) {
			data.addInfoToTooltip(tooltip);
			return;
		}

		String chicken = stack.get(RoostComponents.CHICKEN);
		if (chicken != null && !chicken.isEmpty()) {
			tooltip.add(Text.literal("Broken chicken, id = \"" + chicken + "\""));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();

		if (world.getBlockEntity(pos) instanceof RoostBlockEntity roost) {
			if (world.isClient) {
				return ActionResult.SUCCESS;
			}
			// A full roost falls through to the block, which opens its screen instead.
			return roost.putChickenIn(context.getStack()) ? ActionResult.SUCCESS : ActionResult.PASS;
		}

		if (!world.isClient) {
			DataChicken data = DataChicken.getDataFromStack(context.getStack());
			if (data != null) {
				data.spawnEntity(world, pos.offset(context.getSide()));
				context.getStack().decrement(1);
			}
		}

		return ActionResult.SUCCESS;
	}
}
