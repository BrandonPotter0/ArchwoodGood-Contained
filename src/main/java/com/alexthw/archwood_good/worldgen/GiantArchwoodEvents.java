package com.alexthw.archwood_good.worldgen;

import com.alexthw.archwood_good.ArchwoodGood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import java.util.Map;

@EventBusSubscriber(modid = ArchwoodGood.MODID)
public class GiantArchwoodEvents {
    private static final Map<ResourceLocation, ResourceKey<ConfiguredFeature<?, ?>>> GIANTS = Map.of(
            ResourceLocation.parse("ars_nouveau:blue_archwood_sapling"), AWGConfiguredFeatures.GIANT_BLUE_TREE_KEY,
            ResourceLocation.parse("ars_nouveau:red_archwood_sapling"), AWGConfiguredFeatures.GIANT_RED_TREE_KEY,
            ResourceLocation.parse("ars_nouveau:green_archwood_sapling"), AWGConfiguredFeatures.GIANT_GREEN_TREE_KEY,
            ResourceLocation.parse("ars_nouveau:purple_archwood_sapling"), AWGConfiguredFeatures.GIANT_PURPLE_TREE_KEY,
            ResourceLocation.parse("ars_elemental:yellow_archwood_sapling"), AWGConfiguredFeatures.GIANT_YELLOW_TREE_KEY,
            ResourceLocation.parse("archwood_good:orange_archwood_sapling"), AWGConfiguredFeatures.GIANT_ORANGE_TREE_KEY,
            ResourceLocation.parse("archwood_good:white_archwood_sapling"), AWGConfiguredFeatures.GIANT_WHITE_TREE_KEY
    );

    @SubscribeEvent
    public static void onGrow(BlockGrowFeatureEvent event) {
        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        ResourceKey<ConfiguredFeature<?, ?>> giant = GIANTS.get(id);
        if (giant == null) return;

        BlockPos origin = findTwoByTwoOrigin(level, pos, state.getBlock());
        if (origin == null) return;

        // Treat the four saplings as one growth operation and consume the
        // complete cluster before the Giant Archwood feature is placed.
        consumeTwoByTwo(level, origin, state.getBlock());

        level.registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(giant)
                .ifPresent(event::setFeature);
    }

    private static void consumeTwoByTwo(LevelAccessor level, BlockPos origin, Block sapling) {
        clearSapling(level, origin, sapling);
        clearSapling(level, origin.east(), sapling);
        clearSapling(level, origin.south(), sapling);
        clearSapling(level, origin.east().south(), sapling);
    }

    private static void clearSapling(LevelAccessor level, BlockPos pos, Block sapling) {
        if (level.getBlockState(pos).is(sapling)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static BlockPos findTwoByTwoOrigin(LevelAccessor level, BlockPos pos, Block sapling) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dz = -1; dz <= 0; dz++) {
                BlockPos origin = pos.offset(dx, 0, dz);
                if (isSame(level, origin, sapling)
                        && isSame(level, origin.east(), sapling)
                        && isSame(level, origin.south(), sapling)
                        && isSame(level, origin.east().south(), sapling)) {
                    return origin;
                }
            }
        }
        return null;
    }

    private static boolean isSame(LevelAccessor level, BlockPos pos, Block sapling) {
        return level.getBlockState(pos).is(sapling);
    }
}
