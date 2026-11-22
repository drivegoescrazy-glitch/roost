package com.setycz.chickens.entity;

import com.setycz.chickens.ChickensEntities;
import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.SpawnType;
import com.setycz.chickens.block.entity.HenhouseBlockEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The modded chicken. Extends the vanilla chicken so the vanilla model,
 * animations and sounds are reused, and layers the old-type/stats/egg logic on
 * top exactly like the 1.12.2 version.
 */
public class ChickensChickenEntity extends ChickenEntity {
	private static final TrackedData<String> TYPE = DataTracker.registerData(
			ChickensChickenEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<Boolean> ANALYZED = DataTracker.registerData(
			ChickensChickenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> GROWTH = DataTracker.registerData(
			ChickensChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> GAIN = DataTracker.registerData(
			ChickensChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> STRENGTH = DataTracker.registerData(
			ChickensChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> LAY_PROGRESS = DataTracker.registerData(
			ChickensChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);

	private int layTimer;

	public ChickensChickenEntity(EntityType<? extends ChickensChickenEntity> entityType, World world) {
		super(entityType, world);
		this.resetTimeUntilNextEgg();
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(TYPE, "");
		builder.add(ANALYZED, false);
		builder.add(GROWTH, 1);
		builder.add(GAIN, 1);
		builder.add(STRENGTH, 1);
		builder.add(LAY_PROGRESS, 0);
	}

	public ChickensRegistryItem getChickenDescription() {
		ChickensRegistryItem description = ChickensRegistry.get(this.getChickenTypeInternal());
		if (description != null && description.isEnabled()) {
			return description;
		}
		ChickensRegistryItem smart = ChickensRegistry.getSmartChicken();
		if (smart != null) {
			return smart;
		}
		for (ChickensRegistryItem item : ChickensRegistry.all()) {
			if (item.isEnabled()) {
				return item;
			}
		}
		for (ChickensRegistryItem item : ChickensRegistry.all()) {
			return item;
		}
		return null;
	}

	public String getChickenTypeInternal() {
		return this.dataTracker.get(TYPE);
	}

	public void setChickenType(String registryName) {
		this.dataTracker.set(TYPE, registryName);
		this.resetTimeUntilNextEgg();
	}

	public Identifier getTexture() {
		ChickensRegistryItem description = this.getChickenDescription();
		if (description != null) {
			return description.getTexture();
		}
		ChickensRegistryItem smart = ChickensRegistry.getSmartChicken();
		return smart != null ? smart.getTexture()
				: Identifier.of(ChickensMod.MOD_ID, "textures/entity/smart_chicken.png");
	}

	public int getTier() {
		ChickensRegistryItem description = this.getChickenDescription();
		return description == null ? 1 : description.getTier();
	}

	public boolean getStatsAnalyzed() {
		return this.dataTracker.get(ANALYZED);
	}

	public void setStatsAnalyzed(boolean analyzed) {
		this.dataTracker.set(ANALYZED, analyzed);
	}

	public int getGrowth() {
		return this.dataTracker.get(GROWTH);
	}

	private void setGrowth(int growth) {
		this.dataTracker.set(GROWTH, growth);
	}

	public int getGain() {
		return this.dataTracker.get(GAIN);
	}

	private void setGain(int gain) {
		this.dataTracker.set(GAIN, gain);
	}

	public int getStrength() {
		return this.dataTracker.get(STRENGTH);
	}

	private void setStrength(int strength) {
		this.dataTracker.set(STRENGTH, strength);
	}

	public int getLayProgress() {
		return this.dataTracker.get(LAY_PROGRESS);
	}

	private void setLayProgress(int minutes) {
		this.dataTracker.set(LAY_PROGRESS, minutes);
	}

	@Override
	public Text getName() {
		if (this.hasCustomName()) {
			return super.getName();
		}
		ChickensRegistryItem description = this.getChickenDescription();
		return description == null ? super.getName()
				: Text.translatable("entity." + description.getEntityName() + ".name");
	}

	@Override
	public boolean isFireImmune() {
		ChickensRegistryItem description = this.getChickenDescription();
		return (description != null && description.isImmuneToFire()) || super.isFireImmune();
	}

	@Override
	public void tick() {
		// Keep the vanilla egg timer from ever expiring: modded chickens lay
		// their own item through the timer below instead of vanilla eggs.
		this.eggLayTime = 6000;
		if (!this.getWorld().isClient() && this.isAlive() && !this.isBaby() && !this.hasJockey()) {
			if (this.layTimer > 1) {
				this.layTimer--;
			} else {
				// A stale or broken timer from old save data must never make a
				// chicken lay every tick: the henhouse scan would stall the server.
				this.layTimer = 0;
				try {
					this.layEgg();
				} catch (Throwable throwable) {
					ChickensMod.LOGGER.warn("Chicken {} could not lay its item",
							this.getChickenTypeInternal(), throwable);
				}
				this.resetTimeUntilNextEgg();
			}
		}
		super.tick();
	}

	private void layEgg() {
		ChickensRegistryItem description = this.getChickenDescription();
		if (description == null) {
			return;
		}
		ItemStack stack = description.createLayItem();
		int gain = this.getGain();
		if (gain >= 5) {
			stack.setCount(stack.getCount() * 2);
		}
		if (gain >= 10) {
			stack.setCount(stack.getCount() * 2);
		}

		ItemStack leftover = HenhouseBlockEntity.pushItemStack(stack, this.getWorld(), this.getBlockPos());
		if (!leftover.isEmpty() && !this.getWorld().isClient()) {
			this.dropStack(leftover);
			this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F,
					(this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
		}
	}

	private void resetTimeUntilNextEgg() {
		ChickensRegistryItem description = this.getChickenDescription();
		if (description == null) {
			this.layTimer = 6000;
			this.setLayProgress(0);
			return;
		}
		int min = description.getMinLayTime();
		int max = Math.max(description.getMaxLayTime(), min + 1);
		int base = min + this.random.nextInt(max - min);
		int timer = (int) Math.max(1.0F, base * (10.0F - this.getGrowth() + 1.0F) / 10.0F);
		this.layTimer = Math.max(20, timer * 2);
		this.setLayProgress(this.layTimer / 2400);
	}

	@Override
	public void setBreedingAge(int age) {
		int childAge = -24000;
		if (age == childAge) {
			age = Math.min(-1, childAge * (10 - this.getGrowth() + 1) / 10);
		}
		int loveAge = 6000;
		if (age == loveAge) {
			age = Math.max(1, loveAge * (10 - this.getGrowth() + 1) / 10);
		}
		super.setBreedingAge(age);
	}

	@Override
	public ChickensChickenEntity createChild(ServerWorld world, PassiveEntity other) {
		if (!(other instanceof ChickensChickenEntity mate)) {
			return null;
		}

		ChickensRegistryItem parent1 = this.getChickenDescription();
		ChickensRegistryItem parent2 = mate.getChickenDescription();
		if (parent1 == null || parent2 == null) {
			return null;
		}
		ChickensRegistryItem child = ChickensRegistry.getRandomChild(parent1, parent2);
		if (child == null) {
			return null;
		}

		ChickensChickenEntity baby = ChickensEntities.CHICKEN.create(world);
		if (baby == null) {
			return null;
		}
		baby.setChickenType(child.getRegistryName().toString());

		boolean mutatingStats = parent1 == parent2 && child == parent1;
		if (mutatingStats) {
			baby.setGrowth(calculateNewStat(this.getStrength(), mate.getStrength(),
					this.getGrowth(), mate.getGrowth(), this.random));
			baby.setGain(calculateNewStat(this.getStrength(), mate.getStrength(),
					this.getGain(), mate.getGain(), this.random));
			baby.setStrength(calculateNewStat(this.getStrength(), mate.getStrength(),
					this.getStrength(), mate.getStrength(), this.random));
		} else if (parent1 == child) {
			inheritStats(baby, this);
		} else if (parent2 == child) {
			inheritStats(baby, mate);
		}
		return baby;
	}

	private static void inheritStats(ChickensChickenEntity baby, ChickensChickenEntity parent) {
		baby.setGrowth(parent.getGrowth());
		baby.setGain(parent.getGain());
		baby.setStrength(parent.getStrength());
	}

	private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random random) {
		int mutation = random.nextInt(2) + 1;
		int value = (stat1 * thisStrength + stat2 * mateStrength) / Math.max(1, thisStrength + mateStrength) + mutation;
		return Math.max(1, Math.min(10, value));
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
								 @Nullable EntityData entityData) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData);

		if (entityData instanceof ChickenGroupData groupData) {
			this.setChickenType(groupData.type());
		} else {
			SpawnType spawnType = this.getSpawnType(world);
			List<ChickensRegistryItem> possible = ChickensRegistry.getPossibleChickensToSpawn(spawnType);
			if (!possible.isEmpty()) {
				ChickensRegistryItem picked = possible.get(this.random.nextInt(possible.size()));
				this.setChickenType(picked.getRegistryName().toString());
				entityData = new ChickenGroupData(picked.getRegistryName().toString());
			}
		}

		if (this.random.nextInt(5) == 0) {
			this.setBreedingAge(-24000);
		}
		return entityData;
	}

	private SpawnType getSpawnType(ServerWorldAccess world) {
		if (world instanceof ServerWorld serverWorld && serverWorld.getRegistryKey() == World.NETHER) {
			return SpawnType.HELL;
		}
		boolean snowy = world.getBiome(this.getBlockPos()).value().getTemperature() < 0.15F;
		return snowy ? SpawnType.SNOW : SpawnType.NORMAL;
	}

	@Override
	protected void dropLoot(DamageSource source, boolean causedByPlayer) {
		ChickensRegistryItem description = this.getChickenDescription();
		if (description != null) {
			this.dropStack(description.createDropItem());
		}

		if (this.isOnFire()) {
			this.dropStack(new ItemStack(Items.COOKED_CHICKEN));
		} else {
			this.dropStack(new ItemStack(Items.CHICKEN));
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("Type", this.getChickenTypeInternal());
		nbt.putBoolean("Analyzed", this.getStatsAnalyzed());
		nbt.putInt("Growth", this.getGrowth());
		nbt.putInt("Gain", this.getGain());
		nbt.putInt("Strength", this.getStrength());
		nbt.putInt("LayTimer", this.layTimer);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.set(TYPE, nbt.getString("Type"));
		this.setStatsAnalyzed(nbt.getBoolean("Analyzed"));
		this.setGrowth(nbt.contains("Growth") ? nbt.getInt("Growth") : 1);
		this.setGain(nbt.contains("Gain") ? nbt.getInt("Gain") : 1);
		this.setStrength(nbt.contains("Strength") ? nbt.getInt("Strength") : 1);
		if (nbt.contains("LayTimer")) {
			this.layTimer = nbt.getInt("LayTimer");
			this.setLayProgress(this.layTimer / 2400);
		} else {
			this.resetTimeUntilNextEgg();
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, net.minecraft.block.BlockState state) {
		if (this.random.nextFloat() <= 0.1F) {
			super.playStepSound(pos, state);
		}
	}

	/** Group data so a spawned flock shares one chicken kind. */
	public record ChickenGroupData(String type) implements EntityData {
	}
}
