package com.alexthw.archwood_good.config;

import com.alexthw.archwood_good.ArchwoodGood;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;

/** Worldgen threads cannot call ConfigValue.get(). Read the saved toml instead. */
@EventBusSubscriber(modid = "archwood_good")
public final class AWGConfigCache {
    public static volatile boolean giantTrees = true;
    private static volatile boolean logged;

    private AWGConfigCache() {}

    public static boolean giantTreesEnabled() {
        boolean value = read();
        giantTrees = value;
        if (!logged) {
            logged = true;
            ArchwoodGood.LOGGER.info("Giant Archwood Trees config is {}", value ? "ON" : "OFF");
        }
        return value;
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            giantTrees = read();
            logged = false;
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            giantTrees = read();
            logged = false;
            ArchwoodGood.LOGGER.info("Giant Archwood Trees config reloaded: {}", giantTrees ? "ON" : "OFF");
        }
    }

    private static boolean read() {
        try {
            return AWGConfig.GIANT_ARCHWOOD_TREES.get();
        } catch (Throwable ignored) {
            return readFile();
        }
    }

    private static boolean readFile() {
        Path file = FMLPaths.CONFIGDIR.get().resolve("archwood_good-common.toml");
        if (!Files.exists(file)) {
            return true;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#") || !trimmed.contains("giantArchwoodTrees")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq < 0) {
                    continue;
                }
                String value = trimmed.substring(eq + 1).trim().toLowerCase();
                if (value.startsWith("false")) {
                    return false;
                }
                if (value.startsWith("true")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
            return giantTrees;
        }
        return true;
    }
}
