package com.alexthw.archwood_good.mixins;

import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry;
import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry.ArchwoodVariant;
import alexthw.ars_elemental.common.rituals.forest.PlaceableLightFeature;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.features.BonemealFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.IPlaceableFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.RandomTreeFeature;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses the ritual's persisted consumed-item list to select Archwood Good
 * variants reliably, and excludes Fading from mixed Archwood forests.
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestationRitual", remap = false)
public abstract class ArchwoodForestationRitualMixin {

    @Inject(method = "addFeatures", at = @At("HEAD"), cancellable = true, remap = false)
    private void archwoodGood$handleSelectedAndMixedForests(
            List<IPlaceableFeature> features, CallbackInfo ci) {

        // selectedSapling is a transient field in Ars Elemental and is not
        // serialized. The consumed-item list is persisted by AbstractRitual,
        // so use it to recover the player's Archwood Good sapling selection.
        // getConsumedItems() is inherited from AbstractRitual, not declared on
        // ArchwoodForestationRitual. Cast to the real superclass to call it.
        for (ItemStack stack : ((AbstractRitual) (Object) this).getConsumedItems()) {
            if (stack.is(AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().asItem())
                    || stack.is(AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().asItem())
                    || stack.is(AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem())) {
                ArchwoodVariant selected = ArchwoodVariantsRegistry.get(stack.getItem());
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
        }

        // No Archwood Good sapling was consumed: preserve mixed-forest
        // generation, but deliberately omit Fading from its feature pool.
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
