package com.alexthw.archwood_good.worldgen;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.common.block.ArchfruitPod;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

/** Original Archwood log/branch pattern with a 3x3 trunk. */
public class GiantMagicTrunkPlacer extends TrunkPlacer {
    public final boolean isWorldGen;
    public final ResourceLocation podID;

    public static final MapCodec<GiantMagicTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.intRange(0, 64).fieldOf("base_height").forGetter(p -> p.baseHeight),
                    Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter(p -> p.heightRandA),
                    Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter(p -> p.heightRandB),
                    Codec.BOOL.fieldOf("isWorldGen").forGetter(p -> p.isWorldGen),
                    Codec.STRING.fieldOf("podID").forGetter(p -> p.podID.toString())
            ).apply(builder, GiantMagicTrunkPlacer::new));

    public GiantMagicTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, boolean isWorldGen, String podName) {
        super(baseHeight, heightRandA, heightRandB);
        this.isWorldGen = isWorldGen;
        ResourceLocation parsed = ResourceLocation.tryParse(podName);
        this.podID = parsed == null ? ResourceLocation.parse("minecraft:air") : parsed;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return AWGFeatures.GIANT_MAGIC_TRUNK.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> consumer,
                                                           RandomSource rand, int foliageHeight, BlockPos pos, TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos below = pos.below();
        for (int dx = 0; dx < 3; dx++) {
            for (int dz = 0; dz < 3; dz++) {
                BlockPos d = below.offset(dx, 0, dz);
                if ((dx == 0 && dz == 0) || world.isStateAtPosition(d, s -> s.is(BlockTags.DIRT) || s.is(Blocks.FARMLAND))) {
                    TrunkPlacer.setDirtAt(world, consumer, rand, d, config);
                }
            }
        }

        int numBranches = 0;
        int lastBranch = 0;
        boolean northB = rand.nextFloat() >= 0.5f;
        boolean southB = rand.nextFloat() >= 0.5f;
        boolean eastB = rand.nextFloat() >= 0.5f;
        boolean westB = rand.nextFloat() >= 0.5f;
        int ox = 0;
        int oz = 0;
        int leanX = rand.nextBoolean() ? 1 : -1;
        int leanZ = rand.nextBoolean() ? 1 : -1;

        for (int i = 0; i < foliageHeight; i++) {
            // Wander / lean so the 3x3 column is not a perfect chimney.
            if (i >= 2 && i < foliageHeight - 2) {
                if (rand.nextFloat() < 0.45f) {
                    ox += rand.nextBoolean() ? leanX : (rand.nextFloat() < 0.35f ? -leanX : 0);
                }
                if (rand.nextFloat() < 0.45f) {
                    oz += rand.nextBoolean() ? leanZ : (rand.nextFloat() < 0.35f ? -leanZ : 0);
                }
                ox = Math.max(-2, Math.min(2, ox));
                oz = Math.max(-2, Math.min(2, oz));
                if (rand.nextFloat() < 0.2f) leanX = -leanX;
                if (rand.nextFloat() < 0.2f) leanZ = -leanZ;
            }
            BlockPos layer = pos.offset(ox, i, oz);
            for (int dx = 0; dx < 3; dx++) {
                for (int dz = 0; dz < 3; dz++) {
                    boolean corner = (dx != 1 && dz != 1);
                    // Sometimes drop a corner so the silhouette breaks.
                    if (corner && i > 1 && i < foliageHeight - 2 && rand.nextFloat() < 0.18f) {
                        continue;
                    }
                    BlockPos p = layer.offset(dx, 0, dz);
                    if (TreeFeature.isAirOrLeaves(world, p)) {
                        placeLog(world, consumer, rand, p, config);
                    }
                }
            }
            // Occasional bulge on a face.
            if (i > 1 && i < foliageHeight - 2 && rand.nextFloat() < 0.4f) {
                Direction bulge = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
                BlockPos extra = layer.offset(1, 0, 1).relative(bulge, 2);
                if (TreeFeature.isAirOrLeaves(world, extra)) {
                    placeLog(world, consumer, rand, extra, config);
                }
            }

            if (i == 0) {
                addRoots(world, rand, pos.west(), consumer, config, new Direction[]{Direction.NORTH, Direction.WEST});
                addRoots(world, rand, pos.south(3), consumer, config, new Direction[]{Direction.SOUTH, Direction.WEST});
                addRoots(world, rand, pos.south().west(), consumer, config, new Direction[]{Direction.WEST});
                addRoots(world, rand, pos.south(3).east(2), consumer, config, new Direction[]{Direction.EAST, Direction.SOUTH});
                addRoots(world, rand, pos.east(3), consumer, config, new Direction[]{Direction.EAST, Direction.NORTH});
                addRoots(world, rand, pos.east(3).south(), consumer, config, new Direction[]{Direction.EAST});
                addRoots(world, rand, pos.east().north(), consumer, config, new Direction[]{Direction.NORTH});
                addRoots(world, rand, pos.north(), consumer, config, new Direction[]{Direction.NORTH, Direction.EAST});
            }

            if (i > 1 && i > lastBranch) {
                BlockPos branchBase = pos.offset(ox, 0, oz);
                if (northB) {
                    addBranch(world, branchBase.east(), i, Direction.NORTH, rand, config, consumer);
                    lastBranch = i; numBranches++; northB = false;
                } else if (southB) {
                    addBranch(world, branchBase.south(2).east(), i, Direction.SOUTH, rand, config, consumer);
                    lastBranch = i; numBranches++; southB = false;
                } else if (eastB) {
                    addBranch(world, branchBase.east(2).south(), i, Direction.EAST, rand, config, consumer);
                    lastBranch = i; numBranches++; eastB = false;
                } else if (westB) {
                    addBranch(world, branchBase.south(), i, Direction.WEST, rand, config, consumer);
                    lastBranch = i; numBranches++; westB = false;
                } else if (numBranches == 0) {
                    addBranch(world, branchBase.east(), i, Direction.NORTH, rand, config, consumer);
                    addBranch(world, branchBase.south(2).east(), i, Direction.SOUTH, rand, config, consumer);
                    lastBranch = i; numBranches += 2;
                }
            }

            if (i == foliageHeight - 2) {
                placeOriginalCanopy(world, pos.offset(ox + 1, 0, oz + 1), i, rand, config, consumer);
            }

            if (isWorldGen && i >= 2 && i < foliageHeight - 2) {
                addPod(world, layer, rand, consumer, new Direction[]{Direction.NORTH, Direction.WEST});
                addPod(world, layer.east(2), rand, consumer, new Direction[]{Direction.NORTH, Direction.EAST});
                addPod(world, layer.south(2), rand, consumer, new Direction[]{Direction.SOUTH, Direction.WEST});
                addPod(world, layer.south(2).east(2), rand, consumer, new Direction[]{Direction.SOUTH, Direction.EAST});
            }
        }

        list.add(new FoliagePlacer.FoliageAttachment(pos.offset(1, foliageHeight - 1, 1), 0, true));
        return list;
    }

    /** Wider canopy than the original MagicTrunkPlacer lines, centered on the 3x3 trunk. */
    private void placeOriginalCanopy(LevelSimulatedReader world, BlockPos pos, int i, RandomSource rand,
                                     TreeConfiguration config, BiConsumer<BlockPos, BlockState> consumer) {
        float leafChance = 0.22f;
        fillCrown(world, pos.above(i + 1), 5, rand, config, consumer);
        fillCrown(world, pos.above(i + 2), 6, rand, config, consumer);
        fillCrown(world, pos.above(i + 3), 3, rand, config, consumer);
        addLineLeaves(world, pos.north(5).above(i), Direction.NORTH, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.north(5).above(i + 1), Direction.NORTH, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.north(4).above(i - 1), Direction.NORTH, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.north(4).above(i), Direction.NORTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.north(4).above(i + 1), Direction.NORTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.north(2).above(i + 1), Direction.NORTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.north(1).above(i + 1), Direction.NORTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.north(2).above(i + 2), Direction.NORTH, 6, rand, config, consumer);

        addLineLeaves(world, pos.east(6).above(i), Direction.EAST, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.east(6).above(i + 1), Direction.EAST, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.east(5).above(i - 1), Direction.EAST, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.east(5).above(i), Direction.EAST, 8, rand, config, consumer);
        addLineLeaves(world, pos.east(5).above(i + 1), Direction.EAST, 8, rand, config, consumer);
        addLineLeaves(world, pos.east(3).above(i + 1), Direction.EAST, 8, rand, config, consumer);
        addLineLeaves(world, pos.east(2).above(i + 1), Direction.EAST, 8, rand, config, consumer);
        addLineLeaves(world, pos.east(3).above(i + 2), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(2).above(i + 2), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(1).above(i + 2), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(0).above(i + 2), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(-1).above(i + 2), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(-2).above(i + 2), Direction.EAST, 6, rand, config, consumer);

        addLineLeaves(world, pos.west(5).south().above(i), Direction.WEST, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.west(5).south().above(i + 1), Direction.WEST, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.west(4).south().above(i - 1), Direction.WEST, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.west(4).south().above(i), Direction.WEST, 8, rand, config, consumer);
        addLineLeaves(world, pos.west(4).south().above(i + 1), Direction.WEST, 8, rand, config, consumer);
        addLineLeaves(world, pos.west(2).south().above(i + 1), Direction.WEST, 8, rand, config, consumer);
        addLineLeaves(world, pos.west(1).south().above(i + 1), Direction.WEST, 8, rand, config, consumer);
        addLineLeaves(world, pos.west(2).south().above(i + 2), Direction.WEST, 6, rand, config, consumer);

        addLineLeaves(world, pos.south(5).east().above(i), Direction.SOUTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.south(5).east().above(i + 1), Direction.SOUTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.south(6).east().above(i), Direction.SOUTH, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.south(6).east().above(i + 1), Direction.SOUTH, 8, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.south(3).east().above(i + 1), Direction.SOUTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.south(2).east().above(i + 1), Direction.SOUTH, 8, rand, config, consumer);
        addLineLeaves(world, pos.south(3).east().above(i + 2), Direction.SOUTH, 6, rand, config, consumer);

        addLineLeaves(world, pos.east(2).above(i + 3), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(1).above(i + 3), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(0).above(i + 3), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(-1).above(i + 3), Direction.EAST, 6, rand, config, consumer);
        addLineLeaves(world, pos.east(2).above(i + 4), Direction.EAST, 6, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.east(1).above(i + 4), Direction.EAST, 6, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.east(0).above(i + 4), Direction.EAST, 6, rand, config, leafChance, consumer);
        addLineLeaves(world, pos.east(-1).above(i + 4), Direction.EAST, 6, rand, config, leafChance, consumer);
    }


    /** Solid-ish disk of leaves so the giant crown is fuller, not just thin lines. */
    private void fillCrown(LevelSimulatedReader world, BlockPos center, int radius, RandomSource rand,
                           TreeConfiguration config, BiConsumer<BlockPos, BlockState> consumer) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                if (Math.abs(dx) == radius && Math.abs(dz) == radius && rand.nextFloat() < 0.4f) continue;
                BlockPos p = center.offset(dx, 0, dz);
                if (TreeFeature.validTreePos(world, p)) {
                    setBlock(world, p, config.foliageProvider.getState(rand, p), consumer);
                }
            }
        }
    }

    public void addPod(LevelSimulatedReader world, BlockPos pos, RandomSource random, BiConsumer<BlockPos, BlockState> consumer, Direction[] validDirs) {
        if (random.nextDouble() > 0.12) return;
        Direction d = validDirs[random.nextInt(validDirs.length)];
        int age = random.nextIntBetweenInclusive(0, 2);
        if (!world.isStateAtPosition(pos.relative(d), BlockBehaviour.BlockStateBase::isAir)) return;
        BlockState pod = BuiltInRegistries.BLOCK.get(podID).defaultBlockState();
        if (pod.isAir()) return;
        if (pod.hasProperty(HorizontalDirectionalBlock.FACING)) pod = pod.setValue(HorizontalDirectionalBlock.FACING, d.getOpposite());
        if (pod.hasProperty(SconceBlock.LIGHT_LEVEL)) pod = pod.setValue(SconceBlock.LIGHT_LEVEL, 6 + age);
        if (pod.hasProperty(ArchfruitPod.AGE)) pod = pod.setValue(ArchfruitPod.AGE, age);
        setBlock(world, pos.relative(d), pod, consumer);
    }

    public void addBranch(LevelSimulatedReader world, BlockPos pos, int height, Direction d, RandomSource random,
                          TreeConfiguration config, BiConsumer<BlockPos, BlockState> consumer) {
        pos = pos.above(height);
        addLog(world, pos.relative(d), random, config, consumer);
        addLog(world, pos.relative(d).above(1), random, config, consumer);
        addLog(world, pos.relative(d).above(2), random, config, consumer);
        addLog(world, pos.relative(d, 2).above(2), random, config, consumer);
        addLog(world, pos.relative(d, 3).above(2), random, config, consumer);
        addLog(world, pos.relative(d, 3).above(1), random, config, consumer);

        addLineLeaves(world, pos.relative(d).above(1), d, 5, random, config, consumer);
        addLineLeaves(world, pos.relative(d).above(2), d, 5, random, config, consumer);
        addLineLeaves(world, pos.relative(d).above(3), d, 5, random, config, consumer);
        for (int j = 1; j < 4; j++) {
            addLineLeaves(world, pos.relative(d, j).above(3), d, 5, random, config, consumer);
            addLineLeaves(world, pos.relative(d, j).above(2), d, 5, random, config, consumer);
            addLineLeaves(world, pos.relative(d, j).above(4), d, 5, random, config, 0.22f, consumer);
        }
        for (int i = 0; i < 2; i++) {
            addHollowLine(world, pos.relative(d, 2 + i).above(1), d, 2, random, config, consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), d, 2, random, config, consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(1), d, 3, random, config, 0.1f, consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), d, 3, random, config, 0.1f, consumer);
        }
        addLineLeaves(world, pos.relative(d, 4).above(1), d, 3, random, config, consumer);
        addLineLeaves(world, pos.relative(d, 4).above(2), d, 3, random, config, consumer);
        addLineLeaves(world, pos.relative(d, 5).above(1), d, 3, random, config, 0.1f, consumer);
        addLineLeaves(world, pos.relative(d, 5).above(2), d, 3, random, config, 0.1f, consumer);
    }

    public boolean addLog(LevelSimulatedReader world, BlockPos pos, RandomSource random, TreeConfiguration config, BiConsumer<BlockPos, BlockState> consumer) {
        if (TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos, config.trunkProvider.getState(random, pos), consumer);
            return true;
        }
        return false;
    }

    public void addHollowLine(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand,
                              TreeConfiguration config, BiConsumer<BlockPos, BlockState> consumer) {
        addHollowLine(world, pos, d, length, rand, config, 1.0f, consumer);
    }

    public void addHollowLine(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand,
                              TreeConfiguration config, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();
        if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, length))) {
            setBlock(world, pos.relative(left, length), config.foliageProvider.getState(rand, pos.relative(left, length)), consumer);
        }
        if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(right, length))) {
            setBlock(world, pos.relative(right, length), config.foliageProvider.getState(rand, pos.relative(right, length)), consumer);
        }
    }

    public void addLineLeaves(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand,
                              TreeConfiguration config, BiConsumer<BlockPos, BlockState> consumer) {
        addLineLeaves(world, pos, d, length, rand, config, 1.0f, consumer);
    }

    public void addLineLeaves(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand,
                              TreeConfiguration config, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        if (length % 2 == 0) addLineLeavesEven(world, pos, d, length, rand, config, chance, consumer);
        else addLineLeavesOdd(world, pos, d, length, rand, config, chance, consumer);
    }

    public void addLineLeavesEven(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand,
                                  TreeConfiguration config, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        Direction left = d.getClockWise();
        for (int i = 0; i < length; i++) {
            BlockPos p = pos.relative(left, i - length / 3);
            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, p)) {
                setBlock(world, p, config.foliageProvider.getState(rand, p), consumer);
            }
        }
    }

    public void addLineLeavesOdd(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand,
                                 TreeConfiguration config, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();
        length += 2;
        for (int i = 0; i < (length - 1) / 2; i++) {
            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, i))) {
                setBlock(world, pos.relative(left, i), config.foliageProvider.getState(rand, pos.relative(left, i)), consumer);
            }
            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(right, i))) {
                setBlock(world, pos.relative(right, i), config.foliageProvider.getState(rand, pos.relative(right, i)), consumer);
            }
        }
    }

    public boolean addRoots(LevelSimulatedReader world, RandomSource rand, BlockPos pos, BiConsumer<BlockPos, BlockState> consumer,
                            TreeConfiguration config, Direction[] extendedDirs) {
        BlockState state = config.trunkProvider.getState(rand, pos);
        if (rand.nextDouble() < 0.75 && TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos, state, consumer);
            if (isWorldGen) {
                for (int i = 0; i < 2; i++) {
                    if (TreeFeature.validTreePos(world, pos.below())) {
                        pos = pos.below();
                        setBlock(world, pos, state, consumer);
                    } else break;
                }
                for (Direction d : extendedDirs) {
                    placeRotatedRoot(world, rand, pos.below().relative(d), consumer, config, d);
                }
            }
            return true;
        }
        return false;
    }

    public boolean placeRotatedRoot(LevelSimulatedReader world, RandomSource rand, BlockPos pos,
                                    BiConsumer<BlockPos, BlockState> consumer, TreeConfiguration config, Direction direction) {
        BlockState state = config.trunkProvider.getState(rand, pos);
        if (state.hasProperty(RotatedPillarBlock.AXIS)) {
            state = state.setValue(RotatedPillarBlock.AXIS, direction.getAxis());
        }
        if (rand.nextDouble() < 0.6 && (TreeFeature.validTreePos(world, pos) || world.isStateAtPosition(pos, b -> b.is(Blocks.DIRT) || b.is(Blocks.GRASS_BLOCK)))) {
            setBlock(world, pos, state, consumer);
            return true;
        }
        return false;
    }

    public void setBlock(LevelSimulatedReader world, BlockPos pos, BlockState state, BiConsumer<BlockPos, BlockState> consumer) {
        consumer.accept(pos, state);
    }
}
