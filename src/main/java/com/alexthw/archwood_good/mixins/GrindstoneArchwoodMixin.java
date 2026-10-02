package com.alexthw.archwood_good.mixins;

import com.alexthw.archwood_good.misc.FadingGrindstone;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class GrindstoneArchwoodMixin {
    @Inject(method = "canGrindstoneRepair", at = @At("HEAD"), cancellable = true)
    private void archwoodGood$allowFadingGrind(CallbackInfoReturnable<Boolean> cir) {
        if (FadingGrindstone.canGrind((ItemStack) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
