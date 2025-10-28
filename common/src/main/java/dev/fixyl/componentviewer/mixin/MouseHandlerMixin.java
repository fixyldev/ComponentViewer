package dev.fixyl.componentviewer.mixin;

import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.InteractionResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.control.keyboard.Keyboard.Action;

@Mixin(value = MouseHandler.class, priority = Integer.MIN_VALUE)
public final class MouseHandlerMixin {

    private MouseHandlerMixin() {}

    @Inject(method = "onPress(JIII)V", at = @At(value = "HEAD"))
    private void onPress(long windowHandle, int button, int action, int modifiers, CallbackInfo callback) {
        if (windowHandle != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }

        ComponentViewer.dispatchEventSafely(dispatcher ->
            dispatcher.invokeKeyInputEvent(Type.MOUSE.getOrCreate(button), modifiers, Action.fromGlfw(action))
        );
    }

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
            callback.cancel();
        }
    }
}
