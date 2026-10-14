package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AWGFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, ArchwoodGood.MODID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, ArchwoodGood.MODID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS =
            DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, ArchwoodGood.MODID);

    public static final DeferredHolder<Feature<?>, IcyMageLightFeature> ICY_MAGE_LIGHT =
            FEATURES.register("icy_mage_light", () -> new IcyMageLightFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, GiantForestTreeFeature> GIANT_FOREST_TREE =
            FEATURES.register("giant_forest_tree", () -> new GiantForestTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<GiantMagicTrunkPlacer>> GIANT_MAGIC_TRUNK =
            TRUNK_PLACERS.register("giant_magic_trunk_placer", () -> new TrunkPlacerType<>(GiantMagicTrunkPlacer.CODEC));
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<AncientArchwoodExclusionPlacement>> ANCIENT_ARCHWOOD_EXCLUSION =
            PLACEMENT_MODIFIERS.register("ancient_archwood_exclusion",
                    () -> () -> AncientArchwoodExclusionPlacement.CODEC);
    public static final DeferredHolder<Feature<?>, AncientArchwoodGroundFeature> ANCIENT_ARCHWOOD_GROUND =
            FEATURES.register("ancient_archwood_ground",
                    () -> new AncientArchwoodGroundFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
        TRUNK_PLACERS.register(bus);
        PLACEMENT_MODIFIERS.register(bus);
    }
}
