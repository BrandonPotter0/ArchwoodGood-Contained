package com.alexthw.archwood_good.misc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.GrindstoneEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Grind colored archwood logs and wood into fading archwood. Log for log, wood for wood.
 * One filled slot, the other empty. A small flat amount of experience.
 */
public final class FadingGrindstone {
    private static final int XP = 2;
    private static final Map<ResourceLocation, ResourceLocation> TO_FADING = new HashMap<>();

    static {
        log("ars_nouveau", "blue");
        log("ars_nouveau", "red");
        log("ars_nouveau", "green");
        log("ars_nouveau", "purple");
        log("ars_elemental", "yellow");
        log("archwood_good", "orange");
        log("archwood_good", "white");
        wood("ars_nouveau", "blue");
        wood("ars_nouveau", "red");
        wood("ars_nouveau", "green");
        wood("ars_nouveau", "purple");
        wood("ars_elemental", "yellow");
        wood("archwood_good", "orange");
        wood("archwood_good", "white");
    }

    private FadingGrindstone() {
    }

    private static void log(String namespace, String color) {
        TO_FADING.put(ResourceLocation.fromNamespaceAndPath(namespace, color + "_archwood_log"), ResourceLocation.fromNamespaceAndPath("archwood_good", "archwood_log"));
        TO_FADING.put(ResourceLocation.fromNamespaceAndPath(namespace, "stripped_" + color + "_archwood_log"), ResourceLocation.fromNamespaceAndPath("archwood_good", "stripped_archwood_log"));
    }

    private static void wood(String namespace, String color) {
        TO_FADING.put(ResourceLocation.fromNamespaceAndPath(namespace, color + "_archwood_wood"), ResourceLocation.fromNamespaceAndPath("archwood_good", "archwood_wood"));
        TO_FADING.put(ResourceLocation.fromNamespaceAndPath(namespace, "stripped_" + color + "_archwood_wood"), ResourceLocation.fromNamespaceAndPath("archwood_good", "stripped_archwood_wood"));
    }

    public static boolean canGrind(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return TO_FADING.containsKey(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static void onPlace(GrindstoneEvent.OnPlaceItem event) {
        convert(event.getTopItem(), event.getBottomItem()).ifPresent(output -> {
            event.setOutput(output);
            event.setXp(XP);
        });
    }

    public static void onTake(GrindstoneEvent.OnTakeItem event) {
        if (convert(event.getTopItem(), event.getBottomItem()).isEmpty()) {
            return;
        }
        event.setXp(XP);
        event.setNewTopItem(ItemStack.EMPTY);
        event.setNewBottomItem(ItemStack.EMPTY);
    }

    private static Optional<ItemStack> convert(ItemStack top, ItemStack bottom) {
        boolean topEmpty = top.isEmpty();
        boolean bottomEmpty = bottom.isEmpty();
        if (topEmpty == bottomEmpty) {
            return Optional.empty();
        }
        ItemStack input = topEmpty ? bottom : top;
        ResourceLocation in = BuiltInRegistries.ITEM.getKey(input.getItem());
        ResourceLocation out = TO_FADING.get(in);
        if (out == null) {
            return Optional.empty();
        }
        Item result = BuiltInRegistries.ITEM.get(out);
        if (result == null || result == net.minecraft.world.item.Items.AIR) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack(result, input.getCount()));
    }
}
