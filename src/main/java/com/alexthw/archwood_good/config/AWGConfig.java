package com.alexthw.archwood_good.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AWGConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue GIANT_ARCHWOOD_TREES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("World generation").push("worldgen");
        GIANT_ARCHWOOD_TREES = builder
                .comment("Enable Giant Trees in Archwood Forests. Default is on.")
                .translation("archwood_good.config.giant_archwood_trees")
                .define("giantArchwoodTrees", true);
        builder.pop();
        SPEC = builder.build();
    }

    private AWGConfig() {}
}
