package com.alexthw.archwood_good.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
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
import java.util.List;

public class GlacialPriorityTreeFeature extends Feature<NoneFeatureConfiguration> {
    public GlacialPriorityTreeFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        if (!level.getBlockState(origin).isAir()) return false;
        BlockPos floor = origin.below();
        if (!level.getBlockState(floor).isSolid()) return false;

        int height = 8 + random.nextInt(3);
        Direction lean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        List<BlockPos> trunk = new ArrayList<>();
        int x = origin.getX(), y = origin.getY(), z = origin.getZ();
        for (int i = 0; i < height; i++) {
            BlockPos p = new BlockPos(x, y + i, z);
            trunk.add(p);
            trunk.add(p.relative(lean.getClockWise()));
            if (i > 0 && i % 2 == 0 && random.nextFloat() < 0.75f) {
                x += lean.getStepX(); z += lean.getStepZ();
            }
            if (i > 2 && random.nextFloat() < 0.35f) {
                Direction kink = random.nextBoolean() ? lean.getClockWise() : lean.getCounterClockWise();
                x += kink.getStepX(); z += kink.getStepZ();
            }
        }
        int minX = origin.getX(), maxX = origin.getX(), minZ = origin.getZ(), maxZ = origin.getZ();
        for (BlockPos p : trunk) {
            minX = Math.min(minX, p.getX() - 3); maxX = Math.max(maxX, p.getX() + 3);
            minZ = Math.min(minZ, p.getZ() - 3); maxZ = Math.max(maxZ, p.getZ() + 3);
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int cx = minX; cx <= maxX; cx++)
            for (int cz = minZ; cz <= maxZ; cz++)
                for (int cy = origin.getY(); cy <= origin.getY() + height + 3; cy++) {
                    cursor.set(cx, cy, cz);
                    if (canCarve(level.getBlockState(cursor))) level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 2);
                }
        level.setBlock(floor, Blocks.DIRT.defaultBlockState(), 2);
        level.setBlock(floor.relative(lean.getClockWise()), Blocks.DIRT.defaultBlockState(), 2);

        boolean white = random.nextBoolean();
        Block logBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(white ? "archwood_good:white_archwood_log" : "ars_nouveau:blue_archwood_log"));
        Block leafBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(white ? "archwood_good:white_archwood_leaves" : "ars_nouveau:blue_archwood_leaves"));
        if (logBlock == Blocks.AIR || leafBlock == Blocks.AIR) return false;
        BlockState logY = withAxis(logBlock.defaultBlockState(), Direction.Axis.Y);
        BlockState leaves = leafState(leafBlock);
        for (BlockPos p : trunk) level.setBlock(p, logY, 2);
        BlockPos crown = trunk.get(trunk.size() - 1);
        int leafR = 3;
        for (int dx = -leafR; dx <= leafR; dx++)
            for (int dz = -leafR; dz <= leafR; dz++)
                for (int dy = -1; dy <= 2; dy++) {
                    if (dx * dx + dz * dz + dy * dy > leafR * leafR) continue;
                    BlockPos p = crown.offset(dx, dy, dz);
                    if (trunk.contains(p)) continue;
                    BlockState cur = level.getBlockState(p);
                    if (cur.isAir() || canCarve(cur)) level.setBlock(p, leaves, 2);
                }
        for (BlockPos p : trunk) level.setBlock(p, logY, 2);
        return true;
    }

    private static BlockState withAxis(BlockState state, Direction.Axis axis) {
        return state.hasProperty(RotatedPillarBlock.AXIS) ? state.setValue(RotatedPillarBlock.AXIS, axis) : state;
    }
    private static BlockState leafState(Block leafBlock) {
        BlockState leaves = leafBlock.defaultBlockState();
        if (leaves.hasProperty(BlockStateProperties.DISTANCE)) leaves = leaves.setValue(BlockStateProperties.DISTANCE, 1);
        if (leaves.hasProperty(BlockStateProperties.PERSISTENT)) leaves = leaves.setValue(BlockStateProperties.PERSISTENT, false);
        if (leaves.hasProperty(BlockStateProperties.WATERLOGGED)) leaves = leaves.setValue(BlockStateProperties.WATERLOGGED, false);
        return leaves;
    }
    private static boolean canCarve(BlockState state) {
        if (state.isAir()) return false;
        Block b = state.getBlock();
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.ICE) || state.is(BlockTags.SNOW)
                || b == Blocks.SNOW_BLOCK || b == Blocks.SNOW || b == Blocks.PACKED_ICE || b == Blocks.BLUE_ICE
                || b == Blocks.ICE || b == Blocks.DIRT || b == Blocks.GRASS_BLOCK || b == Blocks.GRAVEL
                || b == Blocks.CALCITE || b == Blocks.TUFF || b == Blocks.COBBLESTONE || b == Blocks.COBBLED_DEEPSLATE;
    }
}
