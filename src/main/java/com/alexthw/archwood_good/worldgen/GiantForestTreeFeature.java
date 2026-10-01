package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.config.AWGConfigCache;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Giant trees for Archwood Forest. Fading is excluded. */
public class GiantForestTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final String[] TREES = {
            "giant_blue_archwood_tree",
            "giant_red_archwood_tree",
            "giant_green_archwood_tree",
            "giant_purple_archwood_tree",
            "giant_yellow_archwood_tree",
            "giant_orange_archwood_tree",
            "giant_white_archwood_tree"
    };

    public GiantForestTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }


    /** Skip rivers and lakes. A puddle is fine; a wide water surface is not. */
    private static boolean isRiverOrOpenWater(WorldGenLevel level, BlockPos pos) {
        var biome = level.getBiome(pos);
        if (biome.is(net.minecraft.tags.BiomeTags.IS_RIVER)) {
            return true;
        }
        BlockPos ground = pos.below();
        if (!level.getBlockState(ground).isSolid() || level.getFluidState(ground).is(net.minecraft.tags.FluidTags.WATER)) {
            return true;
        }
        int water = 0;
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos floor = new BlockPos(pos.getX() + dx, pos.getY() - 1, pos.getZ() + dz);
                if (level.getFluidState(floor).is(net.minecraft.tags.FluidTags.WATER)
                        || level.getBlockState(floor).is(net.minecraft.world.level.block.Blocks.WATER)) {
                    water++;
                }
            }
        }
        return water >= 8;
    }

    public static boolean enabled() {
        return AWGConfigCache.giantTreesEnabled();
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!enabled()) {
            return false;
        }
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos pos = context.origin();
        if (!level.getBlockState(pos).isAir()) {
            pos = pos.above();
        }
        if (!level.getBlockState(pos).isAir()) {
            return false;
        }
        if (isRiverOrOpenWater(level, pos)) {
            return false;
        }
        BlockPos ground = pos.below();
        BlockPos[] pads = {ground, ground.east(), ground.south(), ground.east().south()};
        for (BlockPos pad : pads) {
            if (level.getBlockState(pad).isSolid()) {
                level.setBlock(pad, Blocks.DIRT.defaultBlockState(), 2);
            }
        }
        String name = TREES[random.nextInt(TREES.length)];
        ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(
                Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath("archwood_good", name));
        var lookup = level.registryAccess().lookup(Registries.CONFIGURED_FEATURE);
        if (lookup.isEmpty()) {
            return false;
        }
        var holder = lookup.get().get(key);
        if (holder.isEmpty()) {
            return false;
        }
        Holder<ConfiguredFeature<?, ?>> feature = holder.get();
        return feature.value().place(level, context.chunkGenerator(), random, pos);
    }
}
