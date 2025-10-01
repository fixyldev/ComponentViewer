package dev.fixyl.componentviewer.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.event.EventDispatcher;

@Mixin(value = GameRenderer.class)
public final class GameRendererMixin {

    private GameRendererMixin() {}

    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At(value = "HEAD"))
    private void render(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo callback) {
        ComponentViewer.dispatchEventSafely(EventDispatcher::invokeStartRenderEvent);
    }
}
