package com.alexthw.archwood_good.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
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
 *
 * Work is driven by chunk load/unload events rather than player proximity.
 * Only a bounded number of positions are sampled per server tick. Fluids are
 * changed only when their own biome is the Glacial Archwood Caves.
 */
@EventBusSubscriber(modid = "archwood_good")
public class GlacialFreezeNearby {
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final int CHUNKS_PER_PASS = 2;
    private static final int SAMPLES_PER_CHUNK = 256;
    private static final int FREEZE_CHANCE_DENOMINATOR = 3;

    // Insertion order gives loaded chunks a fair, rotating share of the work.
    private static final Map<ServerLevel, LinkedHashMap<Long, ChunkPos>> LOADED_CHUNKS = new LinkedHashMap<>();
    private static final Map<ServerLevel, Integer> NEXT_CHUNK_INDEX = new LinkedHashMap<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        synchronized (LOADED_CHUNKS) {
            LOADED_CHUNKS.computeIfAbsent(level, ignored -> new LinkedHashMap<>())
                    .put(chunk.getPos().toLong(), chunk.getPos());
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
                } else {
                    NEXT_CHUNK_INDEX.put(level, 0);
                }
            }
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

        for (ChunkPos chunkPos : work) {
            // Never force-load a chunk just to freeze it.
            if (!level.hasChunk(chunkPos.x, chunkPos.z)) continue;

            int minY = level.getMinBuildHeight();
            int maxY = level.getMaxBuildHeight();
            if (maxY <= minY) continue;

            for (int sample = 0; sample < SAMPLES_PER_CHUNK; sample++) {
                int x = chunkPos.getMinBlockX() + level.getRandom().nextInt(16);
                int y = minY + level.getRandom().nextInt(maxY - minY);
                int z = chunkPos.getMinBlockZ() + level.getRandom().nextInt(16);
                BlockPos pos = new BlockPos(x, y, z);

                if (!level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;

                FluidState fluid = level.getFluidState(pos);
                if (!fluid.is(FluidTags.WATER) && !fluid.is(FluidTags.LAVA)) continue;
                if (level.getRandom().nextInt(FREEZE_CHANCE_DENOMINATOR) != 0) continue;

                if (fluid.is(FluidTags.WATER)) {
                    level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
        }
    }
}
