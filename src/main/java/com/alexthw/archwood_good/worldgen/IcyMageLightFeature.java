package com.alexthw.archwood_good.worldgen;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Places an Ars Nouveau mage light tinted white or icy blue. */
public class IcyMageLightFeature extends Feature<NoneFeatureConfiguration> {
    public IcyMageLightFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

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
        level.setBlock(pos, light, 2);
        if (level.getBlockEntity(pos) instanceof LightTile tile) {
            if (random.nextBoolean()) {
                tile.color = new ParticleColor(240, 248, 255); // white
            } else {
                tile.color = new ParticleColor(70, 150, 255);  // blue
            }
        }
        return true;
    }
}
