package lucraft.mods.lucraftcore.superpowers;

import lucraft.mods.lucraftcore.superpowers.abilities.AbilityCondition;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.List;

public abstract class Ability {

    protected final ServerPlayerEntity player;
    protected boolean enabled;
    protected boolean granted;
    protected int cooldown;
    protected int maxCooldown;
    protected int ticks;
    protected int energyCost;
    protected List<AbilityCondition> conditions = Collections.emptyList();

    protected Ability(ServerPlayerEntity player) {
        this.player = player;
    }

    public abstract String getId();

    public AbilityType getType() {
        return AbilityType.CONSTANT;
    }

    /**
     * Whether the ability belongs in the ability bar. Constant abilities are
     * passive (strength, resistance, healing, ...) and the 1.12 bar hid them.
     */
    public boolean showInAbilityBar() {
        return getType() != AbilityType.CONSTANT;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isGranted() {
        return granted;
    }

    /** Applies addon-pack configuration: energy cost, cooldown, conditions. */
    public void configure(int energyCost, int cooldown, List<AbilityCondition> conditions) {
        this.energyCost = Math.max(0, energyCost);
        if (cooldown > 0)
            this.maxCooldown = cooldown;
        this.conditions = conditions == null ? Collections.emptyList() : List.copyOf(conditions);
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public List<AbilityCondition> getConditions() {
        return conditions;
    }

    public boolean areConditionsMet() {
        for (AbilityCondition condition : conditions)
            if (!condition.test(player))
                return false;
        return true;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;
        this.enabled = enabled;
        if (enabled)
            onEnabled();
        else
            onDisabled();
    }

    public void onGrant() {
        granted = true;
        if (getType() == AbilityType.CONSTANT && areConditionsMet())
            setEnabled(true);
    }

    public void onRevoke() {
        granted = false;
        boolean was = enabled;
        enabled = false;
        if (was)
            onDisabled();
        cleanup();
    }

    /** Called once per server tick while the ability is granted. */
    public void tick() {
        ticks++;
        boolean met = areConditionsMet();
        if (enabled && met)
            onTick();
        if (getType() == AbilityType.CONSTANT) {
            if (granted && met && !enabled)
                setEnabled(true);
            else if (enabled && !met)
                setEnabled(false);
        }
        if (cooldown > 0)
            cooldown--;
    }

    protected void onTick() {
    }

    protected void onEnabled() {
    }

    protected void onDisabled() {
    }

    protected void cleanup() {
    }

    public boolean use() {
        if (!areConditionsMet())
            return false;
        if (getType() == AbilityType.CONSTANT || getType() == AbilityType.TOGGLE) {
            if (!enabled && !payEnergy())
                return false;
            setEnabled(!enabled);
            return true;
        }
        if (cooldown > 0)
            return false;
        if (onUse()) {
            cooldown = maxCooldown;
            return true;
        }
        return false;
    }

    /** Action abilities call this from {@link #onUse()} before acting. */
    protected boolean payEnergy() {
        return AbilityEnergy.consume(player, energyCost);
    }

    protected boolean onUse() {
        return false;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMaxCooldown() {
        return maxCooldown;
    }

    protected void setMaxCooldown(int ticks) {
        this.maxCooldown = ticks;
    }
}
