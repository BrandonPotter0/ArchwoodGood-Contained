package com.alexthw.archwood_good.worldgen.trees;

import com.alexthw.archwood_good.ArchwoodGood;
import com.alexthw.archwood_good.worldgen.AWGConfiguredFeatures;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class AWGTreeGrowers {

    public static final TreeGrower ORANGE_ARCHWOOD_TREE = new TreeGrower(
            ArchwoodGood.res("orange_archwood_tree").toString(),
            Optional.of(AWGConfiguredFeatures.GIANT_ORANGE_TREE_KEY),
            Optional.of(AWGConfiguredFeatures.ORANGE_SAPLING_TREE_KEY),
            Optional.empty());

    public static final TreeGrower WHITE_ARCHWOOD_TREE = new TreeGrower(
            ArchwoodGood.res("white_archwood_tree").toString(),
            Optional.of(AWGConfiguredFeatures.GIANT_WHITE_TREE_KEY),
            Optional.of(AWGConfiguredFeatures.WHITE_SAPLING_TREE_KEY),
            Optional.empty());

    public static final TreeGrower FADING_ARCHWOOD_TREE = new TreeGrower(
            ArsNouveau.prefix("fading_archwood_tree").toString(),
            Optional.of(AWGConfiguredFeatures.GIANT_FADING_TREE_KEY),
            Optional.of(AWGConfiguredFeatures.FADING_SAPLING_TREE_KEY),
            Optional.empty());
}
