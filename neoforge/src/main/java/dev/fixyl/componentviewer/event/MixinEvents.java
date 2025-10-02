package dev.fixyl.componentviewer.event;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.Event;

import dev.fixyl.componentviewer.control.Tooltip;

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

    public static class KeyPressEvent extends Event {

        public final Key key;
        public final int modifiers;

        public KeyPressEvent(Key key, int modifiers) {
            this.key = key;
            this.modifiers = modifiers;
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
