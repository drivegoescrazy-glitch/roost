package lucraft.mods.lucraftcore.superpowers;

import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConfig;
import lucraft.mods.lucraftcore.superpowers.abilities.AttributeAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.CommandRunnerAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.EnergyBlastAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.FlightAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.GeneratorAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.HealingAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.PotionAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.PunchAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.SizeAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.SprintAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.TeleportAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.TogglePotionAbility;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbilityRegistry {

    @FunctionalInterface
    public interface AbilityFactory {
        Ability create(ServerPlayerEntity player, AbilityConfig config);
    }

    public static final Map<String, AbilityFactory> ABILITIES = new LinkedHashMap<>();

    static {
        register("flight", (player, config) -> new FlightAbility(player,
                (float) config.getDouble("speed", 0.1D), (float) config.getDouble("sprint_speed", 0.2D)));
        register("speed", (player, config) -> attribute(player, "speed",
                EntityAttributes.GENERIC_MOVEMENT_SPEED, config.getDouble("amount", 0.3D),
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        register("strength", (player, config) -> attribute(player, "strength",
                EntityAttributes.GENERIC_ATTACK_DAMAGE, config.getDouble("amount", 4.0D),
                EntityAttributeModifier.Operation.ADD_VALUE));
        register("health", (player, config) -> attribute(player, "health",
                EntityAttributes.GENERIC_MAX_HEALTH, config.getDouble("amount", 10.0D),
                EntityAttributeModifier.Operation.ADD_VALUE));
        register("jump_boost", (player, config) -> attribute(player, "jump_boost",
                EntityAttributes.GENERIC_JUMP_STRENGTH, config.getDouble("amount", 0.2D),
                EntityAttributeModifier.Operation.ADD_VALUE));
        register("step_assist", (player, config) -> attribute(player, "step_assist",
                EntityAttributes.GENERIC_STEP_HEIGHT, config.getDouble("amount", 0.5D),
                EntityAttributeModifier.Operation.ADD_VALUE));
        register("knockback_resistance", (player, config) -> attribute(player, "knockback_resistance",
                EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, config.getDouble("amount", 0.5D),
                EntityAttributeModifier.Operation.ADD_VALUE));
        register("fall_resistance", (player, config) -> attribute(player, "fall_resistance",
                EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, config.getDouble("amount", -0.5D),
                EntityAttributeModifier.Operation.ADD_VALUE));
        register("damage_resistance", (player, config) -> attribute(player, "damage_resistance",
                EntityAttributes.GENERIC_ARMOR, config.getDouble("amount", 4.0D),
                EntityAttributeModifier.Operation.ADD_VALUE));
        register("water_breathing", (player, config) -> new PotionAbility(player, "water_breathing",
                StatusEffects.WATER_BREATHING, config.getInt("amplifier", 0)));
        register("tough_lungs", (player, config) -> new PotionAbility(player, "tough_lungs",
                StatusEffects.WATER_BREATHING, config.getInt("amplifier", 0)));
        register("fire_resistance", (player, config) -> new PotionAbility(player, "fire_resistance",
                StatusEffects.FIRE_RESISTANCE, config.getInt("amplifier", 0)));
        register("slowfall", (player, config) -> new PotionAbility(player, "slowfall",
                StatusEffects.SLOW_FALLING, config.getInt("amplifier", 0)));
        register("invisibility", (player, config) -> new TogglePotionAbility(player, "invisibility",
                StatusEffects.INVISIBILITY, config.getInt("amplifier", 0)));
        register("healing", (player, config) -> new HealingAbility(player,
                config.getInt("frequency", 20), (float) config.getDouble("amount", 1.0D)));
        register("teleport", (player, config) -> new TeleportAbility(player, config.getDouble("distance", 30.0D)));
        register("size_change", (player, config) -> new SizeAbility(player,
                (float) config.getDouble("size", 2.0D), (float) config.getDouble("default_size", 1.0D)));
        register("sprint", SprintAbility::new);
        register("generator", GeneratorAbility::new);
        register("energy_blast", EnergyBlastAbility::new);
        register("punch", PunchAbility::new);
        register("command", CommandRunnerAbility::new);
    }

    private static Ability attribute(ServerPlayerEntity player, String id,
                                     net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute,
                                     double amount, EntityAttributeModifier.Operation operation) {
        return new AttributeAbility(player, id, attribute, amount, operation);
    }

    public static void register(String id, AbilityFactory factory) {
        ABILITIES.put(id, factory);
    }

    public static List<String> ids() {
        return List.copyOf(ABILITIES.keySet());
    }

    public static boolean exists(String id) {
        return ABILITIES.containsKey(normalize(id));
    }

    public static Ability create(String id, ServerPlayerEntity player) {
        return create(AbilityConfig.of(normalize(id)), player);
    }

    public static Ability create(AbilityConfig config, ServerPlayerEntity player) {
        AbilityFactory factory = ABILITIES.get(normalize(config.abilityId()));
        if (factory == null)
            return null;
        Ability ability = factory.create(player, config);
        if (ability == null)
            return null;
        ability.configure(config.energyCost(), config.cooldown(), config.conditions());
        return ability;
    }

    /** Accepts both "flight" and "lucraftcore:flight". */
    public static String normalize(String id) {
        if (id == null)
            return "";
        return id.startsWith("lucraftcore:") ? id.substring("lucraftcore:".length()) : id;
    }
}
