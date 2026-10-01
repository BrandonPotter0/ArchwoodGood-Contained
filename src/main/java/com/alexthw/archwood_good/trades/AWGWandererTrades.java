package com.alexthw.archwood_good.trades;

import com.alexthw.archwood_good.ArchwoodGood;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

/**
 * Adds Archwood saplings (except Fading) to the Wandering Trader generic pool.
 * Cost and listing weight match vanilla saplings: 1 emerald, 1 sapling, 12 uses, 1 XP.
 */
@EventBusSubscriber(modid = ArchwoodGood.MODID)
public class AWGWandererTrades {

    private static final String[] SAPLINGS = {
            "archwood_good:orange_archwood_sapling",
            "archwood_good:white_archwood_sapling",
            "ars_nouveau:blue_archwood_sapling",
            "ars_nouveau:red_archwood_sapling",
            "ars_nouveau:green_archwood_sapling",
            "ars_nouveau:purple_archwood_sapling",
            "ars_elemental:yellow_archwood_sapling"
    };

    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event) {
        for (String id : SAPLINGS) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
            if (item == null || item == Items.AIR) {
                continue;
            }
            // Same listing as oak/birch/etc. Generic pool = same chance to be rolled.
            event.getGenericTrades().add(new VillagerTrades.ItemsForEmeralds(item, 1, 1, 12, 1));
        }
    }
}
