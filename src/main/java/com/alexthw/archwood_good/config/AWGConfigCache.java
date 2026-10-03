package com.alexthw.archwood_good.config;

import com.alexthw.archwood_good.ArchwoodGood;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Files;
import java.nio.file.Path;

@EventBusSubscriber(modid = "archwood_good")
public final class AWGConfigCache {
    public static volatile boolean giantTrees = true;
    public static volatile boolean ancientArchwoodExclusion = true;
    public static volatile int ancientArchwoodExclusionRadius = 10;
    private static volatile boolean logged;

    private AWGConfigCache() {}

    public static boolean giantTreesEnabled() {
        boolean value = readBoolean(AWGConfig.GIANT_ARCHWOOD_TREES, "giantArchwoodTrees", true);
        giantTrees = value;
        if (!logged) {
            logged = true;
            ArchwoodGood.LOGGER.info("Giant Archwood Trees config is {}", value ? "ON" : "OFF");
        }
        return value;
    }

    public static boolean ancientArchwoodExclusionEnabled() {
        return ancientArchwoodExclusion;
    }

    public static int ancientArchwoodExclusionRadius() {
        return ancientArchwoodExclusionRadius;
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            giantTrees = readBoolean(AWGConfig.GIANT_ARCHWOOD_TREES, "giantArchwoodTrees", true);
            ancientArchwoodExclusion = readBoolean(AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION, "ancientArchwoodExclusion", true);
            ancientArchwoodExclusionRadius = readInt(AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION_RADIUS,
                    "ancientArchwoodExclusionRadius", 16, 0, 256);
            logged = false;
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            giantTrees = readBoolean(AWGConfig.GIANT_ARCHWOOD_TREES, "giantArchwoodTrees", true);
            ancientArchwoodExclusion = readBoolean(AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION, "ancientArchwoodExclusion", true);
            ancientArchwoodExclusionRadius = readInt(AWGConfig.ANCIENT_ARCHWOOD_EXCLUSION_RADIUS,
                    "ancientArchwoodExclusionRadius", 10, 0, 256);
            logged = false;
            ArchwoodGood.LOGGER.info("Archwood Good worldgen config reloaded: giantTrees={}, ancientExclusion={}, radius={}",
                    giantTrees, ancientArchwoodExclusion, ancientArchwoodExclusionRadius);
        }
    }

    private static boolean readBoolean(ModConfigSpec.BooleanValue value, String key, boolean fallback) {
        try { return value.get(); } catch (Throwable ignored) { return readBooleanFile(key, fallback); }
    }

    private static int readInt(ModConfigSpec.IntValue value, String key, int fallback, int min, int max) {
        try { return value.get(); } catch (Throwable ignored) {
            return Math.max(min, Math.min(max, readIntFile(key, fallback)));
        }
    }

    private static boolean readBooleanFile(String key, boolean fallback) {
        Path file = FMLPaths.CONFIGDIR.get().resolve("archwood_good-common.toml");
        if (!Files.exists(file)) return fallback;
        try {
            for (String line : Files.readAllLines(file)) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#") || !trimmed.contains(key)) continue;
                int eq = trimmed.indexOf('=');
                if (eq < 0) continue;
                String value = trimmed.substring(eq + 1).trim().toLowerCase();
                if (value.startsWith("false")) return false;
                if (value.startsWith("true")) return true;
            }
        } catch (Exception ignored) {}
        return fallback;
    }

    private static int readIntFile(String key, int fallback) {
        Path file = FMLPaths.CONFIGDIR.get().resolve("archwood_good-common.toml");
        if (!Files.exists(file)) return fallback;
        try {
            for (String line : Files.readAllLines(file)) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#") || !trimmed.contains(key)) continue;
                int eq = trimmed.indexOf('=');
                if (eq < 0) continue;
                return Integer.parseInt(trimmed.substring(eq + 1).trim());
            }
        } catch (Exception ignored) {}
        return fallback;
    }
}
