package com.timwoodcreates.roost.data;

import com.timwoodcreates.roost.RoostChickens;
import com.timwoodcreates.roost.RoostComponents;
import com.timwoodcreates.roost.RoostItems;
import com.timwoodcreates.roost.RoostTextures;
import com.timwoodcreates.roost.api.ChickenType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public final class DataChicken {
	public static final String CHICKEN_ID_KEY = "Chicken";

	private static final int MAX_STAT = 10;

	private final ChickenType type;
	private final int growth;
	private final int gain;
	private final int strength;

	private final Random rand = new Random();

	private DataChicken(ChickenType type, int growth, int gain, int strength) {
		this.type = type;
		this.growth = clampStat(growth);
		this.gain = clampStat(gain);
		this.strength = clampStat(strength);
	}

	private static int clampStat(int value) {
		return Math.max(1, Math.min(MAX_STAT, value));
	}

	public static DataChicken of(ChickenType type) {
		return type == null ? null : new DataChicken(type, 1, 1, 1);
	}

	public static DataChicken getDataFromName(String name) {
		ChickenType type = RoostChickens.get(name);
		return type == null ? null : of(type);
	}

	public static DataChicken getDataFromStack(ItemStack stack) {
		if (!isChicken(stack)) {
			return null;
		}

		String id = stack.get(RoostComponents.CHICKEN);
		ChickenType type = id == null ? null : RoostChickens.get(id);
		if (type == null) {
			return null;
		}

		return new DataChicken(type,
				stack.getOrDefault(RoostComponents.GROWTH, 1),
				stack.getOrDefault(RoostComponents.GAIN, 1),
				stack.getOrDefault(RoostComponents.STRENGTH, 1));
	}

	public static DataChicken getDataFromEntity(Entity entity) {
		if (entity instanceof ChickenEntity) {
			return of(VanillaChickenType.INSTANCE);
		}
		return null;
	}

	public static boolean isChicken(ItemStack stack) {
		return stack.isOf(RoostItems.ITEM_CHICKEN);
	}

	public ChickenType getType() {
		return type;
	}

	public String getChickenType() {
		return type.id().toString();
	}

	public String getTextureName() {
		return type.texture();
	}

	public Text getDisplayName() {
		return type.getDisplayName();
	}

	public void addInfoToTooltip(List<Text> tooltip) {
		if (growth > 1 && gain > 1 && strength > 1) {
			tooltip.add(Text.translatable("item.roost.chicken.growth", growth));
			tooltip.add(Text.translatable("item.roost.chicken.gain", gain));
			tooltip.add(Text.translatable("item.roost.chicken.strength", strength));
			return;
		}

		List<ItemStack> parents = type.getParents();
		if (parents.size() >= 2) {
			tooltip.add(Text.translatable("item.roost.chicken.parent1", parents.get(0).getName()));
			tooltip.add(Text.translatable("item.roost.chicken.parent2", parents.get(1).getName()));
		}

		Text spawnDescription = type.getSpawnDescription();
		if (spawnDescription != null) {
			tooltip.add(spawnDescription);
		}
	}

	public ItemStack buildChickenStack() {
		ItemStack stack = new ItemStack(RoostItems.ITEM_CHICKEN);
		stack.set(RoostComponents.CHICKEN, getChickenType());
		if (growth != 1) {
			stack.set(RoostComponents.GROWTH, growth);
		}
		if (gain != 1) {
			stack.set(RoostComponents.GAIN, gain);
		}
		if (strength != 1) {
			stack.set(RoostComponents.STRENGTH, strength);
		}
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(RoostTextures.modelIndex(getTextureName())));
		return stack;
	}

	public ChickenEntity buildEntity(World world) {
		return EntityType.CHICKEN.create(world);
	}

	public void spawnEntity(World world, BlockPos pos) {
		ChickenEntity chicken = buildEntity(world);
		if (chicken == null) {
			return;
		}

		chicken.setPosition(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
		chicken.setBreedingAge(0);
		world.spawnEntity(chicken);
	}

	public ItemStack createDropStack() {
		return type.createDrop(rand);
	}

	public ItemStack createChildStack(DataChicken other, World world) {
		if (other == null || other.type != type || !(world instanceof ServerWorld serverWorld)) {
			return buildChickenStack();
		}

		ChickenEntity parentA = buildEntity(world);
		ChickenEntity parentB = other.buildEntity(world);
		if (parentA == null || parentB == null) {
			return ItemStack.EMPTY;
		}

		PassiveEntity child = rand.nextBoolean()
				? parentA.createChild(serverWorld, parentB)
				: parentB.createChild(serverWorld, parentA);
		DataChicken childData = getDataFromEntity(child);
		return childData == null ? ItemStack.EMPTY : childData.buildChickenStack();
	}

	public int getAddedTime(ItemStack stack) {
		return growth * Math.max(0, stack.getCount());
	}

	public int getLayTime() {
		int min = type.minLayTime();
		int max = Math.max(min + 1, type.maxLayTime());
		return min + rand.nextInt(max - min);
	}

	public boolean isEqual(DataChicken other) {
		return other != null
				&& type.id().equals(other.type.id())
				&& growth == other.growth
				&& gain == other.gain
				&& strength == other.strength;
	}

	public static Identifier textureIdentifier(String texture) {
		return Identifier.of("roost", "block/chicken/" + texture);
	}

	@Override
	public String toString() {
		return "DataChicken [type=" + getChickenType() + ", growth=" + growth + ", gain=" + gain + ", strength=" + strength + "]";
	}
}
