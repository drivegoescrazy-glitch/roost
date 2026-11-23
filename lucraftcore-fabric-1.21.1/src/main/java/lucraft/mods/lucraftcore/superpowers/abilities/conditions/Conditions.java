package lucraft.mods.lucraftcore.superpowers.abilities.conditions;

import lucraft.mods.lucraftcore.addonpacks.AddonPackHandler;
import lucraft.mods.lucraftcore.superpowers.PlayerAbilities;
import lucraft.mods.lucraftcore.superpowers.Superpower;
import lucraft.mods.lucraftcore.superpowers.SuperpowerHandler;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

/** Built-in ability conditions, matching the original addon-pack condition types. */
public final class Conditions {

    private Conditions() {
    }

    public record HeldItem(Identifier item) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            net.minecraft.item.Item held = Registries.ITEM.get(item);
            return held != null && (player.getMainHandStack().isOf(held) || player.getOffHandStack().isOf(held));
        }

        @Override
        public String type() {
            return "held_item";
        }
    }

    public record HasSuperpower(Identifier power) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            Superpower superpower = SuperpowerHandler.getPower(player);
            return superpower != null && superpower.getId().equals(power);
        }

        @Override
        public String type() {
            return "superpower";
        }
    }

    public record Level(int level) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            return SuperpowerHandler.getLevel(player) >= level;
        }

        @Override
        public String type() {
            return "level";
        }
    }

    public record AbilityEnabled(String abilityId) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            return PlayerAbilities.isActive(player, abilityId);
        }

        @Override
        public String type() {
            return "ability";
        }
    }

    public record ScoreboardTag(String tag) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            return player.getCommandTags().contains(tag);
        }

        @Override
        public String type() {
            return "scoreboard_tag";
        }
    }

    public record HasEffect(Identifier effect) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            StatusEffect statusEffect = Registries.STATUS_EFFECT.get(effect);
            if (statusEffect == null)
                return false;
            RegistryEntry<StatusEffect> entry = Registries.STATUS_EFFECT.getEntry(statusEffect);
            return entry != null && player.hasStatusEffect(entry);
        }

        @Override
        public String type() {
            return "potion";
        }
    }

    public record AddonPack(String pack) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            return AddonPackHandler.getPacks().stream().anyMatch(info -> info.getFileName().equals(pack));
        }

        @Override
        public String type() {
            return "addonpack";
        }
    }

    public record All(List<AbilityCondition> conditions) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            for (AbilityCondition condition : conditions)
                if (!condition.test(player))
                    return false;
            return true;
        }

        @Override
        public String type() {
            return "and";
        }
    }

    public record Any(List<AbilityCondition> conditions) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            for (AbilityCondition condition : conditions)
                if (condition.test(player))
                    return true;
            return false;
        }

        @Override
        public String type() {
            return "or";
        }
    }

    public record Not(AbilityCondition condition) implements AbilityCondition {
        @Override
        public boolean test(ServerPlayerEntity player) {
            return !condition.test(player);
        }

        @Override
        public String type() {
            return "not";
        }
    }
}
