package com.timwoodcreates.roost.data;

import com.timwoodcreates.roost.RoostConfig;
import com.timwoodcreates.roost.api.ChickenType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Random;

public enum VanillaChickenType implements ChickenType {
	INSTANCE;

	private static final Identifier ID = Identifier.of("minecraft", "chicken");

	@Override
	public Identifier id() {
		return ID;
	}

	@Override
	public String texture() {
		return "vanilla";
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("entity.minecraft.chicken");
	}

	@Override
	public int minLayTime() {
		return 6000;
	}

	@Override
	public int maxLayTime() {
		return 12000;
	}

	@Override
	public ItemStack createDrop(Random random) {
		return new ItemStack(random.nextInt(3) > 0 && !RoostConfig.get().disableEggLaying ? Items.EGG : Items.FEATHER);
	}
}
