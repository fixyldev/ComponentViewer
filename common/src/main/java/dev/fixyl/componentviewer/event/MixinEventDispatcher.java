package dev.fixyl.componentviewer.event;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
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
    InteractionResult invokeBundleTooltipImageEvent();
    void invokeKeyInputEvent(KeyEvent keyEvent, Action action);
    void invokeButtonInputEvent(MouseButtonInfo mouseButtonInfo, Action action);
    InteractionResult invokeMouseScrollEvent(double xOffset, double yOffset);
    void invokeClearToastManagerEvent();
}
