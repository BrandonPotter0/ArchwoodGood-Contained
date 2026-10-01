package com.alexthw.archwood_good.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = "archwood_good")
public class GlacialSnowAccumulate {
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.getGameTime() % 35L != 0L) return;
        RandomSource random = level.random;
        for (ServerPlayer player : level.players()) {
            if (!level.getBiome(player.blockPosition()).is(AWGBiomes.GLACIAL_CAVES_KEY)) continue;
            for (int i = 0; i < 6; i++) {
                pile(level, player.blockPosition().offset(random.nextInt(17) - 8, random.nextInt(11) - 4, random.nextInt(17) - 8));
            }
        }
    }

    private static void pile(ServerLevel level, BlockPos guess) {
        BlockPos.MutableBlockPos cursor = guess.mutable();
        for (int step = 0; step < 8 && level.getBlockState(cursor).isAir(); step++) cursor.move(Direction.DOWN);
        if (!level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.UP)) return;
        BlockPos above = cursor.above();
        if (!level.getFluidState(above).isEmpty() || !level.getFluidState(cursor).isEmpty()) return;
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.is(Blocks.SNOW)) {
            int layers = aboveState.getValue(SnowLayerBlock.LAYERS);
            if (layers < 8) level.setBlock(above, aboveState.setValue(SnowLayerBlock.LAYERS, layers + 1), 2);
            return;
        }
        if (aboveState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(level, above)) {
            level.setBlock(above, Blocks.SNOW.defaultBlockState(), 2);
        }
    }
}
