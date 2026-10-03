package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import com.alexthw.archwood_good.config.AWGConfigCache;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public final class AncientArchwoodExclusionPlacement extends PlacementFilter {
    public static final MapCodec<AncientArchwoodExclusionPlacement> CODEC =
            MapCodec.unit(new AncientArchwoodExclusionPlacement());

    private static final net.minecraft.resources.ResourceKey<Structure> ANCIENT_ARCHWOOD =
            net.minecraft.resources.ResourceKey.create(
                    Registries.STRUCTURE, ArchwoodGood.res("ancient_archwood_tree"));

    private AncientArchwoodExclusionPlacement() {}

    @Override
    public PlacementModifierType<?> type() {
        return AWGFeatures.ANCIENT_ARCHWOOD_EXCLUSION.get();
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        if (!AWGConfigCache.ancientArchwoodExclusionEnabled()) return true;

        int radius = AWGConfigCache.ancientArchwoodExclusionRadius();
        if (radius <= 0 || !(context.getLevel() instanceof WorldGenRegion region)) return true;

        Structure ancient = region.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .get(ANCIENT_ARCHWOOD.location());
        if (ancient == null) return true;

        ChunkPos center = new ChunkPos(pos);
        int search = Math.max(1, (radius + 80 + 15) >> 4);

        /*
         * WorldGenRegion only exposes the bounded set of chunks currently available
         * to this world-generation pass. Do not use StructureManager.startsForStructure
         * here: that method asks the LevelReader for the requested chunk, which can
         * escape the current WorldGenRegion and crash with "Requested chunk unavailable
         * during world generation".
         *
         * Read structure starts directly from already-available ChunkAccess instances.
         * If a candidate chunk is outside the current generation region, skip it rather
         * than forcing an additional chunk request.
         */
        for (int cx = center.x - search; cx <= center.x + search; cx++) {
            for (int cz = center.z - search; cz <= center.z + search; cz++) {
                ChunkAccess chunk;
                try {
                    chunk = region.getChunk(cx, cz);
                } catch (IllegalStateException ignored) {
                    continue;
                }

                StructureStart start = chunk.getStartForStructure(ancient);
                if (start == null || !start.isValid()) continue;

                BoundingBox protectedArea = start.getBoundingBox().inflatedBy(radius, 0, radius);
                if (protectedArea.intersects(pos.getX(), pos.getZ(), pos.getX(), pos.getZ())) {
                    return false;
                }
            }
        }

        return true;
    }
}
