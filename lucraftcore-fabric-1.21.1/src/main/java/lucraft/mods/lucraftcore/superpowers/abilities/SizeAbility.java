package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.sizechanging.SizeChanging;
import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

public class SizeAbility extends Ability {

    private final float size;
    private final float defaultSize;

    public SizeAbility(ServerPlayerEntity player, float size, float defaultSize) {
        super(player);
        this.size = size;
        this.defaultSize = defaultSize;
    }

    @Override
    public String getId() {
        return "size_change";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.TOGGLE;
    }

    @Override
    protected void onEnabled() {
        SizeChanging.setSize(player, size);
    }

    @Override
    protected void onDisabled() {
        SizeChanging.setSize(player, defaultSize);
    }

    @Override
    protected void cleanup() {
        SizeChanging.setSize(player, defaultSize);
    }
}
