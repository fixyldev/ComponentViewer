package dev.fixyl.componentviewer.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.InteractionResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.ComponentViewer;

@Mixin(value = MouseHandler.class, priority = Integer.MIN_VALUE)
public final class MouseHandlerMixin {

    private MouseHandlerMixin() {}

    @Inject(method = "onScroll(JDD)V", at = @At(value = "HEAD"), cancellable = true)
    private void onScroll(long windowHandle, double xOffset, double yOffset, CallbackInfo callback) {
        Minecraft minecraftClient = Minecraft.getInstance();

        if (windowHandle != minecraftClient.getWindow().getWindow()) {
            return;
        }

        InteractionResult result = ComponentViewer.dispatchEventWithResultSafely(
            dispatcher -> dispatcher.invokeMouseScrollEvent(xOffset, yOffset)
        ).orElse(InteractionResult.PASS);

        if (result == InteractionResult.SUCCESS) {
            minecraftClient.getFramerateLimitTracker().onInputReceived();
            callback.cancel();
        }
    }
}
