package anvil.infinity.capabilities;

import anvil.infinity.Infinity;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/** Access to the per player infinity data (the original gauntlet capability). */
public final class GauntletUserInformation {

    public static final AttachmentType<SnapPlayerData> PLAYER_DATA = AttachmentRegistry.createPersistent(
            Infinity.id("player_data"), SnapPlayerData.CODEC);

    private GauntletUserInformation() {
    }

    /** The player's data, created with the original defaults when absent. */
    public static SnapPlayerData get(PlayerEntity player) {
        return player.getAttachedOrCreate(PLAYER_DATA, SnapPlayerData::new);
    }

    public static void set(PlayerEntity player, SnapPlayerData data) {
        player.setAttached(PLAYER_DATA, data);
    }

    /** Non player entities get a throwaway copy, exactly like the original. */
    public static SnapPlayerData getDataByEntity(LivingEntity entity) {
        if (entity instanceof PlayerEntity player)
            return get(player);
        return new SnapPlayerData();
    }

    public static void logRegistration() {
        // Touch the field so the persistent attachment type registers during mod
        // initialization, before a world reads saved player data.
        java.util.Objects.requireNonNull(PLAYER_DATA);
        Infinity.LOGGER.debug("Registered infinity player data attachment");
    }
}
