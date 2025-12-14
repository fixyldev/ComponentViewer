package dev.fixyl.componentviewer.event;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.Event;

import dev.fixyl.componentviewer.control.Tooltip;
import dev.fixyl.componentviewer.control.keyboard.Keyboard.Action;

public final class MixinEvents {

    private MixinEvents() {}

    public static class StartRenderEvent extends Event {}

    public static class TooltipEvent extends Event {

        public final ItemStack itemStack;
        public final Tooltip tooltip;

        public TooltipEvent(ItemStack itemStack, Tooltip tooltip) {
            this.itemStack = itemStack;
            this.tooltip = tooltip;
        }
    }

    public static class BundleTooltipImageEvent extends InteractionResultEvent {}

    public static class KeyInputEvent extends Event {

        public final KeyEvent keyEvent;
        public final Action action;

        public KeyInputEvent(KeyEvent keyEvent, Action action) {
            this.keyEvent = keyEvent;
            this.action = action;
        }
    }

    public static class ButtonInputEvent extends Event {

        public final MouseButtonInfo mouseButtonInfo;
        public final Action action;

        public ButtonInputEvent(MouseButtonInfo mouseButtonInfo, Action action) {
            this.mouseButtonInfo = mouseButtonInfo;
            this.action = action;
        }
    }

    public static class MouseScrollEvent extends InteractionResultEvent {

        public final double xOffset;
        public final double yOffset;

        public MouseScrollEvent(double xOffset, double yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    public static class ClearToastManagerEvent extends Event {}
}
