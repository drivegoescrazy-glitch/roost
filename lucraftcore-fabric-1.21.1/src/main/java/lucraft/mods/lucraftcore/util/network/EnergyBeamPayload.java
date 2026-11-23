package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

/**
 * Carries one energy blast shot to the clients that should see it: where the
 * bolt starts, where it ends and its colour. The original mod spawned an
 * {@code EntityEnergyBlast} for this, whose renderer drew a glowing line along
 * the bolt's motion; the port sends the same line to the client instead of
 * keeping a projectile entity in the world.
 */
public record EnergyBeamPayload(NbtCompound data) implements CustomPayload {

    public static final CustomPayload.Id<EnergyBeamPayload> ID =
            new CustomPayload.Id<>(LucraftCore.id("energy_beam"));

    public static final PacketCodec<RegistryByteBuf, EnergyBeamPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND, EnergyBeamPayload::data,
            EnergyBeamPayload::new);

    public static EnergyBeamPayload create(Vec3d from, Vec3d to, int color) {
        NbtCompound data = new NbtCompound();
        data.putDouble("fx", from.x);
        data.putDouble("fy", from.y);
        data.putDouble("fz", from.z);
        data.putDouble("tx", to.x);
        data.putDouble("ty", to.y);
        data.putDouble("tz", to.z);
        data.putInt("color", color);
        return new EnergyBeamPayload(data);
    }

    public Vec3d from() {
        return new Vec3d(data.getDouble("fx"), data.getDouble("fy"), data.getDouble("fz"));
    }

    public Vec3d to() {
        return new Vec3d(data.getDouble("tx"), data.getDouble("ty"), data.getDouble("tz"));
    }

    public int color() {
        return data.getInt("color");
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
