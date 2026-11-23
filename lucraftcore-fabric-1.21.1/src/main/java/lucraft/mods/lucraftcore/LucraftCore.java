package lucraft.mods.lucraftcore;

import lucraft.mods.lucraftcore.addonpacks.AddonPackHandler;
import lucraft.mods.lucraftcore.commands.CommandLucraftCore;
import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import lucraft.mods.lucraftcore.extendedinventory.ExtendedInventoryHandler;
import lucraft.mods.lucraftcore.karma.ModuleKarma;
import lucraft.mods.lucraftcore.materials.ModuleMaterials;
import lucraft.mods.lucraftcore.sizechanging.SizeChanging;
import lucraft.mods.lucraftcore.superpowers.ModuleSuperpowers;
import lucraft.mods.lucraftcore.utilities.UtilitiesItems;
import lucraft.mods.lucraftcore.utilities.blocks.UtilitiesBlocks;
import lucraft.mods.lucraftcore.utilities.recipes.MachineRecipes;
import lucraft.mods.lucraftcore.util.LCComponents;
import lucraft.mods.lucraftcore.util.network.LCNetworking;
import lucraft.mods.lucraftcore.util.particles.LCParticles;
import lucraft.mods.lucraftcore.util.sounds.LCSounds;
import lucraft.mods.lucraftcore.util.triggers.LCriteria;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LucraftCore implements ModInitializer {

    public static final String MODID = "lucraftcore";
    public static final String NAME = "Lucraft: Core";
    public static final String VERSION = "1.21.1-2.4.17";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    private static final Map<String, Integer> GUARDED_ERRORS = new HashMap<>();

    /**
     * Runs a gameplay step so that a failure inside one ability, effect or handler
     * is reported in the log instead of killing the server thread (which drops the
     * player back to the main menu with "Connection lost").
     */
    public static void guard(String what, Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            reportError(what, e);
        }
    }

    /** Logs a failure once per place, so a broken frame cannot flood the log. */
    public static void reportError(String what, Throwable error) {
        int seen;
        synchronized (GUARDED_ERRORS) {
            seen = GUARDED_ERRORS.merge(what, 1, Integer::sum);
        }
        if (seen <= 5)
            LOGGER.error("Error in {} (seen {} time(s); the game keeps running)", what, seen, error);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Lucraft: Core (Fabric 1.21.1) loading");
        LCConfig.load();
        LCComponents.register();
        LCSounds.register();
        LCParticles.register();
        LCriteria.register();
        if (LCConfig.engine) {
            LCNetworking.registerPayloads();
            LCNetworking.registerServerReceivers();
            LCNetworking.logRegistration();
            CommandLucraftCore.register();
        }
        if (LCConfig.materials)
            ModuleMaterials.register();
        if (LCConfig.utilities) {
            UtilitiesItems.register();
            UtilitiesBlocks.register();
            ModuleInfinity.register();
            MachineRecipes.load();
        }
        if (LCConfig.karma)
            ModuleKarma.register();
        if (LCConfig.sizeChanging)
            SizeChanging.register();
        if (LCConfig.superpowers)
            ModuleSuperpowers.register();
            ExtendedInventoryHandler.register();
        AddonPackHandler.load();
    }
}
