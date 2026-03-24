package dev.fixyl.componentviewer.control.keyboard;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.Minecraft;

import dev.fixyl.componentviewer.DisablableMod;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.keymapping.KeyMappings;
import dev.fixyl.componentviewer.event.EventDispatcher;

/**
 * The platform-specific keyboard logic for Fabric.
 * <p>
 * This class implements the registration of key mappings
 * for the Fabric platform.
 */
public class FabricKeyboard extends Keyboard {

    public FabricKeyboard(Minecraft minecraftClient, DisablableMod disablableMod, EventDispatcher eventDispatcher, Configs configs) {
        super(
            minecraftClient,
            disablableMod,
            eventDispatcher,
            configs,
            configs.controlsAlternativeCopyModifierKey,
            configs.controlsAllowCyclingOptionsWhileInScreen
        );

        FabricKeyboard.registerKeyMappings(configs);
    }

    private static void registerKeyMappings(KeyMappings keyMappings) {
        for (Category category : keyMappings.getKeyMappingCategories()) {
            Category.register(category.id());
        }

        for (KeyMapping keyMapping : keyMappings.getKeyMappings()) {
            KeyMappingHelper.registerKeyMapping(keyMapping);
        }
    }
}
