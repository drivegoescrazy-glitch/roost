package anvil.infinity.capabilities;

import anvil.infinity.snap.SnapResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Replaces the original {@code ICapabilityPlayerData}: the selected snap result
 * plus the last recorded stone position (kept for compatibility with the
 * original data layout).
 */
public record SnapPlayerData(int xPos, int yPos, int zPos, int dim, SnapResult snapResult) {

    public static final Codec<SnapPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("xPos", 0).forGetter(SnapPlayerData::xPos),
            Codec.INT.optionalFieldOf("yPos", 0).forGetter(SnapPlayerData::yPos),
            Codec.INT.optionalFieldOf("zPos", 0).forGetter(SnapPlayerData::zPos),
            Codec.INT.optionalFieldOf("dim", 0).forGetter(SnapPlayerData::dim),
            SnapResult.CODEC.optionalFieldOf("snapResult", SnapResult.KILLHALF)
                    .forGetter(SnapPlayerData::snapResult)
    ).apply(instance, SnapPlayerData::new));

    public SnapPlayerData() {
        this(0, 0, 0, 0, SnapResult.KILLHALF);
    }

    public SnapPlayerData withSnapResult(SnapResult result) {
        return new SnapPlayerData(xPos, yPos, zPos, dim, result);
    }

    public SnapPlayerData withPosition(int x, int y, int z, int dimension) {
        return new SnapPlayerData(x, y, z, dimension, snapResult);
    }
}
