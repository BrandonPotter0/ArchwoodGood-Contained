package com.alexthw.archwood_good.mixins;

import com.alexthw.archwood_good.worldgen.AWGBiomes;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forces stable icy Magelight particles in Glacial Archwood Caves.
 *
 * Ars Nouveau 5.13.x ticks LightTile through ITickable.tick(Level, BlockState,
 * BlockPos), not a no-argument BlockEntity.tick(). Replace the client particle
 * emission only in the Glacial Archwood Caves; all other biomes keep the
 * original Ars Nouveau behaviour.
 */
@Mixin(LightTile.class)
public abstract class GlacialMagelightParticleMixin {
    private static final ParticleColor ARCHWOOD_GOOD$ICE_WHITE = new ParticleColor(240, 248, 255);
    private static final ParticleColor ARCHWOOD_GOOD$ICE_BLUE = new ParticleColor(173, 216, 230);

    @Inject(
            method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 1
    )
    private void archwoodGood$useStableGlacialColour(Level level, BlockState state, BlockPos pos, CallbackInfo ci) {
        if (!level.isClientSide || !level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) {
            return;
        }

        // Stable per-position variation: each light remains blue or icy white.
        ParticleColor icyColor = Math.floorMod(pos.asLong(), 4L) == 0L
                ? ARCHWOOD_GOOD$ICE_WHITE
                : ARCHWOOD_GOOD$ICE_BLUE;

        level.addParticle(
                GlowParticleData.createData(icyColor, 0.25f, 0.9f, 36),
                pos.getX() + 0.5 + level.random.nextDouble() * 0.2 - 0.1,
                pos.getY() + 0.5 + level.random.nextDouble() * 0.2 - 0.1,
                pos.getZ() + 0.5 + level.random.nextDouble() * 0.2 - 0.1,
                0, 0, 0);
        ci.cancel();
    }
}
