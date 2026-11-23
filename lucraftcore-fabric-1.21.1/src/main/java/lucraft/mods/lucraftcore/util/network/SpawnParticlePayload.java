package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/** Asks the client to spawn particles at a position. */
public record SpawnParticlePayload(Identifier particle, NbtCompound data) implements CustomPayload {

    public static final CustomPayload.Id<SpawnParticlePayload> ID = new CustomPayload.Id<>(LucraftCore.id("spawn_particle"));

    public static final PacketCodec<RegistryByteBuf, SpawnParticlePayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, SpawnParticlePayload::particle,
            PacketCodecs.NBT_COMPOUND, SpawnParticlePayload::data,
            SpawnParticlePayload::new);

    public static SpawnParticlePayload create(Identifier particle, Vec3d position, Vec3d velocity, int count) {
        NbtCompound data = new NbtCompound();
        data.putDouble("x", position.x);
        data.putDouble("y", position.y);
        data.putDouble("z", position.z);
        data.putDouble("vx", velocity.x);
        data.putDouble("vy", velocity.y);
        data.putDouble("vz", velocity.z);
        data.putInt("count", Math.max(1, count));
        return new SpawnParticlePayload(particle, data);
    }

    public Vec3d position() {
        return new Vec3d(data.getDouble("x"), data.getDouble("y"), data.getDouble("z"));
    }

    public Vec3d velocity() {
        return new Vec3d(data.getDouble("vx"), data.getDouble("vy"), data.getDouble("vz"));
    }

    public int count() {
        return Math.max(1, data.getInt("count"));
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
