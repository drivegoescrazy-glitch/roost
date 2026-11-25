package lucraft.mods.lucraftcore.superpowers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lucraft.mods.lucraftcore.LucraftCore;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/** Stores and changes a player's superpower, XP and level. */
public final class SuperpowerHandler {

    public static final AttachmentType<SuperpowerData> SUPERPOWER = AttachmentRegistry.createPersistent(
            LucraftCore.id("superpower"), SuperpowerData.CODEC);

    private SuperpowerHandler() {
    }

    /** Forces the attachment type to register before player data is read. */
    public static void registerAttachments() {
        java.util.Objects.requireNonNull(SUPERPOWER);
    }

    public record SuperpowerData(Identifier power, int xp, int level) {

        public static final Codec<SuperpowerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("power").forGetter(SuperpowerData::power),
                Codec.INT.optionalFieldOf("xp", 0).forGetter(SuperpowerData::xp),
                Codec.INT.optionalFieldOf("level", 1).forGetter(SuperpowerData::level)
        ).apply(instance, SuperpowerData::new));
    }

    public static SuperpowerData getData(ServerPlayerEntity player) {
        return player.getAttached(SUPERPOWER);
    }

    public static Superpower getPower(ServerPlayerEntity player) {
        SuperpowerData data = getData(player);
        return data == null ? null : SuperpowerRegistry.get(data.power());
    }

    public static int getLevel(ServerPlayerEntity player) {
        SuperpowerData data = getData(player);
        return data == null ? 0 : data.level();
    }

    public static int getXp(ServerPlayerEntity player) {
        SuperpowerData data = getData(player);
        return data == null ? 0 : data.xp();
    }

    public static void setPower(ServerPlayerEntity player, Superpower power) {
        player.setAttached(SUPERPOWER, new SuperpowerData(power.getId(), 0, 1));
        PlayerAbilities.rebuildPower(player);
    }

    public static void removePower(ServerPlayerEntity player) {
        player.removeAttached(SUPERPOWER);
        PlayerAbilities.rebuildPower(player);
    }

    /** Adds XP, levels up when a threshold is reached, and returns levels gained. */
    public static int addXp(ServerPlayerEntity player, int amount) {
        SuperpowerData data = getData(player);
        Superpower power = getPower(player);
        if (data == null || power == null || amount == 0)
            return 0;
        int xp = Math.max(0, data.xp() + amount);
        int level = power.getLevelForXp(xp);
        int gained = Math.max(0, level - data.level());
        player.setAttached(SUPERPOWER, new SuperpowerData(data.power(), xp, level));
        if (gained > 0)
            PlayerAbilities.rebuildPower(player);
        return gained;
    }

    public static int getXpToNextLevel(ServerPlayerEntity player) {
        Superpower power = getPower(player);
        if (power == null)
            return 0;
        int level = getLevel(player);
        if (level >= power.getMaxLevel())
            return 0;
        return Math.max(0, power.getXpForLevel(level + 1) - getXp(player));
    }
}
