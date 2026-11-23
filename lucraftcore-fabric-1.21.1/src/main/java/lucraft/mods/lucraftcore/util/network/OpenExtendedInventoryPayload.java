package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** C2S request from the extended inventory keybind. */
public record OpenExtendedInventoryPayload() implements CustomPayload {

    public static final OpenExtendedInventoryPayload INSTANCE = new OpenExtendedInventoryPayload();

    public static final CustomPayload.Id<OpenExtendedInventoryPayload> ID =
            new CustomPayload.Id<>(LucraftCore.id("open_extended_inventory"));

    public static final PacketCodec<RegistryByteBuf, OpenExtendedInventoryPayload> CODEC =
            PacketCodec.unit(INSTANCE);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
