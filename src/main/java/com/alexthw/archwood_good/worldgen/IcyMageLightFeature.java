package com.alexthw.archwood_good.worldgen;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Places Ars Nouveau Magelight blocks in Glacial Archwood Caves.
 *
 * Colour is handled by GlacialMagelightParticleMixin on the client. We do not
 * attempt to access or mutate the block entity during world generation because
 * the chunk's block entities may not exist until the chunk is fully loaded.
 */
public class IcyMageLightFeature extends Feature<NoneFeatureConfiguration> {
    public IcyMageLightFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        if (!level.getBlockState(pos).isAir()) {
            return false;
        }

        boolean anchored = false;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).isSolid()) {
                anchored = true;
                break;
            }
        }
        if (!anchored) {
            return false;
        }

        BlockState light = BlockRegistry.LIGHT_BLOCK.get().defaultBlockState();
        return level.setBlock(pos, light, 3);
    }
}
