package dev.fixyl.componentviewer.event;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.control.Tooltip;
import dev.fixyl.componentviewer.control.keyboard.Keyboard.Action;

/**
 * Defines an event dispatcher used to dispatch
 * events originating from mixins.
 */
public interface MixinEventDispatcher {

    void invokeStartRenderEvent();
    void invokeTooltipEvent(ItemStack itemStack, Tooltip tooltip);
    void invokeKeyInputEvent(Key key, int modifiers, Action action);
    InteractionResult invokeMouseScrollEvent(double xOffset, double yOffset);
    void invokeClearToastManagerEvent();
}
