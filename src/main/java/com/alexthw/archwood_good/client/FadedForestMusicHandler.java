package com.alexthw.archwood_good.client;

import com.alexthw.archwood_good.ArchwoodGood;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ArchwoodGood.MODID, value = Dist.CLIENT)
public final class FadedForestMusicHandler {
    private static final ResourceKey<Biome> FADING_FOREST =
            ResourceKey.create(Registries.BIOME, ArchwoodGood.res("fading_forest"));

    private FadedForestMusicHandler() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player != null
                && minecraft.level != null
                && minecraft.level.getBiome(player.blockPosition()).is(FADING_FOREST)) {
            minecraft.getMusicManager().stopPlaying();
        }
    }
}
