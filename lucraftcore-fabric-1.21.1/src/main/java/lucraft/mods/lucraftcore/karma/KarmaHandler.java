package lucraft.mods.lucraftcore.karma;

import com.mojang.serialization.Codec;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.karma.KarmaStat.KarmaClass;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class KarmaHandler {

    public static final AttachmentType<Map<String, Integer>> KARMA = AttachmentRegistry.createPersistent(
            LucraftCore.id("karma"), Codec.unboundedMap(Codec.STRING, Codec.INT));

    public static Map<String, Integer> getStats(PlayerEntity player) {
        return player.getAttachedOrCreate(KARMA, HashMap::new);
    }

    public static int getKarmaStat(PlayerEntity player, KarmaStat stat) {
        return getStats(player).getOrDefault(stat.getName(), 0);
    }

    public static void setKarmaStat(PlayerEntity player, KarmaStat stat, int amount) {
        Map<String, Integer> stats = getStats(player);
        stats.put(stat.getName(), amount);
        player.setAttached(KARMA, stats);
    }

    public static void increaseKarmaStat(PlayerEntity player, KarmaStat stat, int amount) {
        setKarmaStat(player, stat, getKarmaStat(player, stat) + amount);
    }

    public static void increaseKarmaStat(PlayerEntity player, KarmaStat stat) {
        increaseKarmaStat(player, stat, 1);
    }

    public static int getKarma(PlayerEntity player) {
        Map<String, Integer> stats = getStats(player);
        int karma = 0;
        for (KarmaStat stat : KarmaStat.ALL)
            karma += stat.getAmount() * stats.getOrDefault(stat.getName(), 0);
        return karma;
    }

    public static KarmaClass getKarmaClass(PlayerEntity player) {
        int karma = getKarma(player);
        for (KarmaClass karmaClass : KarmaClass.values()) {
            if (karmaClass.contains(karma))
                return karmaClass;
        }
        return KarmaClass.NEUTRAL;
    }

    public static boolean isEvilPlayer(PlayerEntity player) {
        return getKarma(player) <= -26;
    }

    public static boolean isGoodPlayer(PlayerEntity player) {
        return getKarma(player) >= 26;
    }

    public static boolean isMonster(LivingEntity entity) {
        return entity instanceof Monster || entity.getType().getSpawnGroup() == SpawnGroup.MONSTER;
    }

    public static boolean isGoodEntity(LivingEntity entity) {
        if (entity instanceof PlayerEntity player)
            return isGoodPlayer(player);
        return entity instanceof VillagerEntity;
    }

    /** Awards karma for a player kill, mirroring the original death handler. */
    public static void onKill(ServerPlayerEntity player, LivingEntity victim) {
        if (isMonster(victim)) {
            increaseKarmaStat(player, KarmaStat.MONSTER_KILL);
        } else if (victim instanceof PlayerEntity target) {
            if (isGoodPlayer(target))
                increaseKarmaStat(player, KarmaStat.GOOD_PLAYER_KILL);
            else if (isEvilPlayer(target))
                increaseKarmaStat(player, KarmaStat.BAD_PLAYER_KILL);
        } else if (isGoodEntity(victim)) {
            increaseKarmaStat(player, KarmaStat.GOOD_PLAYER_KILL);
        }
    }
}
