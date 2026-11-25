package anvil.infinity;

import anvil.infinity.abilities.InfinityAbilities;
import anvil.infinity.capabilities.GauntletUserInformation;
import anvil.infinity.command.GeneratedStonesCommand;
import anvil.infinity.compat.CompatHandler;
import anvil.infinity.config.InfinityConfig;
import anvil.infinity.helpers.ImmortalityHandler;
import anvil.infinity.registry.InfinityEffects;
import anvil.infinity.items.Items;
import anvil.infinity.registry.InfinitySounds;
import anvil.infinity.worldgen.InfinityWorldGenerator;
import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Infinityraft: infinity stones, the snap and the stone temples.
 *
 * <p>Fabric 1.21.1 port of the original 1.12.2 Forge addon. The gauntlet,
 * generator and stone handling live in the ported Lucraft: Core; this mod adds
 * the six stones, their abilities, the snap, the temple worldgen and the
 * {@code /generatedStones} command.</p>
 */
public class Infinity implements ModInitializer {

    public static final String MOD_ID = "infinity";
    public static final String NAME = "Infinityraft";
    public static final String VERSION = "1.13.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** The original "Lucraft: Core | Infinity" creative tab. */
    public static ItemGroup TAB;

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Infinityraft (Fabric 1.21.1) loading");
        InfinityConfig.load();
        InfinitySounds.register();
        InfinityEffects.register();
        Items.register();
        InfinityAbilities.register();
        CompatHandler.check();
        ImmortalityHandler.register();
        registerTab();
        GeneratedStonesCommand.register();
        InfinityWorldGenerator.register();
        GauntletUserInformation.logRegistration();
        LOGGER.info("Infinityraft loaded: {} stones, {} abilities", Items.ALL.size(),
                InfinityAbilities.REGISTERED.size());
    }

    private void registerTab() {
        TAB = Registry.register(Registries.ITEM_GROUP, id("infinity"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.tabInfinity"))
                        .icon(() -> ModuleInfinity.INFINITY_GAUNTLET == null
                                ? ItemStack.EMPTY
                                : new ItemStack(ModuleInfinity.INFINITY_GAUNTLET))
                        .entries((context, entries) -> {
                            // Same order as the original tab: gauntlet, cast, generator, stones.
                            if (ModuleInfinity.INFINITY_GAUNTLET != null)
                                entries.add(ModuleInfinity.INFINITY_GAUNTLET);
                            if (ModuleInfinity.INFINITY_GAUNTLET_CAST != null)
                                entries.add(ModuleInfinity.INFINITY_GAUNTLET_CAST);
                            if (ModuleInfinity.INFINITY_GENERATOR != null)
                                entries.add(ModuleInfinity.INFINITY_GENERATOR);
                            for (var item : Items.ALL)
                                entries.add(item);
                        })
                        .build());
    }
}
