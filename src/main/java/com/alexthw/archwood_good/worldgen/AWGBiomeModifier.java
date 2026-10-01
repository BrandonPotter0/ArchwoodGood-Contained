package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AWGBiomeModifier {

    public static final TagKey<Biome> ARCHWOOD_FOREST_BIOMES = TagKey.create(Registries.BIOME, ArchwoodGood.res("has_orange_white_archwoods"));

    public static final ResourceKey<BiomeModifier> ORANGE_WHITE_IN_ARCHWOOD_FOREST = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, ArchwoodGood.res("orange_white_in_archwood_forest"));

    public static void bootstrap(BootstrapContext<BiomeModifier> contextBM) {
        HolderSet<Biome> forests = contextBM.lookup(Registries.BIOME).getOrThrow(ARCHWOOD_FOREST_BIOMES);
        try {
            Holder.Reference<PlacedFeature> trees = contextBM.lookup(Registries.PLACED_FEATURE)
                    .get(AWGPlacedFeatures.PLACED_ORANGE_WHITE_KEY).orElseThrow();
            contextBM.register(ORANGE_WHITE_IN_ARCHWOOD_FOREST,
                    new BiomeModifiers.AddFeaturesBiomeModifier(
                            forests,
                            HolderSet.direct(trees),
                            GenerationStep.Decoration.VEGETAL_DECORATION));
        } catch (Exception ignored) {
        }
    }
}
