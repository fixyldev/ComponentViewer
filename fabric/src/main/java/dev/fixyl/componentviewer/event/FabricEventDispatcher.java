package dev.fixyl.componentviewer.event;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.control.Selection.CycleType;
import dev.fixyl.componentviewer.control.keyboard.Keyboard.Action;
import dev.fixyl.componentviewer.control.Tooltip;

public class FabricEventDispatcher implements EventDispatcher {

    @Override
    public void invokeStartRenderEvent() {
        MixinEvents.START_RENDER_EVENT.invoker().onStartRender();
    }

    @Override
    public void invokeTooltipEvent(ItemStack itemStack, Tooltip tooltip) {
        MixinEvents.TOOLTIP_EVENT.invoker().onTooltip(itemStack, tooltip);
    }

    @Override
    public InteractionResult invokeBundleTooltipImageEvent() {
        return MixinEvents.BUNDLE_TOOLTIP_IMAGE_EVENT.invoker().onBundleTooltipImage();
    }

    @Override
    public void invokeKeyInputEvent(KeyEvent keyEvent, Action action) {
        MixinEvents.KEY_INPUT_EVENT.invoker().onKeyInput(keyEvent, action);
    }

    @Override
    public void invokeButtonInputEvent(MouseButtonInfo mouseButtonInfo, Action action) {
        MixinEvents.BUTTON_INPUT_EVENT.invoker().onButtonInput(mouseButtonInfo, action);
    }

    @Override
    public InteractionResult invokeMouseScrollEvent(double xOffset, double yOffset) {
        return MixinEvents.MOUSE_SCROLL_EVENT.invoker().onMouseScroll(xOffset, yOffset);
    }

    @Override
    public void invokeClearToastManagerEvent() {
        MixinEvents.CLEAR_TOAST_MANAGER_EVENT.invoker().onClearToastManager();
    }

    @Override
    public void invokeCycleComponentEvent(CycleType cycleType) {
        KeyboardEvents.CYCLE_COMPONENT_EVENT.invoker().onCycleComponent(cycleType);
    }

    @Override
    public void invokeCopyActionEvent() {
        KeyboardEvents.COPY_ACTION_EVENT.invoker().onCopyAction();
    }
}
