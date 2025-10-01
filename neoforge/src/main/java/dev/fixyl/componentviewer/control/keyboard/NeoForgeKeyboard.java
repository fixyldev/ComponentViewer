package dev.fixyl.componentviewer.control.keyboard;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.DisablableMod;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.keymapping.AdvancedKeyMapping;
import dev.fixyl.componentviewer.event.EventDispatcher;

/**
 * The platform-specific keyboard logic for NeoForge.
 * <p>
 * This class implements the registration of key mappings
 * for the NeoForge platform.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class NeoForgeKeyboard extends Keyboard {

    public NeoForgeKeyboard(Minecraft minecraftClient, DisablableMod disablableMod, EventDispatcher eventDispatcher, Configs configs) {
        super(
            minecraftClient,
            disablableMod,
            eventDispatcher,
            configs,
            configs.controlsAlternativeCopyModifierKey,
            configs.controlsAllowCyclingOptionsWhileInScreen
        );
    }

    @SubscribeEvent
    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        Configs configs = ComponentViewer.getInstance().configs;

        for (AdvancedKeyMapping keyMapping : configs.getKeyMappings()) {
            keyMapping.setKeyConflictContext(switch (keyMapping.getConfictContext()) {
                case UNIVERSAL -> KeyConflictContext.UNIVERSAL;
                case IN_SCREEN -> KeyConflictContext.GUI;
                case IN_GAME -> KeyConflictContext.IN_GAME;
            });

            event.register(keyMapping);
        }
    }
}
