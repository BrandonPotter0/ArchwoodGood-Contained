package com.alexthw.archwood_good.mixins;

import com.alexthw.archwood_good.worldgen.AWGBiomes;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps Magelight glow particles stable in Glacial Archwood Caves.
 * Ars Nouveau's normal LightTile tick transitions the particle colour randomly
 * on every client tick, which can make an icy stored colour look pink.
 */
@Mixin(LightTile.class)
public abstract class GlacialMagelightParticleMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void archwoodGood$useStableGlacialColour(Level level, BlockState state, BlockPos pos, CallbackInfo ci) {
        if (!level.isClientSide || !level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) {
            return;
        }

        LightTile tile = (LightTile) (Object) this;
        if (tile.getColor().getColor() == 65793) {
            ci.cancel();
            return;
        }

        level.addParticle(
                GlowParticleData.createData(tile.getColor(), 0.25f, 0.9f, 36),
                pos.getX() + 0.5 + level.random.nextDouble() * 0.2 - 0.1,
                pos.getY() + 0.5 + level.random.nextDouble() * 0.2 - 0.1,
                pos.getZ() + 0.5 + level.random.nextDouble() * 0.2 - 0.1,
                0, 0, 0);
        ci.cancel();
    }
}
