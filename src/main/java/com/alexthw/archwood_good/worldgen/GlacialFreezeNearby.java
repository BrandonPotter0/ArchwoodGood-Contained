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

@EventBusSubscriber(modid = "archwood_good")
public class GlacialFreezeNearby {
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.getGameTime() % 20L != 0L) return;
        for (ServerPlayer player : level.players()) {
            if (!level.getBiome(player.blockPosition()).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;
            BlockPos center = player.blockPosition();
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int dx = -6; dx <= 6; dx++) {
                for (int dy = -6; dy <= 6; dy++) {
                    for (int dz = -6; dz <= 6; dz++) {
                        cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                        if (!level.getBiome(cursor).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;
                        FluidState fluid = level.getFluidState(cursor);
                        if (fluid.is(FluidTags.WATER)) level.setBlock(cursor, Blocks.ICE.defaultBlockState(), 3);
                        else if (fluid.is(FluidTags.LAVA)) level.setBlock(cursor, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
