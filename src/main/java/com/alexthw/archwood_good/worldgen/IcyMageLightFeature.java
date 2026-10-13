package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Places Ars Nouveau magelight blocks with a Glacial Caves colour.
 * Ars Nouveau's LightTile emits particles directly from its saved colour;
 * a custom particle timeline is not needed for this block in 1.21.x.
 */
public class IcyMageLightFeature extends Feature<NoneFeatureConfiguration> {
    private static final ParticleColor ICE_WHITE = new ParticleColor(240, 248, 255);
    private static final ParticleColor ICE_BLUE = new ParticleColor(173, 216, 230);
    private static final AtomicInteger LOGGED_PLACEMENTS = new AtomicInteger();

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
        if (!level.setBlock(pos, light, 3)) {
            return false;
        }

        if (level.getChunk(pos) instanceof LevelChunk chunk
                && chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE) instanceof LightTile tile) {
            ParticleColor icyColor = random.nextFloat() < 0.35F ? ICE_WHITE : ICE_BLUE;
            tile.setColor(icyColor);
            tile.setChanged();

            // Keep diagnostic output bounded: world generation can place many lights.
            if (LOGGED_PLACEMENTS.getAndIncrement() < 12) {
                ArchwoodGood.LOGGER.info(
                        "[GlacialMageLight] Custom feature placed icy Magelight at {} in biome {} with RGB({}, {}, {}).",
                        pos, level.getBiome(pos).unwrapKey().map(key -> key.location().toString()).orElse("unknown"),
                        icyColor.getRedInt(), icyColor.getGreenInt(), icyColor.getBlueInt());
            }
        } else {
            ArchwoodGood.LOGGER.warn(
                    "[GlacialMageLight] Custom feature placed light block at {}, but could not obtain its LightTile block entity.",
                    pos);
        }

        return true;
    }
}
