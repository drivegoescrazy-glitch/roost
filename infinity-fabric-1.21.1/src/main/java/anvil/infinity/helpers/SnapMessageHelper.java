package anvil.infinity.helpers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/** Broadcasts the "was dusted away" line, as the original helper did. */
public final class SnapMessageHelper {

    private SnapMessageHelper() {
    }

    public static void deathMessage(LivingEntity entity) {
        MinecraftServer server = entity.getServer();
        if (server == null)
            return;
        MutableText message = Text.literal(entity.getName().getString() + " ")
                .append(Text.translatable("infinity.snap.death"));
        server.getPlayerManager().broadcast(message, false);
    }
}
