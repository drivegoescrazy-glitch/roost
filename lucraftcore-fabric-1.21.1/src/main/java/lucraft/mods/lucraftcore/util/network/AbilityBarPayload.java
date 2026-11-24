package lucraft.mods.lucraftcore.util.network;

import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.List;

/** S2C sync of the ability bar: ids, active state and cooldowns. */
public record AbilityBarPayload(NbtCompound data) implements CustomPayload {

    public static final CustomPayload.Id<AbilityBarPayload> ID = new CustomPayload.Id<>(LucraftCore.id("ability_bar"));

    public static final PacketCodec<RegistryByteBuf, AbilityBarPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND, AbilityBarPayload::data,
            AbilityBarPayload::new);

    public record Entry(String id, boolean enabled, int cooldown, int maxCooldown, boolean held) {
    }

    public static AbilityBarPayload of(List<Entry> entries) {
        NbtList list = new NbtList();
        for (Entry entry : entries) {
            NbtCompound compound = new NbtCompound();
            compound.putString("id", entry.id());
            compound.putBoolean("enabled", entry.enabled());
            compound.putInt("cooldown", entry.cooldown());
            compound.putInt("max", entry.maxCooldown());
            compound.putBoolean("held", entry.held());
            list.add(compound);
        }
        NbtCompound root = new NbtCompound();
        root.put("entries", list);
        return new AbilityBarPayload(root);
    }

    public List<Entry> entries() {
        List<Entry> entries = new ArrayList<>();
        NbtList list = data.getList("entries", 10);
        for (int index = 0; index < list.size(); index++) {
            NbtCompound compound = list.getCompound(index);
            entries.add(new Entry(compound.getString("id"), compound.getBoolean("enabled"),
                    compound.getInt("cooldown"), compound.getInt("max"), compound.getBoolean("held")));
        }
        return entries;
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
