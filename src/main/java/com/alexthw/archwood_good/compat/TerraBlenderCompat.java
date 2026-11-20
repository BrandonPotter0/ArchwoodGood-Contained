package com.alexthw.archwood_good.compat;

import com.alexthw.archwood_good.ArchwoodGood;
import com.alexthw.archwood_good.worldgen.biome.BlindingForestRegion;
import com.alexthw.archwood_good.worldgen.biome.DawningForestRegion;
import com.alexthw.archwood_good.worldgen.biome.FadingForestRegion;
import com.alexthw.archwood_good.worldgen.biome.GlacialCavesRegion;
import terrablender.api.Regions;

/**
 * TerraBlender integration kept in its own class so Archwood Good can load
 * when TerraBlender is not installed.
 */
public final class TerraBlenderCompat {
    private TerraBlenderCompat() {
    }

    public static void register() {
        Regions.register(new DawningForestRegion(ArchwoodGood.res("overworld_dawning"), 2));
        Regions.register(new BlindingForestRegion(ArchwoodGood.res("overworld_blinding"), 2));
        Regions.register(new GlacialCavesRegion(ArchwoodGood.res("overworld_glacial_caves"), 3));
        Regions.register(new FadingForestRegion(ArchwoodGood.res("overworld_fading"), 2));

        ArchwoodGood.LOGGER.info("Registered Dawning, Blinding, Fading, and Glacial cave TerraBlender regions");
    }
}
