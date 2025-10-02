package dev.fixyl.componentviewer.control.notification;

import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.OptionEnum;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.option.EnumOption;

public class EnumOptionToast<E extends Enum<E> & OptionEnum> implements Toast {

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private static final long DURATION = 2000L;

    private static final int TEXT_LEFT_MARGIN = 8;
    private static final int TEXT_FIRST_ROW = 7;
    private static final int TEXT_SECOND_ROW = 18;

    private static final int FIRST_ROW_COLOR = ChatFormatting.DARK_AQUA.getColor();
    private static final int SECOND_ROW_COLOR = ChatFormatting.WHITE.getColor();

    private final EnumOption<E> option;
    private final String translationKey;

    private long totalDuration;
    private boolean shouldResetTimer;
    private Toast.Visibility visibility;

    public EnumOptionToast(EnumOption<E> option, @NullPermitted String translationKey) {
        this.option = option;
        this.translationKey = Objects.requireNonNullElse(translationKey, option.getTranslationKey());

        this.totalDuration = DURATION;
        this.shouldResetTimer = false;
        this.visibility = Toast.Visibility.SHOW;
    }

    public void resetTimer() {
        this.shouldResetTimer = true;
    }

    public Toast.Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public Toast.Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        guiGraphics.blitSprite(
            BACKGROUND_SPRITE,
            0,
            0,
            this.width(),
            this.height()
        );

        Font font = toastComponent.getMinecraft().font;

        guiGraphics.drawString(
            font,
            Component.translatable(this.translationKey),
            TEXT_LEFT_MARGIN,
            TEXT_FIRST_ROW,
            FIRST_ROW_COLOR,
            false
        );

        guiGraphics.drawString(
            font,
            this.option.getValue().getCaption(),
            TEXT_LEFT_MARGIN,
            TEXT_SECOND_ROW,
            SECOND_ROW_COLOR,
            false
        );

        this.updateVisibility(toastComponent, timeSinceLastVisible);

        return this.visibility;
    }

    private void updateVisibility(ToastComponent toastComponent, long timeSinceLastVisible) {
        if (this.shouldResetTimer) {
            this.shouldResetTimer = false;
            this.totalDuration = timeSinceLastVisible + DURATION;
        }

        double actualDuration = (this.totalDuration - DURATION) + DURATION * toastComponent.getNotificationDisplayTimeMultiplier();

        this.visibility = (timeSinceLastVisible < actualDuration) ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    public static <E extends Enum<E> & OptionEnum> EnumOptionToast<E> dispatch(EnumOption<E> option, @NullPermitted String translationKey) {
        EnumOptionToast<E> toast = new EnumOptionToast<>(option, translationKey);

        Minecraft.getInstance().getToasts().addToast(toast);

        return toast;
    }

    public static <E extends Enum<E> & OptionEnum> EnumOptionToast<E> dispatch(EnumOption<E> option) {
        return EnumOptionToast.dispatch(option, null);
    }
}
