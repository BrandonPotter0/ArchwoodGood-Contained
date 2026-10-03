package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.config.AWGConfigCache;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Lightly decorates the ground around Ancient Archwood Trees.
 * Only existing grass blocks are replaced, keeping the effect subtle and natural.
 */
public class AncientArchwoodGroundFeature extends Feature<NoneFeatureConfiguration> {
    public AncientArchwoodGroundFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!AWGConfigCache.ancientArchwoodExclusionEnabled()) {
            return false;
        }

        WorldGenLevel level = context.level();
        if (!(level instanceof WorldGenRegion region)) {
            return false;
        }

        BlockPos pos = context.origin();

        // Only decorate the circular exclusion buffer, never the wider forest.
        if (!AncientArchwoodExclusionPlacement.isWithinAncientExclusion(
                region, pos, AWGConfigCache.ancientArchwoodExclusionRadius())) {
            return false;
        }

        // Never alter blocks belonging to the Ancient Archwood structure itself.
        if (AncientArchwoodExclusionPlacement.isInsideAncientStructure(region, pos)) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.GRASS_BLOCK)) {
            BlockPos below = pos.below();
            if (level.getBlockState(below).is(Blocks.GRASS_BLOCK)) {
                pos = below;
                state = level.getBlockState(pos);
            } else {
                return false;
            }
        }

        RandomSource random = context.random();
        int roll = random.nextInt(100);

        // Keep most of the clearing green, with scattered weathered stone,
        // moss and a small number of damp water patches.
        if (roll < 45) {
            level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 2);
        } else if (roll < 85) {
            level.setBlock(pos, Blocks.MOSS_BLOCK.defaultBlockState(), 2);
        } else {
            level.setBlock(pos, Blocks.WATER.defaultBlockState(), 2);
        }

        return true;
    }
}
