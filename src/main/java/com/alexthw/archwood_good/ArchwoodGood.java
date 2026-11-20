package com.alexthw.archwood_good;

import com.alexthw.archwood_good.compat.ArsElementalForestationCompat;
import com.alexthw.archwood_good.compat.TerraBlenderCompat;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.alexthw.archwood_good.registry.AWGItemRegistry;
import com.alexthw.archwood_good.registry.AWGPottedPlants;
import com.alexthw.archwood_good.worldgen.AWGFeatures;
import com.alexthw.archwood_good.config.AWGConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                TerraBlenderCompat.register();
            } else {
                LOGGER.warn("TerraBlender not loaded; custom forests and Glacial Archwood Caves will not generate naturally");
            }
        });
    }

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
