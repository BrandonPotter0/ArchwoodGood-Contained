package com.alexthw.archwood_good.client;

import com.alexthw.archwood_good.worldgen.AWGBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "archwood_good", value = Dist.CLIENT)
public class GlacialCaveSnow {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Player player = mc.player;
        if (level == null || player == null || mc.isPaused()) return;
        if (!level.getBiome(player.blockPosition()).is(AWGBiomes.GLACIAL_CAVES_KEY)) return;
        RandomSource random = level.random;
        for (int i = 0; i < 3; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 14.0;
            double y = player.getY() + (random.nextDouble() - 0.25) * 10.0;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 14.0;
            BlockPos pos = BlockPos.containing(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (!state.getFluidState().isEmpty() || !state.isAir()) continue;
            level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.0, -0.04, 0.0);
        }
    }
}
