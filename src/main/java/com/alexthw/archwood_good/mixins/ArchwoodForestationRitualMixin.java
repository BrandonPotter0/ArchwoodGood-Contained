package com.alexthw.archwood_good.mixins;

import alexthw.ars_elemental.common.rituals.forest.ArchpodFeature;
import alexthw.ars_elemental.common.rituals.forest.PlaceableLightFeature;
import alexthw.ars_elemental.registry.ModItems;
import com.alexthw.archwood_good.registry.AWGBlockRegistry;
import com.hollingsworth.arsnouveau.api.ritual.features.BonemealFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.IPlaceableFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.PlaceBlockFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.RandomTreeFeature;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SaplingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Adds Archwood Good's Orange (Dawning) and White (Blinding) saplings to
 * Ars Elemental's Archwood Forestation ritual without changing Ars Elemental.
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestationRitual", remap = false)
public abstract class ArchwoodForestationRitualMixin {

    private static final int ARCHWOOD_GOOD$DAWNING = 5;
    private static final int ARCHWOOD_GOOD$BLINDING = 6;

    @Shadow(remap = false)
    int biomeType;

    @Inject(method = "canConsumeItem", at = @At("RETURN"), cancellable = true, remap = false)
    private void archwoodGood$acceptAdditionalSaplings(
            ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof BlockItem blockItem)
                || !(blockItem.getBlock() instanceof SaplingBlock)) {
            return;
        }

        if (blockItem.getBlock() == AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get()) {
            biomeType = ARCHWOOD_GOOD$DAWNING;
            cir.setReturnValue(true);
        } else if (blockItem.getBlock() == AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get()) {
            biomeType = ARCHWOOD_GOOD$BLINDING;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "addFeatures", at = @At("HEAD"), cancellable = true, remap = false)
    private void archwoodGood$addDawningAndBlindingFeatures(
            List<IPlaceableFeature> features, CallbackInfo ci) {
        if (biomeType == -1) {
            // Preserve Ars Elemental's mixed forest, adding both Archwood Good trees
            // to the same RandomTreeFeature so all seven variants share one selection pool.
            features.add(new BonemealFeature(6, 0.4));
            features.add(new RandomTreeFeature(List.of(
                    BlockRegistry.BLAZING_SAPLING.defaultBlockState(),
                    BlockRegistry.CASCADING_SAPLING.defaultBlockState(),
                    BlockRegistry.VEXING_SAPLING.defaultBlockState(),
                    BlockRegistry.FLOURISHING_SAPLING.defaultBlockState(),
                    ModItems.FLASHING_SAPLING.get().defaultBlockState(),
                    AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().defaultBlockState(),
                    AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().defaultBlockState()
            ), 8, 0.8));
            features.add(new PlaceBlockFeature(
                    2, 0.1, BlockRegistry.SOURCEBERRY_BUSH.get()::defaultBlockState));
            features.add(new ArchpodFeature(
                    0.5, 0.1, BlockRegistry.BOMBEGRANTE_POD.defaultBlockState()));
            features.add(new ArchpodFeature(
                    0.5, 0.1, BlockRegistry.FROSTAYA_POD.defaultBlockState()));
            features.add(new ArchpodFeature(
                    0.5, 0.1, BlockRegistry.BASTION_POD.defaultBlockState()));
            features.add(new ArchpodFeature(
                    0.5, 0.1, BlockRegistry.MENDOSTEEN_POD.defaultBlockState()));
            features.add(new ArchpodFeature(
                    0.5, 0.1, ModItems.FLASHING_POD.get().defaultBlockState()));
            features.add(new ArchpodFeature(
                    0.5, 0.1, AWGBlockRegistry.DAWNBERRY_POD.get().defaultBlockState()));
            features.add(new ArchpodFeature(
                    0.5, 0.1, AWGBlockRegistry.LIGHTCHEE_POD.get().defaultBlockState()));
            features.add(new PlaceableLightFeature(4, 0.1));
            ci.cancel();
            return;
        }

        if (biomeType != ARCHWOOD_GOOD$DAWNING && biomeType != ARCHWOOD_GOOD$BLINDING) {
            return;
        }

        features.add(new BonemealFeature(6, 0.4));

        if (biomeType == ARCHWOOD_GOOD$DAWNING) {
            features.add(new RandomTreeFeature(
                    List.of(AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get().defaultBlockState()), 8, 0.8));
            features.add(new ArchpodFeature(
                    1, 0.15, AWGBlockRegistry.DAWNBERRY_POD.get().defaultBlockState()));
        } else {
            features.add(new RandomTreeFeature(
                    List.of(AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get().defaultBlockState()), 8, 0.8));
            features.add(new ArchpodFeature(
                    1, 0.15, AWGBlockRegistry.LIGHTCHEE_POD.get().defaultBlockState()));
        }

        features.add(new PlaceableLightFeature(4, 0.1));
        ci.cancel();
    }
}
