package com.alexthw.archwood_good.mixins;

import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry;
import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry.ArchwoodVariant;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

/**
 * Keeps Ars Elemental responsible for all forest feature generation.
 *
 * Ars Elemental's canConsumeItem clears selectedSapling whenever it checks a
 * non-sapling augment. Restore the registered sapling from the ritual's actual
 * consumed items immediately before Ars Elemental builds its features.
 * The original five variants and Archwood Good's three variants then use the
 * dependency's own individual-variant branch.
 *
 * For the default mixed forest only, omit Fading from getAll().
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestationRitual", remap = false)
public abstract class ArchwoodForestationRitualMixin {

    @Shadow
    private Item selectedSapling;

    @Inject(method = "addFeatures", at = @At("HEAD"), remap = false)
    private void archwoodGood$restoreSelectedSapling(
            List<?> rawFeatures, CallbackInfo ci) {
        selectedSapling = null;

        for (ItemStack stack : ((AbstractRitual) (Object) this).getConsumedItems()) {
            if (ArchwoodVariantsRegistry.get(stack.getItem()) != null) {
                selectedSapling = stack.getItem();
                return;
            }
        }
    }

    @Redirect(
            method = "addFeatures",
            at = @At(
                    value = "INVOKE",
                    target = "Lalexthw/ars_elemental/common/rituals/forest/ArchwoodVariantsRegistry;getAll()Ljava/util/Collection;"
            ),
            remap = false
    )
    private Collection<ArchwoodVariant> archwoodGood$excludeFadingFromMixedForests() {
        Item fadingSapling = AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem();
        return ArchwoodVariantsRegistry.getAll().stream()
                .filter(variant -> !variant.sapling().equals(fadingSapling))
                .toList();
    }
}
