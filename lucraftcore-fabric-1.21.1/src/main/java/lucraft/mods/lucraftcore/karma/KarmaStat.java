package lucraft.mods.lucraftcore.karma;

import java.util.ArrayList;
import java.util.List;

public class KarmaStat {

    public static final List<KarmaStat> ALL = new ArrayList<>();

    public static final KarmaStat MONSTER_KILL = new KarmaStat("monster_kill", 2);
    public static final KarmaStat GOOD_PLAYER_KILL = new KarmaStat("good_player_kill", -3);
    public static final KarmaStat BAD_PLAYER_KILL = new KarmaStat("bad_player_kill", 2);
    public static final KarmaStat GOOD_KNOCKOUT = new KarmaStat("good_knockout", -1);
    public static final KarmaStat BAD_KNOCKOUT = new KarmaStat("bad_knockout", 3);
    public static final KarmaStat TAMING = new KarmaStat("taming", 1);
    public static final KarmaStat SAVES = new KarmaStat("saves", 3);
    public static final KarmaStat THEFTS = new KarmaStat("thefts", -2);
    public static final KarmaStat MODIFIED = new KarmaStat("modified", 1);

    private final String name;
    private final int amount;

    private KarmaStat(String name, int amount) {
        this.name = name;
        this.amount = amount;
        ALL.add(this);
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getTranslationKey() {
        return "karma.karmastats." + name;
    }

    public enum KarmaClass {
        TYRANT("tyrant", Integer.MIN_VALUE, -501),
        SUPERVILLAIN("superVillain", -500, -201),
        VILLAIN("villain", -200, -101),
        THUG("thug", -100, -26),
        NEUTRAL("neutral", -25, 25),
        VIGILANTE("vigilante", 26, 100),
        HERO("hero", 101, 200),
        SUPERHERO("superHero", 201, 500),
        LEGEND("legend", 501, Integer.MAX_VALUE);

        private final String name;
        private final int min;
        private final int max;

        KarmaClass(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
        }

        public String getName() {
            return name;
        }

        public String getTranslationKey() {
            return "karma.karmaclass." + name;
        }

        public int getMinKarma() {
            return min;
        }

        public int getMaxKarma() {
            return max;
        }

        public boolean contains(int karma) {
            return karma >= min && karma <= max;
        }
    }
}
