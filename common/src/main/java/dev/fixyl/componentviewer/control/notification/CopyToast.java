package dev.fixyl.componentviewer.control.notification;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;

public class CopyToast implements Toast {

    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");
    private static final long DURATION = 3000L;

    private static final int ITEM_LEFT_MARGIN = 8;
    private static final int ITEM_TOP_MARGIN = 8;
    private static final int TEXT_LEFT_MARGIN = 8;
    private static final int TEXT_LEFT_MARGIN_WITH_ITEM = 30;
    private static final int TEXT_FIRST_ROW = 7;
    private static final int TEXT_SECOND_ROW = 18;

    private static final int FIRST_ROW_COLOR_SUCCESS = ARGB.opaque(ChatFormatting.DARK_GREEN.getColor());
    private static final int SECOND_ROW_COLOR_SUCCESS = ARGB.opaque(ChatFormatting.GOLD.getColor());
    private static final int FIRST_ROW_COLOR_FAILURE = ARGB.opaque(ChatFormatting.RED.getColor());
    private static final int SECOND_ROW_COLOR_FAILURE = ARGB.opaque(ChatFormatting.DARK_AQUA.getColor());

    private final CopyToast.Type toastType;
    private final @NullPermitted ItemStack itemStack;

    private final String translationKey;
    private final int firstRowColor;
    private final int secondRowColor;
    private final int textLeftMargin;

    private Toast.Visibility wantedVisibility;

    public CopyToast(CopyToast.Type type, @NullPermitted ItemStack itemStack) {
        this.toastType = type;
        this.itemStack = itemStack;

        if (type.success) {
            this.translationKey = "componentviewer.notification.toast.copy.success";
            this.firstRowColor = FIRST_ROW_COLOR_SUCCESS;
            this.secondRowColor = SECOND_ROW_COLOR_SUCCESS;
        } else {
            this.translationKey = "componentviewer.notification.toast.copy.failure";
            this.firstRowColor = FIRST_ROW_COLOR_FAILURE;
            this.secondRowColor = SECOND_ROW_COLOR_FAILURE;
        }

        this.textLeftMargin = (itemStack == null) ? TEXT_LEFT_MARGIN : TEXT_LEFT_MARGIN_WITH_ITEM;

        this.wantedVisibility = Toast.Visibility.HIDE;
    }

    public CopyToast(CopyToast.Type type) {
        this(type, null);
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager toastManager, long fullyVisibleForMs) {
        double actualDuration = DURATION * toastManager.getNotificationDisplayTimeMultiplier();

        this.wantedVisibility = (fullyVisibleForMs >= actualDuration) ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND_SPRITE,
            0,
            0,
            this.width(),
            this.height()
        );

        if (this.itemStack != null) {
            graphics.fakeItem(
                this.itemStack,
                ITEM_LEFT_MARGIN,
                ITEM_TOP_MARGIN
            );
        }

        graphics.text(
            font,
            Component.translatable(this.translationKey),
            this.textLeftMargin,
            TEXT_FIRST_ROW,
            this.firstRowColor,
            false
        );

        graphics.text(
            font,
            Component.translatable(this.toastType.translationKey),
            this.textLeftMargin,
            TEXT_SECOND_ROW,
            this.secondRowColor,
            false
        );
    }

    @Override
    public CopyToast.Type getToken() {
        return this.toastType;
    }

    public enum Type {
        COMPONENT_VALUE("componentviewer.notification.toast.copy.type.component_value", true),
        ITEM_STACK("componentviewer.notification.toast.copy.type.item_stack", true),
        GIVE_COMMAND("componentviewer.notification.toast.copy.type.give_command", true),
        FORMATTING_EXCEPTION("componentviewer.notification.toast.copy.type.formatting_exception", false);

        private final String translationKey;
        private final boolean success;

        private Type(String translationKey, boolean success) {
            this.translationKey = translationKey;
            this.success = success;
        }
    }

    public static CopyToast dispatch(CopyToast.Type type, @NullPermitted ItemStack itemStack) {
        CopyToast toast = new CopyToast(type, itemStack);

        Minecraft.getInstance().getToastManager().addToast(toast);

        return toast;
    }

    public static CopyToast dispatch(CopyToast.Type type) {
        return CopyToast.dispatch(type, null);
    }
}
