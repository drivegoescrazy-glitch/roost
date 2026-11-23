package anvil.infinity.abilities;

import anvil.infinity.Infinity;
import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityRegistry;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConfig;
import lucraft.mods.lucraftcore.superpowers.abilities.AttributeAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.EnergyBlastAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.FlightAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.HealingAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.PunchAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.SizeAbility;
import lucraft.mods.lucraftcore.superpowers.abilities.TogglePotionAbility;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers the stone abilities under the ids the stones hand to the gauntlet.
 * Ids that Lucraft already uses for its own superpowers are prefixed with
 * {@code infinity_} so a stone never replaces the core ability of the same name.
 */
public final class InfinityAbilities {

    /** Every id this mod registered, in registration order. */
    public static final List<String> REGISTERED = new ArrayList<>();

    private InfinityAbilities() {
    }

    public static void register() {
        // Abilities the original mod implemented itself.
        register("snap", (player, config) -> new AbilitySnap(player));
        register("kill", (player, config) -> new AbilityKill(player));
        register("changesnap", (player, config) -> new AbilityChangeSnap(player));
        register("saturation", (player, config) -> new AbilitySaturation(player));
        register("fast_forward", (player, config) -> new AbilityFastForward(player));
        register("stop_time", (player, config) -> new AbilityStopTime(player));
        register("locate", (player, config) -> new AbilityLocate(player));

        // Stone passives, built from the core ability classes with the values
        // the original set on them.
        register("immortality", (player, config) -> new ImmortalityAbility(player));
        register("infinity_healing", (player, config) -> new HealingAbility(player, 1, 1.0F) {
            @Override
            public String getId() {
                return "infinity_healing";
            }
        });
        register("infinity_strength", (player, config) -> new AttributeAbility(player, "infinity_strength",
                EntityAttributes.GENERIC_ATTACK_DAMAGE, 1000.0D, EntityAttributeModifier.Operation.ADD_VALUE));
        register("infinity_resistance", (player, config) -> new AttributeAbility(player, "infinity_resistance",
                EntityAttributes.GENERIC_ARMOR, 1000.0D, EntityAttributeModifier.Operation.ADD_VALUE));
        register("infinity_punch", (player, config) -> new PunchAbility(player,
                config("infinity_punch", 1000.0D, 3.5D)));
        register("infinity_blast", (player, config) -> new EnergyBlastAbility(player,
                config("infinity_blast", 1000.0D, 24.0D)));
        register("infinity_flight", (player, config) -> new FlightAbility(player, 0.2F, 0.3F) {
            @Override
            public String getId() {
                return "infinity_flight";
            }
        });
        register("infinity_invisibility", (player, config) -> new TogglePotionAbility(player,
                "infinity_invisibility", StatusEffects.INVISIBILITY, 0));
        register("infinity_size", (player, config) -> new SizeAbility(player,
                anvil.infinity.config.InfinityConfig.size, 1.0F) {
            @Override
            public String getId() {
                return "infinity_size";
            }
        });

        Infinity.LOGGER.debug("Registered infinity abilities: {}", REGISTERED);
    }

    private static void register(String id, AbilityRegistry.AbilityFactory factory) {
        AbilityRegistry.register(id, factory);
        REGISTERED.add(id);
    }

    /** Builds an ability config with damage/range values in the core's format. */
    public static AbilityConfig config(String id, double damage, double range) {
        JsonObject data = new JsonObject();
        data.addProperty("damage", damage);
        data.addProperty("range", range);
        data.addProperty("reach", range);
        return new AbilityConfig(id, 1, 0, 0, data, List.of());
    }

    public static Identifier id(String path) {
        return Infinity.id(path);
    }

    /** True when the ability exists (used when resolving optional addon ids). */
    public static boolean exists(String id) {
        return AbilityRegistry.exists(id);
    }

    /** Convenience for other classes that need a fresh ability instance. */
    public static Ability create(String id, net.minecraft.server.network.ServerPlayerEntity player) {
        return AbilityRegistry.create(id, player);
    }
}
