package com.alexthw.archwood_good.client;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = "archwood_good", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AWGConfigScreen {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        if (!ModList.get().isLoaded("cloth_config")) {
            return;
        }
        ModList.get().getModContainerById("archwood_good").ifPresent(container ->
                container.registerExtensionPoint(IConfigScreenFactory.class,
                        (modContainer, parent) -> open(parent)));
    }

    private static Screen open(Screen parent) {
        try {
            return AWGClothScreen.create(parent);
        } catch (Throwable t) {
            Throwable cause = t.getCause() == null ? t : t.getCause();
            throw new IllegalStateException("Archwood Good config screen: " + cause, t);
        }
    }
}
