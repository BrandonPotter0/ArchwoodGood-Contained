package com.alexthw.archwood_good.mixins;

import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry;
import alexthw.ars_elemental.common.rituals.forest.ArchwoodVariantsRegistry.ArchwoodVariant;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

/**
 * Leaves Ars Elemental's original Archwood Forestation ritual logic intact.
 *
 * The only adjustment is to omit Archwood Good's Fading variant from the
 * registry collection used by Ars Elemental when constructing a mixed forest.
 * Individual sapling selection uses ArchwoodVariantsRegistry.get(selectedSapling)
 * and does not call getAll(), so Fading remains individually selectable.
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestationRitual", remap = false)
public abstract class ArchwoodForestationRitualMixin {

    @Redirect(
            method = "addFeatures",
            at = @At(
                    value = "INVOKE",
                    target = "Lalexthw/ars_elemental/common/rituals/forest/ArchwoodVariantsRegistry;getAll()Ljava/util/Collection;"
            ),
            remap = false
    )
    private Collection<ArchwoodVariant> archwoodGood$excludeFadingFromMixedForests() {
        return ArchwoodVariantsRegistry.getAll().stream()
                .filter(variant -> variant.sapling()
                        != AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get().asItem())
                .toList();
    }
}
