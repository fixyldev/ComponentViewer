package dev.fixyl.componentviewer.mixin;

import net.minecraft.client.gui.components.toasts.ToastManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.event.EventDispatcher;

@Mixin(value = ToastManager.class)
public final class ToastManagerMixin {

    private ToastManagerMixin() {}

    @Inject(method = "clear()V", at = @At(value = "HEAD"))
    private void clear(CallbackInfo callback) {
        ComponentViewer.dispatchEventSafely(EventDispatcher::invokeClearToastManagerEvent);
    }
}
