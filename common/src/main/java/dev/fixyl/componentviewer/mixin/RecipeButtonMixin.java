package dev.fixyl.componentviewer.mixin;

import static net.minecraft.world.item.TooltipFlag.*;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.control.Tooltip;

@Mixin(value = RecipeButton.class, priority = Integer.MAX_VALUE)
public final class RecipeButtonMixin {

    private RecipeButtonMixin() {}

    @Redirect(method = "getTooltipText(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;getTooltipFromItem(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private List<Component> getTooltipFromItem(Minecraft minecraftClient, ItemStack stack) {
        return stack.getTooltipLines(
            TooltipContext.of(minecraftClient.level),
            minecraftClient.player,
            minecraftClient.options.advancedItemTooltips ? ADVANCED : NORMAL
        );
    }

    @Inject(method = "getTooltipText(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "RETURN"))
    private void getTooltipText(ItemStack stack, CallbackInfoReturnable<List<Component>> callback) {
        ComponentViewer.dispatchEventSafely(dispatcher ->
            dispatcher.invokeTooltipEvent(stack, new Tooltip(callback.getReturnValue()))
        );
    }
}
