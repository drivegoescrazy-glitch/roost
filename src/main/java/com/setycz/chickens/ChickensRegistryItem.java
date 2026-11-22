package com.setycz.chickens;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * One kind of chicken: what it lays, what it drops, its colours, texture and
 * breeding parents. Mirrors the original ChickensRegistryItem.
 */
public class ChickensRegistryItem {
	private final Identifier registryName;
	private final String entityName;
	private final Identifier texture;
	private final int bgColor;
	private final int fgColor;
	private ItemStack layItem;
	private ItemStack dropItem;
	private ChickensRegistryItem parent1;
	private ChickensRegistryItem parent2;
	private SpawnType spawnType = SpawnType.NORMAL;
	private boolean enabled = true;
	private float layCoefficient = 1.0F;
	private String colorKey;

	public ChickensRegistryItem(Identifier registryName, String entityName, Identifier texture,
								ItemStack layItem, int bgColor, int fgColor,
								ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
		this.registryName = registryName;
		this.entityName = entityName;
		this.texture = texture;
		this.layItem = layItem.copy();
		this.dropItem = layItem.copy();
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.parent1 = parent1;
		this.parent2 = parent2;
	}

	public ChickensRegistryItem(Identifier registryName, String entityName, Identifier texture,
								ItemStack layItem, int bgColor, int fgColor) {
		this(registryName, entityName, texture, layItem, bgColor, fgColor, null, null);
	}

	public Identifier getRegistryName() {
		return this.registryName;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public Identifier getTexture() {
		return this.texture;
	}

	public int getBgColor() {
		return this.bgColor;
	}

	public int getFgColor() {
		return this.fgColor;
	}

	public String getColorKey() {
		return this.colorKey;
	}

	public ChickensRegistryItem setColorKey(String colorKey) {
		this.colorKey = colorKey;
		return this;
	}

	public ItemStack createLayItem() {
		return this.layItem.copy();
	}

	public ItemStack createDropItem() {
		return this.dropItem.copy();
	}

	public ItemStack getLayItem() {
		return this.layItem;
	}

	public ChickensRegistryItem setLayItem(ItemStack stack) {
		this.layItem = stack.copy();
		return this;
	}

	public ChickensRegistryItem setDropItem(ItemStack stack) {
		this.dropItem = stack.copy();
		return this;
	}

	public ChickensRegistryItem setSpawnType(SpawnType type) {
		this.spawnType = type;
		return this;
	}

	public SpawnType getSpawnType() {
		return this.spawnType;
	}

	public ChickensRegistryItem setLayCoefficient(float layCoefficient) {
		this.layCoefficient = layCoefficient;
		return this;
	}

	public int getTier() {
		return this.parent1 != null && this.parent2 != null
				? Math.max(this.parent1.getTier(), this.parent2.getTier()) + 1
				: 1;
	}

	public ChickensRegistryItem getParent1() {
		return this.parent1;
	}

	public ChickensRegistryItem getParent2() {
		return this.parent2;
	}

	public ChickensRegistryItem setParentsNew(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
		this.parent1 = parent1;
		this.parent2 = parent2;
		return this;
	}

	public void setNoParents() {
		this.parent1 = null;
		this.parent2 = null;
	}

	public boolean isChildOf(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
		return this.parent1 == parent1 && this.parent2 == parent2
				|| this.parent1 == parent2 && this.parent2 == parent1;
	}

	public boolean isBreedable() {
		return this.parent1 != null && this.parent2 != null;
	}

	public boolean canSpawn() {
		return this.getTier() == 1 && this.spawnType != SpawnType.NONE;
	}

	public boolean isImmuneToFire() {
		return this.spawnType == SpawnType.HELL;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return this.enabled
				&& (this.parent1 == null || this.parent1.isEnabled())
				&& (this.parent2 == null || this.parent2.isEnabled());
	}

	public int getMinLayTime() {
		return (int) Math.max(6000.0F * this.getTier() * this.layCoefficient, 1.0F);
	}

	public int getMaxLayTime() {
		return 2 * this.getMinLayTime();
	}
}
