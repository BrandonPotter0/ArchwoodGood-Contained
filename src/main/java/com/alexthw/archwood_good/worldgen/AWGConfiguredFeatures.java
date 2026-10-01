package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class AWGConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_SAPLING_TREE_KEY = register("orange_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_SAPLING_TREE_KEY = register("white_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FADING_SAPLING_TREE_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, ArsNouveau.prefix("fading_archwood_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_ORANGE_TREE_KEY = register("giant_orange_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_WHITE_TREE_KEY = register("giant_white_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_FADING_TREE_KEY = register("giant_fading_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_BLUE_TREE_KEY = register("giant_blue_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_RED_TREE_KEY = register("giant_red_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_GREEN_TREE_KEY = register("giant_green_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_PURPLE_TREE_KEY = register("giant_purple_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_YELLOW_TREE_KEY = register("giant_yellow_archwood_tree");

    public static final ResourceKey<ConfiguredFeature<?, ?>> NATURAL_ORANGE_TREE_KEY = register("natural_orange_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NATURAL_WHITE_TREE_KEY = register("natural_white_archwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIXED_ORANGE_WHITE_KEY = register("mixed_orange_white_archwoods");

    /** Kept for compatibility with AWGBiomeModifier. */
    public static final ResourceKey<ConfiguredFeature<?, ?>> COMMON_ORANGE_TREE_KEY = register("mixed_orange_white_archwoods");

    private static ResourceKey<ConfiguredFeature<?, ?>> register(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ArchwoodGood.res(name));
    }
}
