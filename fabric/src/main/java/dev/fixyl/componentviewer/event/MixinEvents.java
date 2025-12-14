package dev.fixyl.componentviewer.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;

import dev.fixyl.componentviewer.control.Tooltip;
import dev.fixyl.componentviewer.control.keyboard.Keyboard.Action;

public final class MixinEvents {

    private MixinEvents() {}

    public static final Event<StartRenderCallback> START_RENDER_EVENT = EventFactory.createArrayBacked(StartRenderCallback.class, listeners -> () -> {
        for (StartRenderCallback listener : listeners) {
            listener.onStartRender();
        }
    });

    public static final Event<TooltipCallback> TOOLTIP_EVENT = EventFactory.createArrayBacked(TooltipCallback.class, listeners -> (itemStack, tooltip) -> {
        for (TooltipCallback listener : listeners) {
            listener.onTooltip(itemStack, tooltip);
        }
    });

    public static final Event<BundleTooltipImageCallback> BUNDLE_TOOLTIP_IMAGE_EVENT = EventFactory.createArrayBacked(BundleTooltipImageCallback.class, listeners -> () -> {
        for (BundleTooltipImageCallback listener : listeners) {
            InteractionResult result = listener.onBundleTooltipImage();

            if (result != InteractionResult.PASS) {
                return result;
            }
        }

        return InteractionResult.PASS;
    });

    public static final Event<KeyInputCallback> KEY_INPUT_EVENT = EventFactory.createArrayBacked(KeyInputCallback.class, listeners -> (keyEvent, action) -> {
        for (KeyInputCallback listener : listeners) {
            listener.onKeyInput(keyEvent, action);
        }
    });

    public static final Event<ButtonInputCallback> BUTTON_INPUT_EVENT = EventFactory.createArrayBacked(ButtonInputCallback.class, listeners -> (mouseButtonInfo, action) -> {
        for (ButtonInputCallback listener : listeners) {
            listener.onButtonInput(mouseButtonInfo, action);
        }
    });

    public static final Event<MouseScrollCallback> MOUSE_SCROLL_EVENT = EventFactory.createArrayBacked(MouseScrollCallback.class, listeners -> (xOffset, yOffset) -> {
        for (MouseScrollCallback listener : listeners) {
            InteractionResult result = listener.onMouseScroll(xOffset, yOffset);

            if (result != InteractionResult.PASS) {
                return result;
            }
        }

        return InteractionResult.PASS;
    });

    public static final Event<ClearToastManagerCallback> CLEAR_TOAST_MANAGER_EVENT = EventFactory.createArrayBacked(ClearToastManagerCallback.class, listeners -> () -> {
        for (ClearToastManagerCallback listener : listeners) {
            listener.onClearToastManager();
        }
    });

    @FunctionalInterface
    public static interface StartRenderCallback {
        void onStartRender();
    }

    @FunctionalInterface
    public static interface TooltipCallback {
        void onTooltip(ItemStack itemStack, Tooltip tooltip);
    }

    @FunctionalInterface
    public static interface BundleTooltipImageCallback {
        InteractionResult onBundleTooltipImage();
    }

    @FunctionalInterface
    public static interface KeyInputCallback {
        void onKeyInput(KeyEvent keyEvent, Action action);
    }

    @FunctionalInterface
    public static interface ButtonInputCallback {
        void onButtonInput(MouseButtonInfo mouseButtonInfo, Action action);
    }

    @FunctionalInterface
    public static interface MouseScrollCallback {
        InteractionResult onMouseScroll(double xOffset, double yOffset);
    }

    @FunctionalInterface
    public static interface ClearToastManagerCallback {
        void onClearToastManager();
    }
}
