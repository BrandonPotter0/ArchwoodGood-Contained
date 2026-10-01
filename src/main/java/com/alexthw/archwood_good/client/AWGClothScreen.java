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
        boolean current = true;
        try {
            current = AWGConfig.GIANT_ARCHWOOD_TREES.get();
        } catch (Throwable ignored) {
            current = true;
        }
        worldgen.addEntry(entry.startBooleanToggle(Component.literal("Giant Archwood Trees"), current)
                .setDefaultValue(true)
                .setTooltip(Optional.of(new Component[]{Component.literal("Enable Giant Trees in Archwood Forests. Default is on.")}))
                .setSaveConsumer(AWGConfig.GIANT_ARCHWOOD_TREES::set)
                .build());
        return builder.build();
    }
}
