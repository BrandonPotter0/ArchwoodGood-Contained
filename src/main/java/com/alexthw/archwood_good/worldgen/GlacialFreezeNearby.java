package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gradually converts fluids in loaded Glacial Archwood Caves chunks.
 * Diagnostic summaries are logged every 10 seconds to help identify whether
 * events, biome matching, or fluid detection are failing.
 */
@EventBusSubscriber(modid = ArchwoodGood.MODID)
public class GlacialFreezeNearby {
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final int LOG_INTERVAL_TICKS = 200;
    private static final int CHUNKS_PER_PASS = 8;
    private static final int Y_LEVELS_PER_PASS = 16;

    private static final Map<ServerLevel, LinkedHashMap<Long, ChunkPos>> LOADED_CHUNKS = new LinkedHashMap<>();
    private static final Map<ServerLevel, Integer> NEXT_CHUNK_INDEX = new LinkedHashMap<>();
    private static final Map<ServerLevel, Map<Long, Integer>> NEXT_Y_BY_CHUNK = new LinkedHashMap<>();
    private static final Map<ServerLevel, ScanStats> STATS = new LinkedHashMap<>();
    private static boolean tickEventLogged = false;

    private static final class ScanStats {
        long passes;
        long chunksScanned;
        long positionsScanned;
        long biomeMatches;
        long waterFound;
        long lavaFound;
        long waterConverted;
        long lavaConverted;
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
            ArchwoodGood.LOGGER.info("[GlacialFreeze] Server level tick event received; gradual fluid scanner is active.");
        }

        if (level.getGameTime() % CHECK_INTERVAL_TICKS != 0L) return;

        ArrayList<ChunkPos> work = new ArrayList<>(CHUNKS_PER_PASS);
        int trackedChunkCount;
        synchronized (LOADED_CHUNKS) {
            LinkedHashMap<Long, ChunkPos> chunks = LOADED_CHUNKS.get(level);
            trackedChunkCount = chunks == null ? 0 : chunks.size();
            if (chunks == null || chunks.isEmpty()) {
                ScanStats stats = STATS.computeIfAbsent(level, ignored -> new ScanStats());
                if (level.getGameTime() - stats.lastSummaryTick >= LOG_INTERVAL_TICKS) {
                    stats.lastSummaryTick = level.getGameTime();
                    ArchwoodGood.LOGGER.warn("[GlacialFreeze] Tick handler is running, but no loaded chunks are tracked for dimension {}. Chunk-load events may not be reaching the scanner.",
                            level.dimension().location());
                }
                return;
            }

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
        if (maxY <= minY) {
            ArchwoodGood.LOGGER.error("[GlacialFreeze] Invalid build-height range for {}: minY={}, maxY={}",
                    level.dimension().location(), minY, maxY);
            return;
        }

        ScanStats stats;
        synchronized (LOADED_CHUNKS) {
            stats = STATS.computeIfAbsent(level, ignored -> new ScanStats());
        }
        stats.passes++;

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
                        FluidState fluid = level.getFluidState(pos);
                        if (fluid.is(FluidTags.WATER)) {
                            stats.waterFound++;
                            if (level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3)) {
                                stats.waterConverted++;
                            }
                        } else if (fluid.is(FluidTags.LAVA)) {
                            stats.lavaFound++;
                            if (level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3)) {
                                stats.lavaConverted++;
                            }
                        }
                    }
                }
            }
        }

        if (level.getGameTime() - stats.lastSummaryTick >= LOG_INTERVAL_TICKS) {
            stats.lastSummaryTick = level.getGameTime();
            ArchwoodGood.LOGGER.info(
                    "[GlacialFreeze] dimension={}, trackedChunks={}, chunksScannedTotal={}, positionsScannedTotal={}, biomeMatchesTotal={}, waterFoundTotal={}, lavaFoundTotal={}, waterConvertedTotal={}, lavaConvertedTotal={}, missingChunksTotal={}",
                    level.dimension().location(), trackedChunkCount, stats.chunksScanned, stats.positionsScanned,
                    stats.biomeMatches, stats.waterFound, stats.lavaFound, stats.waterConverted, stats.lavaConverted,
                    stats.missingChunks);
        }
    }
}
