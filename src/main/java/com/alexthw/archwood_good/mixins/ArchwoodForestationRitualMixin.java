package com.alexthw.archwood_good.mixins;

import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry;
import alexthw.ars_elemental.common.rituals.forest.PlaceableLightFeature;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.hollingsworth.arsnouveau.api.ritual.features.BonemealFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.IPlaceableFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.RandomTreeFeature;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps Fading Archwood selectable individually while excluding it from
 * Ars Elemental's mixed Archwood forest.
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestationRitual", remap = false)
public abstract class ArchwoodForestationRitualMixin {

    @Shadow(remap = false)
    private Item selectedSapling;

    @Inject(method = "addFeatures", at = @At("HEAD"), cancellable = true, remap = false)
    private void archwoodGood$excludeFadingFromMixedForest(
            List<IPlaceableFeature> features, CallbackInfo ci) {
        // A selected sapling follows Ars Elemental's normal per-variant path.
        // Only replace the unselected/mixed-forest path.
        if (selectedSapling != null) {
            return;
        }

        features.add(new BonemealFeature(6, 0.4));

        List<BlockState> saplings = new ArrayList<>();
        for (ArchwoodVariantsRegistry.ArchwoodVariant variant
                : ArchwoodVariantsRegistry.getAll()) {
            if (variant.sapling() == AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem()) {
                continue;
            }
            if (variant.sapling() instanceof BlockItem blockItem) {
                saplings.add(blockItem.getBlock().defaultBlockState());
            }
        }

        if (!saplings.isEmpty()) {
            features.add(new RandomTreeFeature(saplings, 8, 0.8));
        }

        for (ArchwoodVariantsRegistry.ArchwoodVariant variant
                : ArchwoodVariantsRegistry.getAll()) {
            if (variant.sapling() == AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem()) {
                continue;
            }
            features.add(variant.plantFeature().get());
            features.add(variant.podFeature().get());
        }

        features.add(new PlaceableLightFeature(4, 0.1));
        ci.cancel();
    }
}
