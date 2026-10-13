package com.alexthw.archwood_good.mixins;

import com.alexthw.archwood_good.worldgen.AWGBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * In Glacial Archwood Caves only, let the original Minecraft/Ars tree
 * generators consider obstructed trunk and foliage positions valid. Their
 * normal block-placement consumer then replaces the obstructing blocks.
 */
@Mixin(TreeFeature.class)
public abstract class GlacialTreePriorityMixin {
    @Inject(method = "validTreePos", at = @At("HEAD"), cancellable = true, require = 0)
    private static void archwoodGood$allowGlacialTreePlacement(
            LevelSimulatedReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (reader instanceof WorldGenLevel level
                && level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isAirOrLeaves", at = @At("HEAD"), cancellable = true, require = 0)
    private static void archwoodGood$allowGlacialTreeTrunksAndLeaves(
            LevelSimulatedReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (reader instanceof WorldGenLevel level
                && level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) {
            cir.setReturnValue(true);
        }
    }
}
