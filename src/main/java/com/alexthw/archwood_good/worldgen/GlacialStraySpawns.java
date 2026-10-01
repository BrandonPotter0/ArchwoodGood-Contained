package com.alexthw.archwood_good.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

@EventBusSubscriber(modid = "archwood_good")
public class GlacialStraySpawns {
    @SubscribeEvent
    public static void onPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getEntityType() != EntityType.STRAY) return;
        if (!(event.getLevel() instanceof ServerLevelAccessor level)) return;
        BlockPos pos = event.getPos();
        if (!level.getBiome(pos).is(AWGBiomes.GLACIAL_CAVES_KEY)) return;
        boolean ok = Monster.checkMonsterSpawnRules(EntityType.STRAY, level, event.getSpawnType(), pos, event.getRandom());
        event.setResult(ok ? MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED : MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
    }

    @SubscribeEvent
    public static void onPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getEntity().getType() != EntityType.STRAY) return;
        if (!event.getEntity().level().getBiome(event.getEntity().blockPosition()).is(AWGBiomes.GLACIAL_CAVES_KEY)) return;
        if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION || event.getSpawnType() == MobSpawnType.STRUCTURE) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.SUCCEED);
        }
    }
}
