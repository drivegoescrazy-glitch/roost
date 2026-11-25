package anvil.infinity.worldgen;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

/**
 * Per world record of which stones have already generated, replacing the
 * original {@code WorldSavedData} of the same name and NBT layout.
 */
public class WorldData extends PersistentState {

    public static final String NAME = "generated_stones";

    public static final PersistentState.Type<WorldData> TYPE = new PersistentState.Type<>(
            WorldData::new, WorldData::fromNbt, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    public boolean power;
    public boolean space;
    public boolean reality;
    public boolean soul;
    public boolean time;
    public boolean mind;

    public int powerx;
    public int spacex;
    public int realityx;
    public int soulx;
    public int timex;
    public int mindx;

    public int powerz;
    public int spacez;
    public int realityz;
    public int soulz;
    public int timez;
    public int mindz;

    public WorldData() {
    }

    public static WorldData get(ServerWorld world) {
        ServerWorld overworld = world.getServer().getOverworld();
        return overworld.getPersistentStateManager().getOrCreate(TYPE, NAME);
    }

    public static WorldData fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        WorldData data = new WorldData();
        data.power = nbt.getBoolean("power");
        data.space = nbt.getBoolean("space");
        data.reality = nbt.getBoolean("reality");
        data.soul = nbt.getBoolean("soul");
        data.time = nbt.getBoolean("time");
        data.mind = nbt.getBoolean("mind");
        data.powerx = nbt.getInt("powerX");
        data.spacex = nbt.getInt("spaceX");
        data.realityx = nbt.getInt("realityX");
        data.soulx = nbt.getInt("soulX");
        data.timex = nbt.getInt("timeX");
        data.mindx = nbt.getInt("mindX");
        data.powerz = nbt.getInt("powerZ");
        data.spacez = nbt.getInt("spaceZ");
        data.realityz = nbt.getInt("realityZ");
        data.soulz = nbt.getInt("soulZ");
        data.timez = nbt.getInt("timeZ");
        data.mindz = nbt.getInt("mindZ");
        return data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putBoolean("power", power);
        nbt.putBoolean("space", space);
        nbt.putBoolean("reality", reality);
        nbt.putBoolean("soul", soul);
        nbt.putBoolean("time", time);
        nbt.putBoolean("mind", mind);
        nbt.putInt("powerX", powerx);
        nbt.putInt("spaceX", spacex);
        nbt.putInt("realityX", realityx);
        nbt.putInt("soulX", soulx);
        nbt.putInt("timeX", timex);
        nbt.putInt("mindX", mindx);
        nbt.putInt("powerZ", powerz);
        nbt.putInt("spaceZ", spacez);
        nbt.putInt("realityZ", realityz);
        nbt.putInt("soulZ", soulz);
        nbt.putInt("timeZ", timez);
        nbt.putInt("mindZ", mindz);
        return nbt;
    }
}
