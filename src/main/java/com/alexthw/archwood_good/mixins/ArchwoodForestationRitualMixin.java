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
 * Adds Archwood Good's individual sapling variants and excludes Fading from
 * mixed forests, while leaving Ars Elemental's original variants untouched.
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestationRitual", remap = false)
public abstract class ArchwoodForestationRitualMixin {

    @Inject(method = "addFeatures", at = @At("HEAD"), cancellable = true, remap = false)
    private void archwoodGood$handleSelectedAndMixedForests(
            List<IPlaceableFeature> features, CallbackInfo ci) {

        // First determine whether the player selected any registered sapling.
        // Ars Elemental's original five variants must retain their own addFeatures
        // implementation, so only intercept selections belonging to Archwood Good.
        for (ItemStack stack : ((AbstractRitual) (Object) this).getConsumedItems()) {
            ArchwoodVariant selected = ArchwoodVariantsRegistry.get(stack.getItem());
            if (selected == null) {
                continue;
            }

            boolean isArchwoodGoodSapling =
                    stack.is(AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().asItem())
                    || stack.is(AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().asItem())
                    || stack.is(AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem());

            if (!isArchwoodGoodSapling) {
                // This is one of Ars Elemental's original five saplings.
                // Do not cancel: let Ars Elemental generate its original forest.
                return;
            }

            // Archwood Good sapling selected: generate only its corresponding forest.
            features.add(new BonemealFeature(6, 0.4));
            features.add(selected.treeFeature().get());
            features.add(selected.plantFeature().get());
            features.add(selected.podFeature().get());
            features.add(new PlaceableLightFeature(4, 0.1));
            ci.cancel();
            return;
        }

        // No individual registered sapling was selected: generate a mixed forest,
        // excluding Fading while retaining Ars Elemental's other registered variants.
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
