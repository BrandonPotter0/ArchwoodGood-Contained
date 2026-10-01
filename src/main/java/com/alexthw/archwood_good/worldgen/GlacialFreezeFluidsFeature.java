package com.alexthw.archwood_good.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.FluidState;

public class GlacialFreezeFluidsFeature extends Feature<NoneFeatureConfiguration> {
    public GlacialFreezeFluidsFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        int x0 = origin.getX() & ~15;
        int z0 = origin.getZ() & ~15;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean any = false;
        int minY = level.getMinBuildHeight();
        int maxY = Math.min(level.getMaxBuildHeight(), 128);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    cursor.set(x0 + x, y, z0 + z);
                    FluidState fluid = level.getFluidState(cursor);
                    if (fluid.isEmpty()) continue;
                    if (fluid.is(FluidTags.WATER)) {
                        level.setBlock(cursor, Blocks.ICE.defaultBlockState(), 2);
                        any = true;
                    } else if (fluid.is(FluidTags.LAVA)) {
                        level.setBlock(cursor, Blocks.OBSIDIAN.defaultBlockState(), 2);
                        any = true;
                    }
                }
            }
        }
        return any;
    }
}
