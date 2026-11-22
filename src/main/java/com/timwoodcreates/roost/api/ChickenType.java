package com.timwoodcreates.roost.api;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Random;

/**
 * A kind of chicken that a roost can hold. Roost ships the vanilla chicken; other mods may
 * register their own kinds through {@code RoostChickens.register}.
 *
 * <p>The texture name must be one of the textures shipped by Roost for the roost and chicken
 * item to display correctly. Unknown names fall back to the "unknownchicken" texture.
 */
public interface ChickenType {
	/** Registry id of this chicken, for example {@code minecraft:chicken}. */
	Identifier id();

	/** Texture name below {@code roost:textures/block/chicken/} and {@code roost:textures/item/chicken/}. */
	String texture();

	Text getDisplayName();

	int minLayTime();

	int maxLayTime();

	/** Builds the stack this chicken lays. A new stack must be returned on every call. */
	ItemStack createDrop(Random random);

	/** Stacks shown as this chicken's parents, empty when it cannot be bred. */
	default List<ItemStack> getParents() {
		return List.of();
	}

	/** Optional extra tooltip line, for example where this chicken can be found. */
	default Text getSpawnDescription() {
		return null;
	}
}
