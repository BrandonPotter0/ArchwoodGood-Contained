package com.alexthw.archwood_good.worldgen.biome;

import com.alexthw.archwood_good.worldgen.AWGBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

/**
 * Rare overworld slice, same pattern Ars Nouveau uses for Archwood Forest.
 * Warm savanna climate cells in this region become Dawning Forest.
 */
public class DawningForestRegion extends Region {
    public DawningForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder ->
                builder.replaceBiome(Biomes.SAVANNA, AWGBiomes.DAWNING_FOREST_KEY));
    }
}
