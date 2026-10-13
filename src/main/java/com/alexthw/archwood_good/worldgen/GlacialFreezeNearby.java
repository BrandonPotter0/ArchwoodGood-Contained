package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Gradually converts fluids in loaded Glacial Archwood Caves chunks.
 *
 * Scanning and conversion are deliberately separate: the scanner discovers
 * candidate positions in a rotating set of loaded chunks, then a small global
 * per-tick budget converts candidates over time to avoid large visual bursts.
 */
@EventBusSubscriber(modid = ArchwoodGood.MODID)
public class GlacialFreezeNearby {
    private static final int SCAN_INTERVAL_TICKS = 20;
    private static final int LOG_INTERVAL_TICKS = 200;
    private static final int CHUNKS_PER_PASS = 8;
    private static final int Y_LEVELS_PER_PASS = 16;

    // Keep the conversion rate low and steady; increase only if testing shows
    // the caves take unreasonably long to freeze.
    private static final int MAX_CONVERSIONS_PER_TICK = 2;
    private static final int MAX_PENDING_POSITIONS = 100_000;
    private static final ParticleColor ICE_WHITE = new ParticleColor(240, 248, 255);
    private static final ParticleColor ICE_BLUE = new ParticleColor(173, 216, 230);

    private static final Map<ServerLevel, LinkedHashMap<Long, ChunkPos>> LOADED_CHUNKS = new LinkedHashMap<>();
    private static final Map<ServerLevel, Integer> NEXT_CHUNK_INDEX = new LinkedHashMap<>();
    private static final Map<ServerLevel, Map<Long, Integer>> NEXT_Y_BY_CHUNK = new LinkedHashMap<>();
    private static final Map<ServerLevel, ScanStats> STATS = new LinkedHashMap<>();
    private static final Map<ServerLevel, ArrayDeque<BlockPos>> PENDING = new LinkedHashMap<>();
    private static final Map<ServerLevel, Set<Long>> PENDING_KEYS = new LinkedHashMap<>();
    private static boolean tickEventLogged = false;

    private static final class ScanStats {
        long chunksScanned;
        long positionsScanned;
        long biomeMatches;
        long waterFound;
        long lavaFound;
        long waterConverted;
        long lavaConverted;
        long magelightsFound;
        long magelightsTinted;
        long magelightsMissingTile;
        long pendingDropped;
        long missingChunks;
        long lastSummaryTick;
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        synchronized (LOADED_CHUNKS) {
            LOADED_CHUNKS.computeIfAbsent(level, ignored -> new LinkedHashMap<>())
                    .put(chunk.getPos().toLong(), chunk.getPos());
            NEXT_Y_BY_CHUNK.computeIfAbsent(level, ignored -> new LinkedHashMap<>())
                    .putIfAbsent(chunk.getPos().toLong(), level.getMinBuildHeight());
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        synchronized (LOADED_CHUNKS) {
            LinkedHashMap<Long, ChunkPos> chunks = LOADED_CHUNKS.get(level);
            if (chunks != null) {
                chunks.remove(chunk.getPos().toLong());
                if (chunks.isEmpty()) {
                    LOADED_CHUNKS.remove(level);
                    NEXT_CHUNK_INDEX.remove(level);
                    NEXT_Y_BY_CHUNK.remove(level);
                    STATS.remove(level);
                    PENDING.remove(level);
                    PENDING_KEYS.remove(level);
                }
            }
            Map<Long, Integer> cursors = NEXT_Y_BY_CHUNK.get(level);
            if (cursors != null) cursors.remove(chunk.getPos().toLong());
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (!tickEventLogged) {
            tickEventLogged = true;
            ArchwoodGood.LOGGER.info("[GlacialFreeze] Gradual scanner and limited conversion queue are active.");
        }

        ScanStats stats;
        synchronized (LOADED_CHUNKS) {
            stats = STATS.computeIfAbsent(level, ignored -> new ScanStats());
            PENDING.computeIfAbsent(level, ignored -> new ArrayDeque<>());
            PENDING_KEYS.computeIfAbsent(level, ignored -> new HashSet<>());
        }

        // Do a small amount of work every tick; conversions no longer all occur
        // during the chunk-scanning pass.
        convertPending(level, stats);

        if (level.getGameTime() % SCAN_INTERVAL_TICKS == 0L) {
            scanChunks(level, stats);
        }

        if (level.getGameTime() - stats.lastSummaryTick >= LOG_INTERVAL_TICKS) {
            stats.lastSummaryTick = level.getGameTime();
            int trackedChunkCount;
            int pendingCount;
            synchronized (LOADED_CHUNKS) {
                Map<Long, ChunkPos> chunks = LOADED_CHUNKS.get(level);
                trackedChunkCount = chunks == null ? 0 : chunks.size();
                ArrayDeque<BlockPos> pending = PENDING.get(level);
                pendingCount = pending == null ? 0 : pending.size();
            }
            ArchwoodGood.LOGGER.info(
                    "[GlacialFreeze] dimension={}, trackedChunks={}, chunksScannedTotal={}, positionsScannedTotal={}, biomeMatchesTotal={}, waterFoundTotal={}, lavaFoundTotal={}, waterConvertedTotal={}, lavaConvertedTotal={}, magelightsFoundTotal={}, magelightsTintedTotal={}, magelightsMissingTileTotal={}, pending={}, pendingDroppedTotal={}, missingChunksTotal={}",
                    level.dimension().location(), trackedChunkCount, stats.chunksScanned, stats.positionsScanned,
                    stats.biomeMatches, stats.waterFound, stats.lavaFound, stats.waterConverted, stats.lavaConverted,
                    stats.magelightsFound, stats.magelightsTinted, stats.magelightsMissingTile,
                    pendingCount, stats.pendingDropped, stats.missingChunks);
        }
    }

    private static void scanChunks(ServerLevel level, ScanStats stats) {
        ArrayList<ChunkPos> work = new ArrayList<>(CHUNKS_PER_PASS);
        synchronized (LOADED_CHUNKS) {
            LinkedHashMap<Long, ChunkPos> chunks = LOADED_CHUNKS.get(level);
            if (chunks == null || chunks.isEmpty()) return;

            ArrayList<ChunkPos> snapshot = new ArrayList<>(chunks.values());
            int start = Math.floorMod(NEXT_CHUNK_INDEX.getOrDefault(level, 0), snapshot.size());
            int count = Math.min(CHUNKS_PER_PASS, snapshot.size());
            for (int i = 0; i < count; i++) {
                work.add(snapshot.get((start + i) % snapshot.size()));
            }
            NEXT_CHUNK_INDEX.put(level, (start + count) % snapshot.size());
        }

        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        if (maxY <= minY) return;

        for (ChunkPos chunkPos : work) {
            if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
                stats.missingChunks++;
                continue;
            }

            long key = chunkPos.toLong();
            int startY;
            synchronized (LOADED_CHUNKS) {
                Map<Long, Integer> cursors = NEXT_Y_BY_CHUNK.get(level);
                if (cursors == null) continue;
                startY = cursors.getOrDefault(key, minY);
                if (startY < minY || startY >= maxY) startY = minY;
                int nextY = startY + Y_LEVELS_PER_PASS;
                cursors.put(key, nextY >= maxY ? minY : nextY);
            }

            stats.chunksScanned++;
            int endY = Math.min(maxY, startY + Y_LEVELS_PER_PASS);
            for (int y = startY; y < endY; y++) {
                for (int dx = 0; dx < 16; dx++) {
                    for (int dz = 0; dz < 16; dz++) {
                        BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + dx, y, chunkPos.getMinBlockZ() + dz);
                        stats.positionsScanned++;
                        if (!level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;

                        stats.biomeMatches++;
                        tintGlacialMagelight(level, pos);
                        FluidState fluid = level.getFluidState(pos);
                        if (fluid.is(FluidTags.WATER)) {
                            stats.waterFound++;
                            enqueue(level, pos, stats);
                        } else if (fluid.is(FluidTags.LAVA)) {
                            stats.lavaFound++;
                            enqueue(level, pos, stats);
                        }
                    }
                }
            }
        }
    }

    private static void tintGlacialMagelight(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!state.is(BlockRegistry.LIGHT_BLOCK.get())) return;

        ScanStats stats = STATS.get(level);
        if (stats != null) stats.magelightsFound++;

        if (!(level.getBlockEntity(pos) instanceof LightTile tile)) {
            if (stats != null) stats.magelightsMissingTile++;
            return;
        }

        ParticleColor current = tile.getColor();
        boolean alreadyIcyBlue = current.getRedInt() == ICE_BLUE.getRedInt()
                && current.getGreenInt() == ICE_BLUE.getGreenInt()
                && current.getBlueInt() == ICE_BLUE.getBlueInt();
        boolean alreadyIcyWhite = current.getRedInt() == ICE_WHITE.getRedInt()
                && current.getGreenInt() == ICE_WHITE.getGreenInt()
                && current.getBlueInt() == ICE_WHITE.getBlueInt();
        if (alreadyIcyBlue || alreadyIcyWhite) return;

        // LightTile emits particles directly from its saved colour.
        ParticleColor icyColor = Math.floorMod(pos.asLong(), 4L) == 0L ? ICE_WHITE : ICE_BLUE;
        tile.setColor(icyColor);
        tile.setChanged();
        if (stats != null) stats.magelightsTinted++;

        ArchwoodGood.LOGGER.info(
                "[GlacialFreeze] Recoloured Magelight at {} in {} from RGB({}, {}, {}) to RGB({}, {}, {}).",
                pos, level.dimension().location(),
                current.getRedInt(), current.getGreenInt(), current.getBlueInt(),
                icyColor.getRedInt(), icyColor.getGreenInt(), icyColor.getBlueInt());
    }

    private static void enqueue(ServerLevel level, BlockPos pos, ScanStats stats) {
        long key = pos.asLong();
        synchronized (LOADED_CHUNKS) {
            Set<Long> keys = PENDING_KEYS.computeIfAbsent(level, ignored -> new HashSet<>());
            if (keys.contains(key)) return;
            ArrayDeque<BlockPos> queue = PENDING.computeIfAbsent(level, ignored -> new ArrayDeque<>());
            if (queue.size() >= MAX_PENDING_POSITIONS) {
                stats.pendingDropped++;
                return;
            }
            keys.add(key);
            queue.addLast(pos.immutable());
        }
    }

    private static void convertPending(ServerLevel level, ScanStats stats) {
        for (int i = 0; i < MAX_CONVERSIONS_PER_TICK; i++) {
            BlockPos pos;
            synchronized (LOADED_CHUNKS) {
                ArrayDeque<BlockPos> queue = PENDING.get(level);
                if (queue == null || queue.isEmpty()) return;
                pos = queue.pollFirst();
                PENDING_KEYS.getOrDefault(level, Set.of()).remove(pos.asLong());
            }

            // Do not force unloaded chunks to load just to convert a stale entry.
            if (!level.hasChunkAt(pos)) continue;
            if (!level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;

            FluidState fluid = level.getFluidState(pos);
            if (fluid.is(FluidTags.WATER)) {
                if (level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3)) {
                    stats.waterConverted++;
                }
            } else if (fluid.is(FluidTags.LAVA)) {
                if (level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3)) {
                    stats.lavaConverted++;
                }
            }
        }
    }
}
