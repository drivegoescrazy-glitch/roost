package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** C2S request to use or toggle an ability from the ability bar. */
public record UseAbilityPayload(String abilityId) implements CustomPayload {

    public static final CustomPayload.Id<UseAbilityPayload> ID = new CustomPayload.Id<>(LucraftCore.id("use_ability"));

    public static final PacketCodec<RegistryByteBuf, UseAbilityPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, UseAbilityPayload::abilityId,
            UseAbilityPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
