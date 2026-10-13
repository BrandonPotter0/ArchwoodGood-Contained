package com.alexthw.archwood_good.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Gradually freezes fluids near players in the Glacial Archwood Caves while
 * the area is loaded. Sampling is concentrated around each player so nearby
 * pools are revisited often without instantly sweeping an entire area.
 */
@EventBusSubscriber(modid = "archwood_good")
public class GlacialFreezeNearby {
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final int SAMPLES_PER_PLAYER = 512;
    private static final int HORIZONTAL_RADIUS = 16;
    private static final int VERTICAL_RADIUS = 32;
    private static final int FREEZE_CHANCE_DENOMINATOR = 3;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.getGameTime() % CHECK_INTERVAL_TICKS != 0L) return;

        for (ServerPlayer player : level.players()) {
            BlockPos center = player.blockPosition();

            for (int sample = 0; sample < SAMPLES_PER_PLAYER; sample++) {
                int x = center.getX() + level.getRandom().nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
                int minY = Math.max(level.getMinBuildHeight(), center.getY() - VERTICAL_RADIUS);
                int maxY = Math.min(level.getMaxBuildHeight(), center.getY() + VERTICAL_RADIUS + 1);
                if (maxY <= minY) continue;
                int y = minY + level.getRandom().nextInt(maxY - minY);
                int z = center.getZ() + level.getRandom().nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
                BlockPos pos = new BlockPos(x, y, z);

                FluidState fluid = level.getFluidState(pos);
                if (!fluid.is(FluidTags.WATER) && !fluid.is(FluidTags.LAVA)) continue;
                if (!level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;

                // A sampled fluid is not guaranteed to freeze immediately.
                // Repeated visits give the area a gradual, time-based change.
                if (level.getRandom().nextInt(FREEZE_CHANCE_DENOMINATOR) != 0) continue;

                if (fluid.is(FluidTags.WATER)) {
                    level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
        }
    }
}
