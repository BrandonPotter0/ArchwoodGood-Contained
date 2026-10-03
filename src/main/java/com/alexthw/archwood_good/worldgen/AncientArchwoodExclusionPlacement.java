package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import com.alexthw.archwood_good.config.AWGConfigCache;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
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
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        if (!AWGConfigCache.ancientArchwoodExclusionEnabled()) return true;

        int radius = AWGConfigCache.ancientArchwoodExclusionRadius();
        if (radius <= 0 || !(context.getLevel() instanceof WorldGenRegion region)) return true;

        Structure ancient = region.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getValue(ANCIENT_ARCHWOOD.location());
        if (ancient == null) return true;

        StructureManager manager = region.getLevel().structureManager().forWorldGenRegion(region);
        ChunkPos center = new ChunkPos(pos);
        int search = Math.max(1, (radius + 80 + 15) >> 4);

        for (int cx = center.x - search; cx <= center.x + search; cx++) {
            for (int cz = center.z - search; cz <= center.z + search; cz++) {
                for (StructureStart start : manager.startsForStructure(
                        new ChunkPos(cx, cz), structure -> structure == ancient)) {
                    if (!start.isValid()) continue;
                    BoundingBox protectedArea = start.getBoundingBox().inflatedBy(radius, 0, radius);
                    if (protectedArea.intersects(pos.getX(), pos.getZ(), pos.getX(), pos.getZ())) return false;
                }
            }
        }
        return true;
    }
}
