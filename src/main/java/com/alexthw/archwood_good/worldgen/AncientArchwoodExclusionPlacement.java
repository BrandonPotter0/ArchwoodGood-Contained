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

        return isWithinAncientExclusion(region, pos, radius);
    }

    /**
     * Checks whether a position is inside the circular exclusion area surrounding
     * an Ancient Archwood Tree. The configured radius is the width of the buffer
     * outside the structure's footprint, not the diameter of the entire tree.
     */
    public static boolean isWithinAncientExclusion(WorldGenRegion region, BlockPos pos, int radius) {
        Structure ancient = region.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .get(ANCIENT_ARCHWOOD.location());
        if (ancient == null) return false;

        ChunkPos center = new ChunkPos(pos);
        int search = Math.max(1, (radius + 80 + 15) >> 4);

        /*
         * WorldGenRegion only exposes the bounded set of chunks currently available
         * to this world-generation pass. Do not use StructureManager.startsForStructure
         * here: that method can request chunks outside the current generation region.
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

                BoundingBox box = start.getBoundingBox();

                // Never allow normal trees to generate inside the Ancient Archwood itself.
                if (box.isInside(pos)) {
                    return true;
                }

                // Build a true circle around the structure. The configured radius is
                // added to the structure's half-width, giving a 10-block outer buffer
                // with a rounded/circular boundary rather than a square bounding box.
                double centerX = (box.minX() + box.maxX()) * 0.5;
                double centerZ = (box.minZ() + box.maxZ()) * 0.5;
                double baseRadius = Math.max(box.getXSpan(), box.getZSpan()) * 0.5;
                double exclusionRadius = baseRadius + radius;

                double dx = (pos.getX() + 0.5) - centerX;
                double dz = (pos.getZ() + 0.5) - centerZ;
                if (dx * dx + dz * dz <= exclusionRadius * exclusionRadius) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Used by the Ancient Archwood ground-dressing feature so it never replaces
     * grass that belongs to the Ancient Archwood structure itself.
     */
    public static boolean isInsideAncientStructure(WorldGenRegion region, BlockPos pos) {
        Structure ancient = region.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .get(ANCIENT_ARCHWOOD.location());
        if (ancient == null) return false;

        ChunkPos center = new ChunkPos(pos);
        int search = Math.max(1, (80 + 15) >> 4);

        for (int cx = center.x - search; cx <= center.x + search; cx++) {
            for (int cz = center.z - search; cz <= center.z + search; cz++) {
                ChunkAccess chunk;
                try {
                    chunk = region.getChunk(cx, cz);
                } catch (IllegalStateException ignored) {
                    continue;
                }

                StructureStart start = chunk.getStartForStructure(ancient);
                if (start != null && start.isValid() && start.getBoundingBox().isInside(pos)) {
                    return true;
                }
            }
        }

        return false;
    }
}
