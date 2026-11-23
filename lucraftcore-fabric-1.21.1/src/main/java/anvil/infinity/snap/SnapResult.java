package anvil.infinity.snap;

import anvil.infinity.config.InfinityConfig;
import com.mojang.serialization.Codec;

/** The selectable snap results, in the original cycle order. */
public enum SnapResult {

    KILLHALF("Kill half"),
    DESTROYSTONES("Destroy the Stones"),
    BRINGBACK("Bring back the dusted (WIP)"),
    RECREATE("Recreate the Universe (WIP)"),
    KILLEVIL("Kill evil entities"),
    CREATIVE("Change to Creative");

    public static final Codec<SnapResult> CODEC = Codec.STRING.xmap(SnapResult::fromString, SnapResult::toString);

    private final String id;

    SnapResult(String id) {
        this.id = id;
    }

    /** Next result in the cycle; "Change to Creative" only cycles in when enabled. */
    public SnapResult next() {
        return switch (this) {
            case KILLHALF -> DESTROYSTONES;
            case DESTROYSTONES -> BRINGBACK;
            case BRINGBACK -> RECREATE;
            case RECREATE -> KILLEVIL;
            case KILLEVIL -> InfinityConfig.snapCreative ? CREATIVE : KILLHALF;
            case CREATIVE -> KILLHALF;
        };
    }

    @Override
    public String toString() {
        return id;
    }

    public static SnapResult fromString(String value) {
        if (value == null)
            return KILLHALF;
        for (SnapResult result : values())
            if (result.id.equals(value) || result.name().equals(value))
                return result;
        return KILLHALF;
    }
}
