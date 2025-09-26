package dev.fixyl.componentviewer.event;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.control.Tooltip;

/**
 * Defines an event dispatcher used to dispatch
 * events originating from mixins.
 */
public interface MixinEventDispatcher {

    void invokeStartRenderEvent();
    void invokeTooltipEvent(ItemStack itemStack, Tooltip tooltip);
    void invokeKeyPressEvent(Key key, int modifiers);
    InteractionResult invokeMouseScrollEvent(double xOffset, double yOffset);
    void invokeClearToastManagerEvent();
}
