package com.alexthw.archwood_good;

import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.alexthw.archwood_good.compat.ArsElementalForestationCompat;
import com.alexthw.archwood_good.registry.AWGItemRegistry;
import com.alexthw.archwood_good.registry.AWGPottedPlants;
import com.alexthw.archwood_good.worldgen.AWGFeatures;
import com.alexthw.archwood_good.worldgen.biome.BlindingForestRegion;
import com.alexthw.archwood_good.worldgen.biome.GlacialCavesRegion;
import com.alexthw.archwood_good.worldgen.biome.DawningForestRegion;
import com.alexthw.archwood_good.worldgen.biome.FadingForestRegion;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import com.alexthw.archwood_good.config.AWGConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.api.Regions;

import static com.alexthw.archwood_good.registry.AWGBlockRegistry.BLOCKS;
import static com.alexthw.archwood_good.registry.AWGBlockRegistry.ITEMS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArchwoodGood.MODID)
public class ArchwoodGood {
    public static final String MODID = "archwood_good";
    public static final Logger LOGGER = LogManager.getLogger("Archwood Good");

    public ArchwoodGood(IEventBus eventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, AWGConfig.SPEC);
        // Initialize potted block registrations before the DeferredRegister is attached to the event bus.
        AWGPottedPlants.init();
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);

        AWGBlockRegistry.init(eventBus);
        AWGItemRegistry.init();

        AWGModSetup.registers(eventBus);
        AWGFeatures.register(eventBus);

//        FEATURES.register(eventBus);

        AWGCreativeTabRegistry.TABS.register(eventBus);
        eventBus.addListener(ArchwoodGood::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded("ars_elemental")) {
                ArsElementalForestationCompat.registerVariants();
                LOGGER.info("Registered Dawning, Blinding, and Fading Archwood Forestation variants with Ars Elemental");
            }
            if (ModList.get().isLoaded("terrablender")) {
                Regions.register(new DawningForestRegion(res("overworld_dawning"), 2));
                Regions.register(new BlindingForestRegion(res("overworld_blinding"), 2));
                Regions.register(new GlacialCavesRegion(res("overworld_glacial_caves"), 3));
                Regions.register(new FadingForestRegion(res("overworld_fading"), 2));
                LOGGER.info("Registered Dawning, Blinding, Fading, and Glacial cave TerraBlender regions");
            } else {
                LOGGER.warn("TerraBlender not loaded; custom forests will exist but will not generate naturally");
            }
        });
    }

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
