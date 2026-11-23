package lucraft.mods.lucraftcore.superpowers;

import com.mojang.serialization.Codec;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.infinity.items.InventoryInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import net.minecraft.item.ItemStack;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConfig;
import lucraft.mods.lucraftcore.superpowers.suitsets.SuitSet;
import lucraft.mods.lucraftcore.superpowers.suitsets.SuitSetRegistry;
import lucraft.mods.lucraftcore.util.triggers.LCriteria;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAbilities {

    public static final AttachmentType<Map<String, Boolean>> GRANTED = AttachmentRegistry.createPersistent(
            LucraftCore.id("abilities"), Codec.unboundedMap(Codec.STRING, Codec.BOOL));

    private static final Map<UUID, Map<String, Ability>> CACHE = new HashMap<>();
    private static final Map<UUID, Map<String, Ability>> SUIT_CACHE = new HashMap<>();
    private static final Map<UUID, Identifier> ACTIVE_SUIT = new HashMap<>();
    private static final Map<UUID, Map<String, Ability>> POWER_CACHE = new HashMap<>();
    private static final Map<UUID, String> POWER_KEY = new HashMap<>();
    private static final Map<UUID, Map<String, Ability>> GAUNTLET_CACHE = new HashMap<>();
    private static final Map<UUID, String> GAUNTLET_KEY = new HashMap<>();

    public static Map<String, Boolean> getGranted(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(GRANTED, LinkedHashMap::new);
    }

    public static boolean grant(ServerPlayerEntity player, String id) {
        if (!AbilityRegistry.exists(id))
            return false;
        Map<String, Boolean> granted = getGranted(player);
        if (granted.containsKey(id))
            return false;
        boolean defaultEnabled = AbilityRegistry.create(id, player).getType() != AbilityType.TOGGLE;
        granted.put(id, defaultEnabled);
        player.setAttached(GRANTED, granted);

        Ability ability = AbilityRegistry.create(id, player);
        cache(player).put(id, ability);
        ability.setEnabled(defaultEnabled);
        ability.onGrant();
        if (defaultEnabled)
            ability.setEnabled(true);
        return true;
    }

    public static boolean revoke(ServerPlayerEntity player, String id) {
        Map<String, Boolean> granted = getGranted(player);
        if (!granted.containsKey(id))
            return false;
        granted.remove(id);
        player.setAttached(GRANTED, granted);
        Ability ability = cache(player).remove(id);
        if (ability != null)
            ability.onRevoke();
        return true;
    }

    public static boolean use(ServerPlayerEntity player, String id) {
        Ability ability = ability(player, id);
        if (ability == null)
            return false;
        boolean result = ability.use();
        saveState(player, id, ability);
        if (result) {
            LCriteria.ABILITY_USED.trigger(player, id);
            AbilityBarSync.send(player);
        }
        return result;
    }

    public static void toggle(ServerPlayerEntity player, String id) {
        use(player, id);
    }

    public static Ability ability(ServerPlayerEntity player, String id) {
        Map<String, Ability> map = cache(player);
        Ability ability = map.get(id);
        if (ability == null && getGranted(player).containsKey(id)) {
            ability = AbilityRegistry.create(id, player);
            if (ability == null)
                return null;
            map.put(id, ability);
            ability.setEnabled(getGranted(player).getOrDefault(id, true));
            if (ability.isEnabled())
                ability.onGrant();
        }
        return ability;
    }

    public static void tick(ServerPlayerEntity player) {
        for (String id : getGranted(player).keySet()) {
            Ability ability = ability(player, id);
            if (ability != null)
                ability.tick();
        }
        Map<String, Ability> suit = SUIT_CACHE.get(player.getUuid());
        if (suit != null) {
            for (Ability ability : suit.values())
                ability.tick();
        }
    }

    public static void sync(ServerPlayerEntity player) {
        rebuildPower(player);
        Map<String, Ability> old = CACHE.remove(player.getUuid());
        if (old != null) {
            for (Ability ability : old.values())
                ability.onRevoke();
        }
        for (Map.Entry<String, Boolean> entry : getGranted(player).entrySet()) {
            Ability ability = AbilityRegistry.create(entry.getKey(), player);
            if (ability == null)
                continue;
            cache(player).put(entry.getKey(), ability);
            boolean enabled = entry.getValue();
            if (enabled || ability.getType() == AbilityType.CONSTANT)
                ability.setEnabled(true);
            ability.onGrant();
            if (enabled)
                ability.setEnabled(true);
        }
    }

    /** Applies/removes the abilities of the currently worn suit set. */
    public static void tickSuit(ServerPlayerEntity player) {
        SuitSet worn = SuitSet.getWornSuit(player);
        Identifier wanted = worn == null ? null : worn.getId();
        Identifier active = ACTIVE_SUIT.get(player.getUuid());
        if (java.util.Objects.equals(wanted, active))
            return;

        Map<String, Ability> suitAbilities = SUIT_CACHE.remove(player.getUuid());
        if (suitAbilities != null) {
            for (Ability ability : suitAbilities.values())
                ability.onRevoke();
        }
        if (worn != null) {
            Map<String, Ability> map = new LinkedHashMap<>();
            for (String id : worn.getAbilities()) {
                Ability ability = AbilityRegistry.create(id, player);
                if (ability == null)
                    continue;
                ability.setEnabled(true);
                map.put(id, ability);
            }
            SUIT_CACHE.put(player.getUuid(), map);
            ACTIVE_SUIT.put(player.getUuid(), worn.getId());
        } else {
            ACTIVE_SUIT.remove(player.getUuid());
        }
    }

    /** Applies the abilities and tick hooks of stones in a held gauntlet. */
    public static void tickGauntlet(ServerPlayerEntity player) {
        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        ItemStack gauntlet = main.getItem() instanceof ItemInfinityGauntlet ? main
                : off.getItem() instanceof ItemInfinityGauntlet ? off : ItemStack.EMPTY;

        List<String> ids = new ArrayList<>();
        if (!gauntlet.isEmpty()) {
            InventoryInfinityGauntlet inventory = new InventoryInfinityGauntlet(gauntlet);
            for (ItemStack stack : inventory.getHeldStacks()) {
                if (stack.getItem() instanceof ItemInfinityStone stone) {
                    stone.onGauntletTick(player, player.getWorld(), gauntlet);
                    ids.addAll(stone.getGauntletAbilities());
                }
            }
        }

        String key = String.join(",", ids);
        UUID uuid = player.getUuid();
        if (!key.equals(GAUNTLET_KEY.get(uuid))) {
            Map<String, Ability> old = GAUNTLET_CACHE.remove(uuid);
            if (old != null)
                for (Ability ability : old.values())
                    ability.onRevoke();
            Map<String, Ability> map = new LinkedHashMap<>();
            for (String id : ids) {
                Ability ability = AbilityRegistry.create(id, player);
                if (ability == null)
                    continue;
                ability.onGrant();
                ability.setEnabled(true);
                map.put(id, ability);
            }
            if (map.isEmpty()) {
                GAUNTLET_KEY.remove(uuid);
            } else {
                GAUNTLET_CACHE.put(uuid, map);
                GAUNTLET_KEY.put(uuid, key);
            }
            return;
        }
        Map<String, Ability> map = GAUNTLET_CACHE.get(uuid);
        if (map != null)
            for (Ability ability : map.values())
                ability.tick();
    }

    /** True when the player currently has this ability enabled from any source. */
    public static boolean isActive(ServerPlayerEntity player, String abilityId) {
        String id = AbilityRegistry.normalize(abilityId);
        Map<String, Ability> direct = CACHE.get(player.getUuid());
        if (direct != null && direct.containsKey(id))
            return direct.get(id).isEnabled();
        Map<String, Ability> suit = SUIT_CACHE.get(player.getUuid());
        if (suit != null && suit.containsKey(id))
            return suit.get(id).isEnabled();
        Map<String, Ability> power = POWER_CACHE.get(player.getUuid());
        if (power != null && power.containsKey(id))
            return power.get(id).isEnabled();
        Map<String, Ability> gauntlet = GAUNTLET_CACHE.get(player.getUuid());
        return gauntlet != null && gauntlet.containsKey(id) && gauntlet.get(id).isEnabled();
    }

    /** Rebuilds the ability set granted by the player's superpower and level. */
    public static void rebuildPower(ServerPlayerEntity player) {
        Map<String, Ability> old = POWER_CACHE.remove(player.getUuid());
        if (old != null) {
            for (Ability ability : old.values())
                ability.onRevoke();
        }
        Superpower power = SuperpowerHandler.getPower(player);
        if (power == null) {
            POWER_KEY.remove(player.getUuid());
            return;
        }
        int level = SuperpowerHandler.getLevel(player);
        Map<String, Ability> map = new LinkedHashMap<>();
        for (AbilityConfig config : power.getAbilitiesForLevel(level)) {
            Ability ability = AbilityRegistry.create(config, player);
            if (ability == null)
                continue;
            ability.onGrant();
            map.put(AbilityRegistry.normalize(config.abilityId()), ability);
        }
        POWER_CACHE.put(player.getUuid(), map);
        POWER_KEY.put(player.getUuid(), power.getId() + "@" + level);
    }

    /** Ticks power abilities, rebuilding them when the power or level changes. */
    public static void tickPower(ServerPlayerEntity player) {
        Superpower power = SuperpowerHandler.getPower(player);
        int level = SuperpowerHandler.getLevel(player);
        String key = power == null ? "" : power.getId() + "@" + level;
        if (!key.equals(POWER_KEY.get(player.getUuid()))) {
            rebuildPower(player);
            return;
        }
        Map<String, Ability> abilities = POWER_CACHE.get(player.getUuid());
        if (abilities != null) {
            for (Ability ability : abilities.values())
                ability.tick();
        }
    }

    /** Abilities shown in the ability bar: command grants, suit and power. */
    public static Map<String, Ability> getDisplayed(ServerPlayerEntity player) {
        Map<String, Ability> displayed = new LinkedHashMap<>();
        for (String id : getGranted(player).keySet()) {
            Ability ability = ability(player, id);
            if (ability != null)
                displayed.put(id, ability);
        }
        Map<String, Ability> suit = SUIT_CACHE.get(player.getUuid());
        if (suit != null)
            displayed.putAll(suit);
        Map<String, Ability> power = POWER_CACHE.get(player.getUuid());
        if (power != null)
            displayed.putAll(power);
        Map<String, Ability> gauntlet = GAUNTLET_CACHE.get(player.getUuid());
        if (gauntlet != null)
            displayed.putAll(gauntlet);
        return displayed;
    }

    public static void clear(ServerPlayerEntity player) {
        CACHE.remove(player.getUuid());
        SUIT_CACHE.remove(player.getUuid());
        ACTIVE_SUIT.remove(player.getUuid());
        POWER_CACHE.remove(player.getUuid());
        POWER_KEY.remove(player.getUuid());
        Map<String, Ability> gauntlet = GAUNTLET_CACHE.remove(player.getUuid());
        if (gauntlet != null)
            for (Ability ability : gauntlet.values())
                ability.onRevoke();
        GAUNTLET_KEY.remove(player.getUuid());
    }

    private static void saveState(ServerPlayerEntity player, String id, Ability ability) {
        Map<String, Boolean> granted = getGranted(player);
        granted.put(id, ability.isEnabled());
        player.setAttached(GRANTED, granted);
    }

    private static Map<String, Ability> cache(ServerPlayerEntity player) {
        return CACHE.computeIfAbsent(player.getUuid(), uuid -> new LinkedHashMap<>());
    }
}
