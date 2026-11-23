package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.superpowers.Ability;
import lucraft.mods.lucraftcore.superpowers.AbilityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * Runs commands when the ability is used, granted or revoked. Replaces the
 * original command abilities (use / on gain / on lose / loop).
 */
public class CommandRunnerAbility extends Ability {

    private final AbilityConfig config;

    public CommandRunnerAbility(ServerPlayerEntity player, AbilityConfig config) {
        super(player);
        this.config = config;
    }

    @Override
    public String getId() {
        return config.abilityId();
    }

    @Override
    public AbilityType getType() {
        return AbilityType.ACTION;
    }

    @Override
    protected boolean onUse() {
        return run(config.getStringList("commands", "command"));
    }

    @Override
    public void onGrant() {
        super.onGrant();
        run(config.getStringList("grant_commands"));
    }

    @Override
    public void onRevoke() {
        run(config.getStringList("revoke_commands"));
        super.onRevoke();
    }

    @Override
    protected void onTick() {
        List<String> loop = config.getStringList("loop_commands");
        if (loop.isEmpty())
            return;
        int interval = Math.max(1, config.getInt("interval", 20));
        if (ticks % interval == 0)
            run(loop);
    }

    private boolean run(List<String> commands) {
        if (commands.isEmpty())
            return false;
        MinecraftServer server = player.getServer();
        if (server == null)
            return false;
        ServerCommandSource source = server.getCommandSource().withEntity(player).withSilent()
                .withPosition(player.getPos());
        for (String command : commands)
            server.getCommandManager().executeWithPrefix(source, command);
        return true;
    }
}
