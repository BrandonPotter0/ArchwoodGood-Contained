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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class AncientArchwoodExclusionPlacement extends PlacementFilter {
    public static final MapCodec<AncientArchwoodExclusionPlacement> CODEC =
            MapCodec.unit(new AncientArchwoodExclusionPlacement());

    private static final net.minecraft.resources.ResourceKey<Structure> ANCIENT_ARCHWOOD =
            net.minecraft.resources.ResourceKey.create(
                    Registries.STRUCTURE, ArchwoodGood.res("ancient_archwood_tree"));

    /*
     * Placement modifiers can be evaluated many times while a WorldGenRegion is
     * being processed. The old implementation rescanned up to a 13x13 chunk area
     * for every single candidate position. Cache only the small set of Ancient
     * Archwood bounding boxes for the current WorldGenRegion instead.
     *
     * Weak keys are intentional: a generation region can be discarded after the
     * chunk-generation pass, so the cache must not keep old regions alive.
     * The cached value contains only immutable BoundingBox instances and therefore
     * does not retain chunks, levels, or other world objects.
     */
    private static final Map<WorldGenRegion, List<BoundingBox>> REGION_STRUCTURE_CACHE =
            Collections.synchronizedMap(new WeakHashMap<>());

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

        // This placement modifier is an exclusion filter: allow generation only
        // when the candidate position is OUTSIDE the Ancient Archwood exclusion zone.
        return !isWithinAncientExclusion(region, pos, radius);
    }

    /**
     * Checks whether a position is inside the circular exclusion area surrounding
     * an Ancient Archwood Tree.
     *
     * The configured radius is the width of the buffer outside the structure's
     * footprint, not the diameter of the entire tree.
     */
    public static boolean isWithinAncientExclusion(WorldGenRegion region, BlockPos pos, int radius) {
        for (BoundingBox box : getAncientArchwoodBoxes(region, radius)) {
            if (isInsideCircularExclusion(box, pos, radius)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets all Ancient Archwood structure bounding boxes visible to this generation
     * region. This expensive chunk scan is performed once per WorldGenRegion instead
     * of once per tree-placement candidate.
     */
    private static List<BoundingBox> getAncientArchwoodBoxes(WorldGenRegion region, int radius) {
        List<BoundingBox> cached = REGION_STRUCTURE_CACHE.get(region);
        if (cached != null) {
            return cached;
        }

        Structure ancient = region.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .get(ANCIENT_ARCHWOOD.location());
        if (ancient == null) {
            List<BoundingBox> empty = List.of();
            REGION_STRUCTURE_CACHE.put(region, empty);
            return empty;
        }

        // max_distance_from_center for the Ancient Archwood jigsaw structure is 80.
        // Use that bound plus the configured exclusion buffer to find every structure
        // start that could possibly affect a candidate position in this region.
        int search = Math.max(1, (radius + 80 + 15) >> 4);
        ChunkPos center = new ChunkPos(region.getCenter());

        List<BoundingBox> boxes = new ArrayList<>();

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
                if (start != null && start.isValid()) {
                    boxes.add(start.getBoundingBox());
                }
            }
        }

        List<BoundingBox> result = List.copyOf(boxes);
        REGION_STRUCTURE_CACHE.put(region, result);
        return result;
    }

    private static boolean isInsideCircularExclusion(BoundingBox box, BlockPos pos, int radius) {
        if (box.isInside(pos)) {
            return true;
        }

        // Build a true circle around the structure. The configured radius is
        // added to the structure's half-width, giving a rounded outer buffer
        // rather than a square biome-wide exclusion.
        double centerX = (box.minX() + box.maxX()) * 0.5;
        double centerZ = (box.minZ() + box.maxZ()) * 0.5;
        double baseRadius = Math.max(box.getXSpan(), box.getZSpan()) * 0.5;
        double exclusionRadius = baseRadius + radius;

        double dx = (pos.getX() + 0.5) - centerX;
        double dz = (pos.getZ() + 0.5) - centerZ;
        return dx * dx + dz * dz <= exclusionRadius * exclusionRadius;
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
