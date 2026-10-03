package com.alexthw.archwood_good.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AWGConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue GIANT_ARCHWOOD_TREES;
    public static final ModConfigSpec.BooleanValue ANCIENT_ARCHWOOD_EXCLUSION;
    public static final ModConfigSpec.IntValue ANCIENT_ARCHWOOD_EXCLUSION_RADIUS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("World generation").push("worldgen");
        GIANT_ARCHWOOD_TREES = builder
                .comment("Enable Giant Trees in Archwood Forests. Default is on.")
                .translation("archwood_good.config.giant_archwood_trees")
                .define("giantArchwoodTrees", true);
        ANCIENT_ARCHWOOD_EXCLUSION = builder
                .comment("Prevent Archwood trees from generating near Ancient Archwood Trees. Default is on.")
                .translation("archwood_good.config.ancient_archwood_exclusion")
                .define("ancientArchwoodExclusion", true);
        ANCIENT_ARCHWOOD_EXCLUSION_RADIUS = builder
                .comment("Horizontal radius in blocks around an Ancient Archwood Tree where Archwood tree generation is prevented. Default is 16.")
                .translation("archwood_good.config.ancient_archwood_exclusion_radius")
                .defineInRange("ancientArchwoodExclusionRadius", 16, 0, 256);
        builder.pop();
        SPEC = builder.build();
    }
    private AWGConfig() {}
}
