package com.alexthw.archwood_good.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Glacial-only tree generator. Logs and leaves deliberately replace terrain
 * in their footprint so cave walls and ceilings cannot veto a tree.
 */
public class GlacialPriorityTreeFeature extends Feature<NoneFeatureConfiguration> {
    public GlacialPriorityTreeFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Find a solid footing beneath the candidate, even if the candidate
        // itself landed inside a wall. Keep the search bounded for worldgen.
        BlockPos.MutableBlockPos scan = origin.mutable();
        boolean foundFloor = false;
        for (int i = 0; i <= 32 && scan.getY() > level.getMinBuildHeight() + 2; i++) {
            if (level.getBlockState(scan.below()).isSolid()) {
                foundFloor = true;
                break;
            }
            scan.move(Direction.DOWN);
        }
        if (!foundFloor) return false;
        BlockPos base = scan.immutable();
        int height = 8 + random.nextInt(3);
        boolean white = random.nextBoolean();
        Block logBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(
                white ? "archwood_good:white_archwood_log" : "ars_nouveau:blue_archwood_log"));
        Block leafBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(
                white ? "archwood_good:white_archwood_leaves" : "ars_nouveau:blue_archwood_leaves"));
        if (logBlock == Blocks.AIR || leafBlock == Blocks.AIR) return false;

        Direction lean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        List<BlockPos> trunk = new ArrayList<>();
        int x = base.getX(), z = base.getZ();
        for (int i = 0; i < height; i++) {
            trunk.add(new BlockPos(x, base.getY() + i, z));
            if (i > 0 && i % 3 == 0 && random.nextFloat() < 0.35f) {
                x += lean.getStepX();
                z += lean.getStepZ();
            }
        }

        BlockState log = logBlock.defaultBlockState();
        if (log.hasProperty(RotatedPillarBlock.AXIS))
            log = log.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState leaves = leafBlock.defaultBlockState();
        if (leaves.hasProperty(BlockStateProperties.DISTANCE))
            leaves = leaves.setValue(BlockStateProperties.DISTANCE, 1);
        if (leaves.hasProperty(BlockStateProperties.PERSISTENT))
            leaves = leaves.setValue(BlockStateProperties.PERSISTENT, true);
        if (leaves.hasProperty(BlockStateProperties.WATERLOGGED))
            leaves = leaves.setValue(BlockStateProperties.WATERLOGGED, false);

        Set<BlockPos> trunkPositions = new HashSet<>(trunk);
        BlockPos crown = trunk.get(trunk.size() - 1);
        int radius = 3;
        // Place foliage first, intentionally overwriting any blocks in its way.
        for (int dx = -radius; dx <= radius; dx++)
            for (int dz = -radius; dz <= radius; dz++)
                for (int dy = -1; dy <= 2; dy++) {
                    if (dx * dx + dz * dz + dy * dy > radius * radius) continue;
                    BlockPos p = crown.offset(dx, dy, dz);
                    if (!trunkPositions.contains(p) && p.getY() < level.getMaxBuildHeight())
                        level.setBlock(p, leaves, 2);
                }
        // Logs have highest priority and overwrite terrain, leaves, and decorations.
        for (BlockPos p : trunk)
            if (p.getY() < level.getMaxBuildHeight()) level.setBlock(p, log, 2);
        level.setBlock(base.below(), Blocks.DIRT.defaultBlockState(), 2);
        return true;
    }
}