package dev.fixyl.componentviewer.mixin;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.event.EventDispatcher;

@Mixin(value = Minecraft.class)
public final class MinecraftMixin {

    private MinecraftMixin() {}

    @Inject(method = "renderFrame(Z)V", at = @At(value = "HEAD"))
    private void renderFrame(boolean advanceGameTime, CallbackInfo callback) {
        ComponentViewer.dispatchEventSafely(EventDispatcher::invokeStartRenderEvent);
    }
}
