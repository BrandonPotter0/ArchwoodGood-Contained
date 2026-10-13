package com.alexthw.archwood_good.worldgen;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.LightBlobMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.LightTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
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

/**
 * Places Ars Nouveau magelights with pale white or icy-blue particles during
 * world generation, rather than relying on a later chunk scan to recolour them.
 */
public class IcyMageLightFeature extends Feature<NoneFeatureConfiguration> {
    private static final ParticleColor ICE_WHITE = new ParticleColor(240, 248, 255);
    private static final ParticleColor ICE_BLUE = new ParticleColor(173, 216, 230);

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

        // Use full update flags so the block entity is initialized and clients are
        // notified where appropriate. Retrieve/create the block entity immediately
        // from the generating chunk instead of assuming getBlockEntity has created it.
        BlockState light = BlockRegistry.LIGHT_BLOCK.get().defaultBlockState();
        if (!level.setBlock(pos, light, 3)) {
            return false;
        }

        if (level.getChunk(pos) instanceof LevelChunk chunk
                && chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE) instanceof LightTile tile) {
            ParticleColor icyColor = random.nextFloat() < 0.35F ? ICE_WHITE : ICE_BLUE;
            tile.setColor(icyColor);

            // The live particle timeline must carry the same colour as the tile.
            PropertyParticleOptions particleOptions = new PropertyParticleOptions(ModParticles.NEW_GLOW_TYPE.get());
            ColorProperty colorProperty = new ColorProperty();
            colorProperty.particleColor = icyColor;
            particleOptions.map.set(ParticlePropertyRegistry.COLOR_PROPERTY.get(), colorProperty);
            LightTimeline timeline = new LightTimeline();
            timeline.onTickEffect = new TimelineEntryData(new LightBlobMotion(), particleOptions);
            tile.setTimeline(timeline);
            tile.setChanged();
            return true;
        }

        return true;
    }
}
