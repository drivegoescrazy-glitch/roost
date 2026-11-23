package lucraft.mods.lucraftcore.superpowers;

import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConfig;
import lucraft.mods.lucraftcore.superpowers.effects.Effect;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/** A superpower, as defined by an addon pack: abilities, levels and XP thresholds. */
public final class Superpower {

    private final Identifier id;
    private final Text name;
    private final Text description;
    private final int color;
    private final int[] levelXp;
    private final List<AbilityConfig> abilities;
    private final List<Effect> effects;

    public Superpower(Identifier id, Text name, Text description, int color, int[] levelXp,
                      List<AbilityConfig> abilities, List<Effect> effects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.levelXp = levelXp == null || levelXp.length == 0 ? new int[]{0} : levelXp;
        this.abilities = abilities == null ? List.of() : List.copyOf(abilities);
        this.effects = effects == null ? List.of() : List.copyOf(effects);
    }

    public Identifier getId() {
        return id;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public int getMaxLevel() {
        return levelXp.length;
    }

    public int[] getLevelXp() {
        return levelXp.clone();
    }

    public List<AbilityConfig> getAbilities() {
        return abilities;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    /** XP needed to reach the given level (levels start at 1). */
    public int getXpForLevel(int level) {
        int index = Math.max(1, Math.min(level, getMaxLevel())) - 1;
        return levelXp[index];
    }

    public int getLevelForXp(int xp) {
        int level = 1;
        for (int index = 1; index < levelXp.length; index++)
            if (xp >= levelXp[index])
                level = index + 1;
        return level;
    }

    /** Abilities unlocked by the given level. */
    public List<AbilityConfig> getAbilitiesForLevel(int level) {
        List<AbilityConfig> unlocked = new ArrayList<>();
        for (AbilityConfig config : abilities)
            if (config.level() <= level)
                unlocked.add(config);
        return unlocked;
    }
}
