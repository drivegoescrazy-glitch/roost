package lucraft.mods.lucraftcore.superpowers.effects;

import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition;
import lucraft.mods.lucraftcore.util.network.LCNetworking;
import lucraft.mods.lucraftcore.util.particles.LCParticles;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Built-in suit and superpower effects. */
public final class Effects {

    private Effects() {
    }

    /** Base class carrying the JSON config and conditions. */
    public abstract static class Configured implements Effect {

        protected final JsonObject data;
        private final List<AbilityCondition> conditions;

        protected Configured(JsonObject data, List<AbilityCondition> conditions) {
            this.data = data == null ? new JsonObject() : data;
            this.conditions = conditions == null ? List.of() : List.copyOf(conditions);
        }

        @Override
        public List<AbilityCondition> conditions() {
            return conditions;
        }

        protected int getInt(String key, int fallback) {
            return data.has(key) ? data.get(key).getAsInt() : fallback;
        }

        protected double getDouble(String key, double fallback) {
            return data.has(key) ? data.get(key).getAsDouble() : fallback;
        }

        protected String getString(String key, String fallback) {
            return data.has(key) ? data.get(key).getAsString() : fallback;
        }
    }

    /** Applies the vanilla glowing outline while active. */
    public static class Glow extends Configured {

        public Glow(JsonObject data, List<AbilityCondition> conditions) {
            super(data, conditions);
        }

        @Override
        public String type() {
            return "glow";
        }

        @Override
        public void onApply(ServerPlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING,
                    StatusEffectInstance.INFINITE, 0, true, false, false));
        }

        @Override
        public void onRemove(ServerPlayerEntity player) {
            player.removeStatusEffect(StatusEffects.GLOWING);
        }
    }

    /** Leaves a particle trail behind the player. */
    public static class Trail extends Configured {

        public Trail(JsonObject data, List<AbilityCondition> conditions) {
            super(data, conditions);
        }

        @Override
        public String type() {
            return "trail";
        }

        @Override
        public void tick(ServerPlayerEntity player, int ticks) {
            int interval = Math.max(1, getInt("interval", 2));
            if (ticks % interval != 0)
                return;
            int count = Math.max(1, getInt("count", 1));
            LCNetworking.sendParticle(player.getServerWorld(), player.getPos().add(0, 0.9D, 0),
                    LCParticles.COLORED_CLOUD, Vec3d.ZERO, count);
        }
    }

    /** Spawns particles in a small radius around the player. */
    public static class Aura extends Configured {

        public Aura(JsonObject data, List<AbilityCondition> conditions) {
            super(data, conditions);
        }

        @Override
        public String type() {
            return "aura";
        }

        @Override
        public void tick(ServerPlayerEntity player, int ticks) {
            int interval = Math.max(1, getInt("interval", 5));
            if (ticks % interval != 0)
                return;
            double radius = Math.max(0.1D, getDouble("radius", 0.8D));
            int count = Math.max(1, getInt("count", 2));
            for (int index = 0; index < count; index++) {
                double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;
                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;
                Vec3d position = player.getPos().add(offsetX, player.getRandom().nextDouble() * 1.6D, offsetZ);
                LCNetworking.sendParticle(player.getServerWorld(), position, LCParticles.COLORED_CLOUD, Vec3d.ZERO, 1);
            }
        }
    }

    /** Renames the player while active and restores the old name afterwards. */
    public static class NameChange extends Configured {

        private static final Map<UUID, Text> ORIGINAL_NAMES = new HashMap<>();

        public NameChange(JsonObject data, List<AbilityCondition> conditions) {
            super(data, conditions);
        }

        @Override
        public String type() {
            return "name_change";
        }

        @Override
        public void onApply(ServerPlayerEntity player) {
            ORIGINAL_NAMES.putIfAbsent(player.getUuid(), player.getCustomName());
            player.setCustomName(Text.literal(getString("name", player.getName().getString())));
            player.setCustomNameVisible(true);
        }

        @Override
        public void onRemove(ServerPlayerEntity player) {
            Text original = ORIGINAL_NAMES.remove(player.getUuid());
            player.setCustomName(original);
            player.setCustomNameVisible(original != null);
        }
    }
}
