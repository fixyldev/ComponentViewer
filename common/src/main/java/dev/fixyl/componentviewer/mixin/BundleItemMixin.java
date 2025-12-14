package dev.fixyl.componentviewer.mixin;

import java.util.Optional;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.event.EventDispatcher;

@Mixin(value = BundleItem.class)
public final class BundleItemMixin {

    private BundleItemMixin() {}

    @Inject(method = "toggleSelectedItem(Lnet/minecraft/world/item/ItemStack;I)V", at = @At(value = "HEAD"), cancellable = true)
    private static void toggleSelectedItem(ItemStack bundle, int selectedItem, CallbackInfo callback) {
        if (BundleItemMixin.prohibitBundleTooltip()) {
            callback.cancel();
        }
    }

    @Inject(method = "getTooltipImage(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;", at = @At(value = "HEAD"), cancellable = true)
    private void getTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> callback) {
        if (BundleItemMixin.prohibitBundleTooltip()) {
            callback.setReturnValue(Optional.empty());
        }
    }

    @Unique
    private static boolean prohibitBundleTooltip() {
        InteractionResult result = ComponentViewer.dispatchEventWithResultSafely(
            EventDispatcher::invokeBundleTooltipImageEvent
        ).orElse(InteractionResult.PASS);

        return result == InteractionResult.SUCCESS;
    }
}
