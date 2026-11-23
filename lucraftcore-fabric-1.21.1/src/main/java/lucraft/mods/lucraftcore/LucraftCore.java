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

public class LucraftCore implements ModInitializer {

    public static final String MODID = "lucraftcore";
    public static final String NAME = "Lucraft: Core";
    public static final String VERSION = "1.21.1-2.4.17";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
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
