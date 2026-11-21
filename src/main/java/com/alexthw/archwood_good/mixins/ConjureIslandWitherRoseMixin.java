package com.alexthw.archwood_good.mixins;

import alexthw.ars_elemental.common.rituals.forest.ArchfruitPod;
import com.alexthw.archwood_good.worldgen.AWGBiomes;
import com.hollingsworth.arsnouveau.api.ritual.ConjureBiomeRitual;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds the vanilla Wither Rose as the special offering for Fading Archwood
 * in Ars Elemental's Conjure Island: Archwood Forest ritual.
 *
 * A Wither Rose and an Archfruit pod are mutually exclusive biome offerings;
 * source gems and the ritual's other normal augmentations remain unaffected.
 */
@Mixin(targets = "alexthw.ars_elemental.common.rituals.forest.ArchwoodForestRitual", remap = false)
public abstract class ConjureIslandWitherRoseMixin {

    @Inject(method = "canConsumeItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void archwoodGood$allowWitherRoseOffering(
            ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        boolean isWitherRose = stack.is(Items.WITHER_ROSE);
        boolean isArchfruitPod = archwoodGood$isArchfruitPod(stack);

        if (!isWitherRose && !isArchfruitPod) {
            return;
        }

        boolean offeringAlreadyConsumed = ((AbstractRitual) (Object) this)
                .getConsumedItems()
                .stream()
                .anyMatch(consumed -> consumed.is(Items.WITHER_ROSE)
                        || archwoodGood$isArchfruitPod(consumed));

        cir.setReturnValue(!offeringAlreadyConsumed);
    }

    @Inject(method = "onStart", at = @At("TAIL"), remap = false)
    private void archwoodGood$selectFadingBiome(
            @Nullable Player player, CallbackInfo ci) {
        boolean hasWitherRose = ((AbstractRitual) (Object) this)
                .getConsumedItems()
                .stream()
                .anyMatch(stack -> stack.is(Items.WITHER_ROSE));

        if (hasWitherRose) {
            ((ConjureBiomeRitual) (Object) this).biome = AWGBiomes.FADING_FOREST_KEY;
        }
    }

    private static boolean archwoodGood$isArchfruitPod(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ArchfruitPod;
    }
}
