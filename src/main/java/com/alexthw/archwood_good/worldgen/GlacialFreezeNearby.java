package com.alexthw.archwood_good.worldgen;

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
 * Each pass scans a complete 16x16 horizontal slice at one Y level, rather
 * than relying on random samples that can miss fluids indefinitely.
 */
@EventBusSubscriber(modid = "archwood_good")
public class GlacialFreezeNearby {
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final int CHUNKS_PER_PASS = 2;

    private static final Map<ServerLevel, LinkedHashMap<Long, ChunkPos>> LOADED_CHUNKS = new LinkedHashMap<>();
    private static final Map<ServerLevel, Integer> NEXT_CHUNK_INDEX = new LinkedHashMap<>();
    private static final Map<ServerLevel, Map<Long, Integer>> NEXT_Y_BY_CHUNK = new LinkedHashMap<>();

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
                }
            }
            Map<Long, Integer> cursors = NEXT_Y_BY_CHUNK.get(level);
            if (cursors != null) cursors.remove(chunk.getPos().toLong());
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.getGameTime() % CHECK_INTERVAL_TICKS != 0L) return;

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
            if (!level.hasChunk(chunkPos.x, chunkPos.z)) continue;

            long key = chunkPos.toLong();
            int y;
            synchronized (LOADED_CHUNKS) {
                Map<Long, Integer> cursors = NEXT_Y_BY_CHUNK.get(level);
                if (cursors == null) continue;
                y = cursors.getOrDefault(key, minY);
                if (y < minY || y >= maxY) y = minY;
                cursors.put(key, y + 1 >= maxY ? minY : y + 1);
            }

            // Scan every block in this horizontal slice. The work is capped
            // at two 16x16 slices per pass to avoid a full-chunk tick spike.
            for (int dx = 0; dx < 16; dx++) {
                for (int dz = 0; dz < 16; dz++) {
                    BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + dx, y, chunkPos.getMinBlockZ() + dz);
                    if (!level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;

                    FluidState fluid = level.getFluidState(pos);
                    if (fluid.is(FluidTags.WATER)) {
                        level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                    } else if (fluid.is(FluidTags.LAVA)) {
                        level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
