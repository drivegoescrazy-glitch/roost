package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** C2S start/stop of a held ability while the use key is down. */
public record HoldAbilityPayload(String abilityId, boolean held) implements CustomPayload {

    public static final CustomPayload.Id<HoldAbilityPayload> ID = new CustomPayload.Id<>(LucraftCore.id("hold_ability"));

    public static final PacketCodec<RegistryByteBuf, HoldAbilityPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, HoldAbilityPayload::abilityId,
            PacketCodecs.BOOL, HoldAbilityPayload::held,
            HoldAbilityPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
