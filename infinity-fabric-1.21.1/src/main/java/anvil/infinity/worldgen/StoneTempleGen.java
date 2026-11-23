package anvil.infinity.worldgen;

import anvil.infinity.Infinity;
import anvil.infinity.items.Items;
import net.minecraft.block.Block;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

/** Places {@code infinity:stonetemple} and fills its chest with the stone. */
public final class StoneTempleGen {

    /** Chest slot the original generator inserted the stone into. */
    public static final int STONE_SLOT = 13;

    private StoneTempleGen() {
    }

    public static boolean generate(ServerWorld world, BlockPos position) {
        if (InfinityWorldGenerator.canGenMind(world))
            return spawn(world, position, Items.MIND_STONE, "Mind");
        if (InfinityWorldGenerator.canGenSpace(world))
            return spawn(world, position, Items.SPACE_STONE, "Space");
        if (InfinityWorldGenerator.canGenReality(world))
            return spawn(world, position, Items.REALITY_STONE, "Reality");
        if (InfinityWorldGenerator.canGenTime(world))
            return spawn(world, position, Items.TIME_STONE, "Time");
        if (InfinityWorldGenerator.canGenSoul(world))
            return spawn(world, position, Items.SOUL_STONE, "Soul");
        if (InfinityWorldGenerator.canGenPower(world))
            return spawn(world, position, Items.POWER_STONE, "Power");
        return false;
    }

    private static boolean spawn(ServerWorld world, BlockPos position, Item stone, String name) {
        Optional<StructureTemplate> optional = world.getStructureTemplateManager()
                .getTemplate(Infinity.id("stonetemple"));
        if (optional.isEmpty()) {
            Infinity.LOGGER.error("Missing structure infinity:stonetemple");
            return false;
        }

        StructureTemplate template = optional.get();
        BlockPos origin = position.up();
        StructurePlacementData placement = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.NONE)
                .setIgnoreEntities(false);
        template.place(world, origin, origin, placement, world.getRandom(), Block.NOTIFY_ALL);

        // The original put the stone in the chest marked by the structure's data
        // block; the port puts it in the temple's only chest.
        BlockPos min = origin;
        BlockPos max = origin.add(template.getSize().getX() - 1, template.getSize().getY() - 1,
                template.getSize().getZ() - 1);
        for (BlockPos pos : BlockPos.iterate(min, max)) {
            if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                chest.setStack(STONE_SLOT, new ItemStack(stone));
                chest.markDirty();
                break;
            }
        }

        WorldData data = WorldData.get(world);
        if (stone == Items.MIND_STONE) {
            data.mind = true;
            data.mindx = position.getX();
            data.mindz = position.getZ();
        } else if (stone == Items.SPACE_STONE) {
            data.space = true;
            data.spacex = position.getX();
            data.spacez = position.getZ();
        } else if (stone == Items.REALITY_STONE) {
            data.reality = true;
            data.realityx = position.getX();
            data.realityz = position.getZ();
        } else if (stone == Items.TIME_STONE) {
            data.time = true;
            data.timex = position.getX();
            data.timez = position.getZ();
        } else if (stone == Items.SOUL_STONE) {
            data.soul = true;
            data.soulx = position.getX();
            data.soulz = position.getZ();
        } else if (stone == Items.POWER_STONE) {
            data.power = true;
            data.powerx = position.getX();
            data.powerz = position.getZ();
        }
        data.markDirty();

        sendMessage(world, name);
        return true;
    }

    private static void sendMessage(ServerWorld world, String stone) {
        MutableText message = Text.literal("The " + stone + " Stone has been generated")
                .formatted(Formatting.GOLD, Formatting.BOLD);
        world.getServer().getPlayerManager().broadcast(message, false);
    }
}
