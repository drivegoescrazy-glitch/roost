package lucraft.mods.lucraftcore.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.addonpacks.AddonPackHandler;
import lucraft.mods.lucraftcore.addonpacks.AddonPackInfo;
import lucraft.mods.lucraftcore.util.energy.EnergyItemStorage;
import lucraft.mods.lucraftcore.util.energy.EnergyStorage;
import lucraft.mods.lucraftcore.util.energy.EnergyUtil;
import lucraft.mods.lucraftcore.util.fluids.FluidTank;
import lucraft.mods.lucraftcore.util.inventory.InventoryUtil;
import lucraft.mods.lucraftcore.extendedinventory.ExtendedInventoryData;
import lucraft.mods.lucraftcore.extendedinventory.ExtendedInventoryHandler;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import lucraft.mods.lucraftcore.infinity.blocks.InfinityGeneratorBlockEntity;
import lucraft.mods.lucraftcore.infinity.items.InventoryInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import lucraft.mods.lucraftcore.utilities.blocks.FuelValues;
import lucraft.mods.lucraftcore.utilities.blocks.FurnaceGeneratorBlockEntity;
import lucraft.mods.lucraftcore.utilities.blocks.UtilitiesBlocks;
import lucraft.mods.lucraftcore.utilities.recipes.MachineRecipes;
import lucraft.mods.lucraftcore.util.inventory.StackInventory;
import com.google.gson.JsonObject;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Operator commands for the engine layer: a self test of the energy, fluid and
 * inventory APIs, and a listing of loaded addon packs.
 */
public final class CommandLucraftCore {

    private CommandLucraftCore() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("lucraftcore")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("engine").executes(CommandLucraftCore::runEngineTest))
                        .then(CommandManager.literal("addonpacks").executes(CommandLucraftCore::listAddonPacks))
                        .then(CommandManager.literal("machines").executes(CommandLucraftCore::runMachineTest))
                        .then(CommandManager.literal("infinity").executes(CommandLucraftCore::runInfinityTest))
                        .then(CommandManager.literal("extended").executes(CommandLucraftCore::runExtendedTest))));
    }

    private static int runMachineTest(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean ok = true;

        int coal = FuelValues.burnTime(new ItemStack(Items.COAL));
        boolean fuelOk = coal == 1600 && FuelValues.burnTime(new ItemStack(Items.STICK)) == 100
                && FuelValues.burnTime(ItemStack.EMPTY) == 0;
        ok &= fuelOk;
        report(source, "fuel", fuelOk, "coal=" + coal);

        boolean registered = true;
        for (String id : List.of("construction_table", "suit_stand", "extractor", "boiler", "furnace_generator"))
            registered &= Registries.BLOCK_ENTITY_TYPE.get(LucraftCore.id(id)) != null;
        ok &= registered;
        report(source, "machine_blocks", registered, "5 block entity types");

        JsonObject json = new JsonObject();
        json.addProperty("input", "minecraft:iron_ore");
        json.addProperty("count", 1);
        JsonObject output = new JsonObject();
        output.addProperty("item", "minecraft:iron_ingot");
        output.addProperty("count", 2);
        json.add("output", output);
        json.addProperty("energy", 400);
        json.addProperty("time", 40);
        boolean added = MachineRecipes.add("extractor", LucraftCore.id("test/extractor"), json);
        var recipe = MachineRecipes.findExtractor(new ItemStack(Items.IRON_ORE));
        boolean recipeOk = added && recipe != null && recipe.output().isOf(Items.IRON_INGOT)
                && recipe.output().getCount() == 2 && recipe.energyPerTick() == 10;
        ok &= recipeOk;
        report(source, "extractor_recipe", recipeOk,
                "loaded=" + MachineRecipes.extractorRecipes().size() + ", found=" + (recipe != null));

        FurnaceGeneratorBlockEntity generator = new FurnaceGeneratorBlockEntity(BlockPos.ORIGIN,
                UtilitiesBlocks.FURNACE_GENERATOR.getDefaultState());
        int accepted = generator.getEnergy().receiveEnergy(500, false);
        boolean energyOk = accepted == 500 && generator.getEnergy().getEnergyStored() == 500;
        ok &= energyOk;
        report(source, "machine_energy", energyOk, "accepted=" + accepted);

        final boolean result = ok;
        source.sendFeedback(() -> Text.literal("[Lucraft] machines " + (result ? "PASS" : "FAIL")
                + " | extractor recipes=" + MachineRecipes.extractorRecipes().size()
                + ", boiler recipes=" + MachineRecipes.boilerRecipes().size()), false);
        return result ? 1 : 0;
    }

    private static int runInfinityTest(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        try {
            return runInfinityTestInternal(source);
        } catch (Throwable throwable) {
            LucraftCore.LOGGER.error("Infinity self test failed", throwable);
            source.sendError(Text.literal("Infinity self test failed: " + throwable));
            return 0;
        }
    }

    private static int runInfinityTestInternal(ServerCommandSource source) {
        boolean ok = true;

        ItemStack gauntlet = new ItemStack(ModuleInfinity.INFINITY_GAUNTLET);
        gauntlet.set(ItemInfinityGauntlet.STONES, List.of(new ItemStack(Items.DIAMOND)));
        boolean componentOk = ItemInfinityGauntlet.getStones(gauntlet).size() == 1
                && !ItemInfinityGauntlet.hasStone(gauntlet, EnumInfinityStone.SPACE);
        boolean rejectsNonStone = !new InventoryInfinityGauntlet(gauntlet).isValid(0, new ItemStack(Items.DIAMOND));
        boolean gauntletOk = componentOk && rejectsNonStone;
        ok &= gauntletOk;
        report(source, "gauntlet", gauntletOk,
                "stones=" + ItemInfinityGauntlet.getStones(gauntlet).size() + ", nonStoneRejected=" + rejectsNonStone);

        InfinityGeneratorBlockEntity generator = new InfinityGeneratorBlockEntity(BlockPos.ORIGIN,
                ModuleInfinity.INFINITY_GENERATOR.getDefaultState());
        int accepted = generator.getEnergy().receiveEnergy(1234, false);
        boolean energyOk = accepted == 1234 && generator.getEnergy().getEnergyStored() == 1234;
        ok &= energyOk;
        report(source, "infinity_generator", energyOk, "accepted=" + accepted);

        boolean registered = Registries.BLOCK_ENTITY_TYPE.get(LucraftCore.id("infinity_generator")) != null
                && Registries.ITEM.get(LucraftCore.id("infinity_gauntlet")) == ModuleInfinity.INFINITY_GAUNTLET;
        ok &= registered;
        report(source, "infinity_registry", registered, "gauntlet + generator registered");

        final boolean result = ok;
        source.sendFeedback(() -> Text.literal("[Lucraft] infinity " + (result ? "PASS" : "FAIL")), false);
        return result ? 1 : 0;
    }

    private static int runExtendedTest(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This test needs a player"));
            return 0;
        }
        ExtendedInventoryData previous = ExtendedInventoryHandler.get(player);
        boolean ok = true;

        ExtendedInventoryHandler.setStack(player, ExtendedInventoryData.SLOT_NECKLACE,
                new ItemStack(Items.DIAMOND));
        boolean stored = ExtendedInventoryHandler.getStack(player, ExtendedInventoryData.SLOT_NECKLACE)
                .isOf(Items.DIAMOND);
        ok &= stored;
        report(source, "extended_storage", stored, "necklace slot persisted");

        SimpleInventory inventory = ExtendedInventoryHandler.createInventory(player);
        boolean screenOk = inventory.size() == 9
                && inventory.isValid(ExtendedInventoryData.SLOT_NECKLACE, new ItemStack(Items.DIAMOND))
                && !inventory.isValid(6, new ItemStack(Items.DIAMOND));
        ok &= screenOk;
        report(source, "extended_screen", screenOk, "size=" + inventory.size() + ", extra slots locked");

        ExtendedInventoryHandler.setStack(player, ExtendedInventoryData.SLOT_NECKLACE,
                previous.get(ExtendedInventoryData.SLOT_NECKLACE));
        final boolean result = ok;
        source.sendFeedback(() -> Text.literal("[Lucraft] extended inventory " + (result ? "PASS" : "FAIL")), false);
        return result ? 1 : 0;
    }

    private static int runEngineTest(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean ok = true;

        EnergyStorage first = new EnergyStorage(1000, 1000);
        EnergyStorage second = new EnergyStorage(1000, 1000);
        int received = first.receiveEnergy(600, false);
        int moved = EnergyUtil.transfer(first, second, 400);
        int drained = second.extractEnergy(150, false);
        boolean energyOk = received == 600 && moved == 400 && drained == 150
                && first.getEnergyStored() == 200 && second.getEnergyStored() == 250;
        ok &= energyOk;
        report(source, "energy", energyOk,
                "receive=" + received + ", transfer=" + moved + ", drain=" + drained
                        + ", stored=" + first.getEnergyStored() + "/" + second.getEnergyStored());

        ItemStack battery = new ItemStack(Items.DIAMOND_SWORD);
        EnergyItemStorage.setStored(battery, 0);
        EnergyItemStorage itemStorage = new EnergyItemStorage(battery, 5000);
        int charged = itemStorage.receiveEnergy(1200, false);
        boolean itemEnergyOk = charged == 1200 && EnergyItemStorage.getStored(battery) == 1200
                && EnergyItemStorage.isEnergyItem(battery);
        ok &= itemEnergyOk;
        report(source, "item_energy", itemEnergyOk,
                "charged=" + charged + ", stored=" + EnergyItemStorage.getStored(battery) + "/5000");

        FluidTank tank = new FluidTank(8000);
        int filled = tank.fill(Fluids.WATER, 4000, false);
        int drainedFluid = tank.drain(1000, false);
        boolean fluidOk = filled == 4000 && drainedFluid == 1000 && tank.getAmount() == 3000
                && tank.contains(Fluids.WATER);
        ok &= fluidOk;
        report(source, "fluid", fluidOk, "filled=" + filled + ", drained=" + drainedFluid
                + ", remaining=" + tank.getAmount() + "/" + tank.getCapacity());

        StackInventory inventory = new StackInventory(3);
        ItemStack remainder = InventoryUtil.insert(inventory, new ItemStack(Items.DIRT, 100), false);
        ItemStack extracted = InventoryUtil.extract(inventory, stack -> stack.isOf(Items.DIRT), 10, false);
        int left = InventoryUtil.count(inventory, new ItemStack(Items.DIRT));
        boolean inventoryOk = remainder.isEmpty() && extracted.getCount() == 10 && left == 90;
        ok &= inventoryOk;
        report(source, "inventory", inventoryOk, "remainder=" + remainder.getCount()
                + ", extracted=" + extracted.getCount() + ", left=" + left);

        boolean passed = ok;
        source.sendFeedback(() -> Text.literal("[Lucraft] engine self test " + (passed ? "PASSED" : "FAILED")), false);
        if (!passed)
            LucraftCore.LOGGER.warn("Engine self test failed");
        return ok ? 1 : 0;
    }

    private static int listAddonPacks(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        List<AddonPackInfo> packs = AddonPackHandler.getPacks();
        source.sendFeedback(() -> Text.literal("[Lucraft] " + packs.size() + " addon pack(s), "
                + AddonPackHandler.getInstructionRecipeCount() + " instruction recipe(s), "
                + AddonPackHandler.getSuitSetCount() + " suit set(s), "
                + AddonPackHandler.getSuperpowerCount() + " superpower(s)"), false);
        for (AddonPackInfo pack : packs)
            source.sendFeedback(() -> Text.literal(" - " + pack.getName() + " (" + pack.getFileName() + ") by " + pack.getAuthor()), false);
        return packs.size();
    }

    private static void report(ServerCommandSource source, String name, boolean passed, String detail) {
        String line = "[Lucraft] " + name + ": " + (passed ? "ok" : "FAILED") + " (" + detail + ")";
        source.sendFeedback(() -> Text.literal(line), false);
    }
}
