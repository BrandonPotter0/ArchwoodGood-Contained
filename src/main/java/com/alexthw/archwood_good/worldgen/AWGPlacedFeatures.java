package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class AWGPlacedFeatures {

    public static final ResourceKey<PlacedFeature> PLACED_ORANGE_TREE_KEY = register("placed_orange_archwood_tree");
    public static final ResourceKey<PlacedFeature> PLACED_WHITE_TREE_KEY = register("placed_white_archwood_tree");
    public static final ResourceKey<PlacedFeature> PLACED_ORANGE_WHITE_KEY = register("placed_orange_white_archwoods");
    public static final ResourceKey<PlacedFeature> PLACED_RARE_ORANGE_WHITE_KEY = register("placed_rare_orange_white_archwoods");

    /** Kept for compatibility with AWGBiomeModifier. */
    public static final ResourceKey<PlacedFeature> COMMON_ORANGE_PLACED_KEY = PLACED_ORANGE_WHITE_KEY;

    private static ResourceKey<PlacedFeature> register(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ArchwoodGood.res(name));
    }
}
