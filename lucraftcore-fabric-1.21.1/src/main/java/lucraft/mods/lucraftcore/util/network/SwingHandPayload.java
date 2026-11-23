package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** Asks clients to show an arm swing on an entity. */
public record SwingHandPayload(int entityId, boolean mainHand) implements CustomPayload {

    public static final CustomPayload.Id<SwingHandPayload> ID = new CustomPayload.Id<>(LucraftCore.id("swing_hand"));

    public static final PacketCodec<RegistryByteBuf, SwingHandPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, SwingHandPayload::entityId,
            PacketCodecs.BOOL, SwingHandPayload::mainHand,
            SwingHandPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
