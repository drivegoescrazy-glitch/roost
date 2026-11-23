package lucraft.mods.lucraftcore.superpowers.effects;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Applies and removes effects per player and per source ("suit", "power").
 * Sources are evaluated every tick so conditions and equipment changes take
 * effect immediately.
 */
public final class EffectHandler {

    private static final Map<UUID, Map<String, List<Active>>> ACTIVE = new HashMap<>();

    private EffectHandler() {
    }

    public static void tick(ServerPlayerEntity player, String source, List<Effect> effects) {
        Map<String, List<Active>> sources = ACTIVE.computeIfAbsent(player.getUuid(), uuid -> new HashMap<>());
        List<Active> active = sources.computeIfAbsent(source, key -> new ArrayList<>());

        active.removeIf(entry -> {
            if (!entry.effect.areConditionsMet(player)) {
                entry.effect.onRemove(player);
                return true;
            }
            return false;
        });

        for (Effect effect : effects) {
            if (!effect.areConditionsMet(player))
                continue;
            boolean present = false;
            for (Active entry : active)
                if (entry.effect == effect) {
                    present = true;
                    break;
                }
            if (!present) {
                effect.onApply(player);
                active.add(new Active(effect));
            }
        }

        for (Active entry : active) {
            entry.effect.tick(player, entry.ticks);
            entry.ticks++;
        }
    }

    public static void clear(ServerPlayerEntity player, String source) {
        Map<String, List<Active>> sources = ACTIVE.get(player.getUuid());
        if (sources == null)
            return;
        List<Active> active = sources.remove(source);
        if (active != null)
            for (Active entry : active)
                entry.effect.onRemove(player);
    }

    public static void clearAll(ServerPlayerEntity player) {
        Map<String, List<Active>> sources = ACTIVE.remove(player.getUuid());
        if (sources == null)
            return;
        for (List<Active> active : sources.values())
            for (Active entry : active)
                entry.effect.onRemove(player);
    }

    private static final class Active {
        private final Effect effect;
        private int ticks;

        private Active(Effect effect) {
            this.effect = effect;
        }
    }
}
