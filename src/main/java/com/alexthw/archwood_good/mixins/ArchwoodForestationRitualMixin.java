package com.alexthw.archwood_good.mixins;

import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry;
import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry.ArchwoodVariant;
import alexthw.ars_elemental.common.rituals.forest.PlaceableLightFeature;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.hollingsworth.arsnouveau.api.ritual.features.BonemealFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.IPlaceableFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.RandomTreeFeature;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps Archwood Good sapling selection stable while the brazier checks its
 * other ingredients, and excludes Fading from mixed Archwood forests.
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestationRitual", remap = false)
public abstract class ArchwoodForestationRitualMixin {

    /**
     * Ars Elemental clears its selectedSapling field when canConsumeItem checks
     * a non-sapling ingredient. Track our own selection so later ingredient
     * checks cannot erase it.
     */
    @Unique
    private Item archwoodGood$selectedSapling;

    @Inject(method = "canConsumeItem", at = @At("HEAD"), remap = false)
    private void archwoodGood$captureSelectedSapling(
            ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ArchwoodVariantsRegistry.get(stack.getItem()) != null) {
            archwoodGood$selectedSapling = stack.getItem();
        }
    }

    @Inject(method = "addFeatures", at = @At("HEAD"), cancellable = true, remap = false)
    private void archwoodGood$handleSelectedAndMixedForests(
            List<IPlaceableFeature> features, CallbackInfo ci) {
        // Handle a selected Archwood Good sapling ourselves. This selection is
        // independent of Ars Elemental's field, which can be cleared by later
        // checks for other ritual ingredients.
        if (archwoodGood$selectedSapling != null) {
            ArchwoodVariant selected =
                    ArchwoodVariantsRegistry.get(archwoodGood$selectedSapling);
            if (selected != null) {
                features.add(new BonemealFeature(6, 0.4));
                features.add(selected.treeFeature().get());
                features.add(selected.plantFeature().get());
                features.add(selected.podFeature().get());
                features.add(new PlaceableLightFeature(4, 0.1));
                ci.cancel();
                return;
            }
        }

        // No Archwood Good sapling was selected: preserve mixed-forest
        // generation, but deliberately omit Fading from its tree/feature pool.
        features.add(new BonemealFeature(6, 0.4));

        List<BlockState> saplings = new ArrayList<>();
        for (ArchwoodVariant variant : ArchwoodVariantsRegistry.getAll()) {
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

        for (ArchwoodVariant variant : ArchwoodVariantsRegistry.getAll()) {
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
