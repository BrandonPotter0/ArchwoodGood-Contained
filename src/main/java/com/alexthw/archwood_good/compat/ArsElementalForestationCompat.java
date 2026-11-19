package com.alexthw.archwood_good.compat;

import alexthw.ars_elemental.common.rituals.forest.ArchpodFeature;
import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.alexthw.archwood_good.worldgen.AWGBiomes;
import com.hollingsworth.arsnouveau.api.ritual.features.IPlaceableFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.PlaceBlockFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.RandomTreeFeature;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

import static com.alexthw.archwood_good.worldgen.trees.AWGTreeGrowers.*;

/**
 * Registers Archwood Good saplings with Ars Elemental's 1.21.1
 * Archwood Forestation variant registry.
 *
 * Called from common setup after Ars Elemental has initialized its own variants.
 */
public final class ArsElementalForestationCompat {

    private ArsElementalForestationCompat() {
    }

    public static void registerVariants() {
        IPlaceableFeature noOpFeature = new PlaceBlockFeature(
                0, 0, Blocks.AIR::defaultBlockState);

        ArchwoodVariantsRegistry.register(new ArchwoodVariantsRegistry.ArchwoodVariant(
                AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().asItem(),
                AWGBlockRegistry.DAWNBERRY_POD.get().asItem(),
                () -> new RandomTreeFeature(
                        List.of(AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().defaultBlockState()),
                        8, 0.8),
                () -> noOpFeature,
                () -> new ArchpodFeature(
                        1, 0.15, AWGBlockRegistry.DAWNBERRY_POD.get().defaultBlockState()),
                () -> AWGBiomes.DAWNING_FOREST_KEY
        ));

        ArchwoodVariantsRegistry.register(new ArchwoodVariantsRegistry.ArchwoodVariant(
                AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().asItem(),
                AWGBlockRegistry.LIGHTCHEE_POD.get().asItem(),
                () -> new RandomTreeFeature(
                        List.of(AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().defaultBlockState()),
                        8, 0.8),
                () -> noOpFeature,
                () -> new ArchpodFeature(
                        1, 0.15, AWGBlockRegistry.LIGHTCHEE_POD.get().defaultBlockState()),
                () -> AWGBiomes.BLINDING_FOREST_KEY
        ));

        // Fading is selectable by its own sapling, but deliberately has no pod
        // and is excluded from the mixed forest by ArchwoodForestationRitualMixin.
        ArchwoodVariantsRegistry.register(new ArchwoodVariantsRegistry.ArchwoodVariant(
                AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem(),
                null,
                () -> new RandomTreeFeature(
                        List.of(AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().defaultBlockState()),
                        8, 0.8),
                () -> noOpFeature,
                () -> noOpFeature,
                () -> AWGBiomes.FADING_FOREST_KEY
        ));
    }
}
