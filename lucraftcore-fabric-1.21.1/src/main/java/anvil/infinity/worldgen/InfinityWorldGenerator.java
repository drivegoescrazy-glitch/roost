package anvil.infinity.worldgen;

import anvil.infinity.config.InfinityConfig;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Generates one temple per stone and world, far from spawn, with the original
 * 1-in-1000 per chunk chance in the configured dimensions.
 */
public final class InfinityWorldGenerator {

    /**
     * Chunks that were generated far enough from spawn and are waiting for their
     * temple roll. 1.21 finishes chunk generation on worker threads and forbids
     * reading neighbouring chunks from there, so the roll and the placement are
     * done at the end of a world tick, once the chunk is a normal loaded chunk.
     */
    private static final Map<RegistryKey<World>, Queue<ChunkPos>> PENDING = new ConcurrentHashMap<>();

    private InfinityWorldGenerator() {
    }

    public static void register() {
        ServerChunkEvents.CHUNK_GENERATE.register((world, chunk) -> {
            if (!InfinityConfig.worldgenEnabled)
                return;
            lucraft.mods.lucraftcore.LucraftCore.guard("queueing infinity stone worldgen", () -> {
                ChunkPos pos = chunk.getPos();
                // Original condition: both coordinates outside +-1000.
                if (pos.x <= 1000 && pos.x >= -1000 || pos.z <= 1000 && pos.z >= -1000)
                    return;
                PENDING.computeIfAbsent(world.getRegistryKey(), key -> new ConcurrentLinkedQueue<>()).add(pos);
            });
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PENDING.clear());

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            Queue<ChunkPos> queue = PENDING.get(world.getRegistryKey());
            if (queue == null || queue.isEmpty())
                return;
            lucraft.mods.lucraftcore.LucraftCore.guard("infinity stone worldgen", () -> {
                int budget = 32;
                ChunkPos pos;
                while (budget-- > 0 && (pos = queue.poll()) != null)
                    generate(world, pos.x, pos.z);
            });
        });
    }

    /** Mirrors {@code IWorldGenerator#generate}; kept public for tests. */
    public static void generate(ServerWorld world, int chunkX, int chunkZ) {
        if (!InfinityConfig.worldgenEnabled || !world.getChunkManager().isChunkLoaded(chunkX, chunkZ))
            return;

        if (InfinityConfig.worldgenDebug)
            System.out.println("Chunk generation in Dim: " + world.getRegistryKey().getValue());

        if (chunkX <= 1000 && chunkX >= -1000 || chunkZ <= 1000 && chunkZ >= -1000)
            return;

        int blockX = chunkX * 16 + 8;
        int blockZ = chunkZ * 16 + 8;
        if (!canGenMind(world) && !canGenPower(world) && !canGenReality(world)
                && !canGenSoul(world) && !canGenSpace(world) && !canGenTime(world))
            return;

        if (InfinityConfig.worldgenDebug)
            System.out.println("A Stone can be generated here");

        if (world.getRandom().nextInt(1000) != 0)
            return;

        int y = getGroundFromAbove(world, blockX, blockZ);
        StoneTempleGen.generate(world, new BlockPos(blockX, y, blockZ));
    }

    /**
     * First solid/water block from above, used as the temple's floor. The
     * original scanned down to y=31 because 1.12 worlds ended there; the scan
     * now stops at the bottom of the current world.
     */
    public static int getGroundFromAbove(ServerWorld world, int x, int z) {
        int y = world.getTopY();
        int bottom = world.getBottomY() + 1;
        boolean found = false;
        while (!found && y-- >= bottom) {
            var block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            found = block == Blocks.WATER || block == Blocks.GRASS_BLOCK || block == Blocks.SAND
                    || block == Blocks.GRAVEL || block == Blocks.STONE || block == Blocks.DIRT
                    || block == Blocks.CLAY || block == Blocks.PODZOL;
        }
        return y;
    }

    public static boolean isCornerValid(ServerWorld world, BlockPos pos) {
        int variation = 3;
        int highest = getGroundFromAbove(world, pos.getX(), pos.getZ());
        return highest > pos.getY() - variation && highest < pos.getY() + variation;
    }

    public static boolean canGenPower(ServerWorld world) {
        return !WorldData.get(world).power && dimensionMatches(world, EnumInfinityStone.POWER);
    }

    public static boolean canGenSoul(ServerWorld world) {
        return !WorldData.get(world).soul && dimensionMatches(world, EnumInfinityStone.SOUL);
    }

    public static boolean canGenSpace(ServerWorld world) {
        return !WorldData.get(world).space && dimensionMatches(world, EnumInfinityStone.SPACE);
    }

    public static boolean canGenReality(ServerWorld world) {
        return !WorldData.get(world).reality && dimensionMatches(world, EnumInfinityStone.REALITY);
    }

    public static boolean canGenTime(ServerWorld world) {
        return !WorldData.get(world).time && dimensionMatches(world, EnumInfinityStone.TIME);
    }

    public static boolean canGenMind(ServerWorld world) {
        return !WorldData.get(world).mind && dimensionMatches(world, EnumInfinityStone.MIND);
    }

    /**
     * The original compared numeric dimension ids. The config accepts the old
     * numbers (-1 nether, 0 overworld, 1 end) as well as a dimension id such as
     * {@code minecraft:overworld}, which is what modded dimensions use now.
     */
    public static boolean dimensionMatches(ServerWorld world, EnumInfinityStone stone) {
        String configured = InfinityConfig.dimension(stone).trim();
        Identifier current = world.getRegistryKey().getValue();
        try {
            int legacy = Integer.parseInt(configured);
            return switch (legacy) {
                case -1 -> current.equals(Identifier.ofVanilla("the_nether"));
                case 0 -> current.equals(Identifier.ofVanilla("overworld"));
                case 1 -> current.equals(Identifier.ofVanilla("the_end"));
                default -> false;
            };
        } catch (NumberFormatException ignored) {
            Identifier wanted = Identifier.tryParse(configured);
            return wanted != null && wanted.equals(current);
        }
    }
}
