package com.alexthw.archwood_good.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static com.alexthw.archwood_good.registry.AWGBlockRegistry.BLOCKS;

/**
 * Registers potted forms of Archwood Good's saplings and connects them to
 * Minecraft's vanilla flower pot interaction.
 */
public final class AWGPottedPlants {
    private AWGPottedPlants() {
    }

    public static final DeferredHolder<Block, ? extends Block> POTTED_ORANGE_ARCHWOOD_SAPLING =
            register("potted_orange_archwood_sapling", () -> AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get());

    public static final DeferredHolder<Block, ? extends Block> POTTED_WHITE_ARCHWOOD_SAPLING =
            register("potted_white_archwood_sapling", () -> AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get());

    public static final DeferredHolder<Block, ? extends Block> POTTED_FADING_ARCHWOOD_SAPLING =
            register("potted_archwood_sapling", () -> AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get());

    private static DeferredHolder<Block, ? extends Block> register(String name, Supplier<? extends Block> sapling) {
        return BLOCKS.register(name, () -> new FlowerPotBlock(
                () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                sapling,
                BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING)
        ));
    }

    public static void registerPlants(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FlowerPotBlock flowerPot = (FlowerPotBlock) Blocks.FLOWER_POT;
            flowerPot.addPlant(BuiltInRegistries.BLOCK.getKey(AWGBlockRegistry.ORANGE_ARCHWOOD_SAPLING.get()),
                    POTTED_ORANGE_ARCHWOOD_SAPLING);
            flowerPot.addPlant(BuiltInRegistries.BLOCK.getKey(AWGBlockRegistry.WHITE_ARCHWOOD_SAPLING.get()),
                    POTTED_WHITE_ARCHWOOD_SAPLING);
            flowerPot.addPlant(BuiltInRegistries.BLOCK.getKey(AWGBlockRegistry.FADING_ARCHWOOD_SAPLING.get()),
                    POTTED_FADING_ARCHWOOD_SAPLING);
        });
    }
}
