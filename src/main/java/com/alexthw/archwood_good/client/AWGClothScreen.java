package com.alexthw.archwood_good.client;

import com.alexthw.archwood_good.config.AWGConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public final class AWGClothScreen {
    private AWGClothScreen() {}

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Archwood Good"))
                .setSavingRunnable(AWGConfig.SPEC::save);
        ConfigEntryBuilder entry = builder.entryBuilder();
        ConfigCategory worldgen = builder.getOrCreateCategory(Component.literal("World Generation"));

        boolean giantTrees = true;
        boolean exclusion = true;
        int radius = 10;
        try {
            giantTrees = AWGConfig.GIANT_ARCHWOOD_TREES.get();
            exclusion = AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION.get();
            radius = AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION_RADIUS.get();
        } catch (Throwable ignored) {}

        worldgen.addEntry(entry.startBooleanToggle(Component.literal("Giant Archwood Trees"), giantTrees)
                .setDefaultValue(true)
                .setTooltip(Optional.of(new Component[]{Component.literal("Enable Giant Trees in Archwood Forests. Default is on.")}))
                .setSaveConsumer(AWGConfig.GIANT_ARCHWOOD_TREES::set)
                .build());

        worldgen.addEntry(entry.startBooleanToggle(Component.literal("Ancient Archwood Exclusion"), exclusion)
                .setDefaultValue(true)
                .setTooltip(Optional.of(new Component[]{Component.literal("Prevent Archwood trees from generating within the configured radius of an Ancient Archwood Tree.")}))
                .setSaveConsumer(AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION::set)
                .build());

        worldgen.addEntry(entry.startIntField(Component.literal("Ancient Archwood Exclusion Radius"), radius)
                 .setDefaultValue(10)
                .setMin(0)
                .setMax(256)
                .setTooltip(Optional.of(new Component[]{Component.literal("Horizontal radius in blocks. Default is 16.")}))
                .setSaveConsumer(AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION_RADIUS::set)
                .build());

        return builder.build();
    }
}
