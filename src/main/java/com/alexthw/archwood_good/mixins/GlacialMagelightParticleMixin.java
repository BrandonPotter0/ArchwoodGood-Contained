package com.alexthw.archwood_good.mixins;

import com.alexthw.archwood_good.worldgen.AWGBiomes;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forces stable icy Magelight particles in Glacial Archwood Caves.
 *
 * LightTile's normal tick owns the pink/random particle timeline. We replace
 * that tick only on the client and only in this biome, so its normal behaviour
 * is untouched everywhere else. This does not depend on a server-side colour
 * update or on the block entity having been available during world generation.
 */
@Mixin(LightTile.class)
public abstract class GlacialMagelightParticleMixin {
    private static final ParticleColor ARCHWOOD_GOOD$ICE_WHITE = new ParticleColor(240, 248, 255);
    private static final ParticleColor ARCHWOOD_GOOD$ICE_BLUE = new ParticleColor(173, 216, 230);

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true, require = 1)
    private void archwoodGood$useStableGlacialColour(CallbackInfo ci) {
        LightTile tile = (LightTile) (Object) this;
        Level level = tile.getLevel();
        if (level == null || !level.isClientSide) {
            return;
        }

        BlockPos pos = tile.getBlockPos();
        if (!level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) {
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
