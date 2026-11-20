package com.alexthw.archwood_good.compat;

import alexthw.ars_elemental.common.rituals.forest.ArchpodFeature;
import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.alexthw.archwood_good.worldgen.AWGBiomes;
import com.hollingsworth.arsnouveau.api.ritual.features.PlaceBlockFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.RandomTreeFeature;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

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

        // Dawning: orange Archwood, Dawnberry pods, and pumpkin growth.
        ArchwoodVariantsRegistry.register(new ArchwoodVariantsRegistry.ArchwoodVariant(
                AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().asItem(),
                AWGBlockRegistry.DAWNBERRY_POD.get().asItem(),
                () -> new RandomTreeFeature(
                        List.of(AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().defaultBlockState()),
                        8, 0.8),
                () -> new NamedPlaceBlockFeature(
                        2, 0.15, Blocks.PUMPKIN::defaultBlockState,
                        "archwood_good_dawning_pumpkin"),
                () -> new ArchpodFeature(
                        1, 0.15, AWGBlockRegistry.DAWNBERRY_POD.get().defaultBlockState()),
                () -> AWGBiomes.DAWNING_FOREST_KEY
        ));

        // Blinding: white Archwood, Lightchee pods, and abundant white tulips.
        ArchwoodVariantsRegistry.register(new ArchwoodVariantsRegistry.ArchwoodVariant(
                AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().asItem(),
                AWGBlockRegistry.LIGHTCHEE_POD.get().asItem(),
                () -> new RandomTreeFeature(
                        List.of(AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().defaultBlockState()),
                        8, 0.8),
                () -> new NamedPlaceBlockFeature(
                        2, 0.2, Blocks.WHITE_TULIP::defaultBlockState,
                        "archwood_good_blinding_tulip"),
                () -> new ArchpodFeature(
                        1, 0.15, AWGBlockRegistry.LIGHTCHEE_POD.get().defaultBlockState()),
                () -> AWGBiomes.BLINDING_FOREST_KEY
        ));

        // Fading: a dead, eerie forest decorated with cobwebs and skeleton skulls.
        // It remains selectable individually but is excluded from mixed forests.
        ArchwoodVariantsRegistry.register(new ArchwoodVariantsRegistry.ArchwoodVariant(
                AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem(),
                null,
                () -> new RandomTreeFeature(
                        List.of(AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().defaultBlockState()),
                        8, 0.8),
                () -> new PlaceBlockFeature(
                        3, 0.15, Blocks.COBWEB::defaultBlockState),
                () -> new PlaceBlockFeature(
                        1, 0.08, Blocks.SKELETON_SKULL::defaultBlockState),
                () -> AWGBiomes.FADING_FOREST_KEY
        ));
    }

    /**
     * Uses a dedicated placement category so this decoration has its own
     * spacing map instead of competing with Ars Elemental's other "block"
     * features in mixed forests.
     */
    private static final class NamedPlaceBlockFeature extends PlaceBlockFeature {

        private final String featureName;

        private NamedPlaceBlockFeature(
                double distance,
                double chance,
                Supplier<BlockState> block,
                String featureName) {
            super(distance, chance, block);
            this.featureName = featureName;
        }

        @Override
        public String getFeatureName() {
            return featureName;
        }
    }
}
