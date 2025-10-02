package dev.fixyl.componentviewer.mixin;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.control.Tooltip;
import dev.fixyl.componentviewer.util.Lists;

@Mixin(value = Screen.class, priority = Integer.MAX_VALUE)
public final class ScreenMixin {

    private ScreenMixin() {}

    @Inject(method = "getTooltipFromItem(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "RETURN"), cancellable = true)
    private static void getTooltipFromItem(Minecraft minecraftClient, ItemStack stack, CallbackInfoReturnable<List<Component>> callback) {
        List<Component> tooltipLines = Lists.makeMutable(callback.getReturnValue());

        ComponentViewer.dispatchEventSafely(dispatcher ->
            dispatcher.invokeTooltipEvent(stack, new Tooltip(tooltipLines))
        );

        callback.setReturnValue(tooltipLines);
    }
}
