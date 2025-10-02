package dev.fixyl.componentviewer.control.notification;

import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.OptionEnum;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.option.EnumOption;

public class EnumOptionToast<E extends Enum<E> & OptionEnum> implements Toast {

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private static final long DURATION = 2000L;

    private static final int TEXT_LEFT_MARGIN = 8;
    private static final int TEXT_FIRST_ROW = 7;
    private static final int TEXT_SECOND_ROW = 18;

    private static final int FIRST_ROW_COLOR = ARGB.opaque(ChatFormatting.DARK_AQUA.getColor());
    private static final int SECOND_ROW_COLOR = ARGB.opaque(ChatFormatting.WHITE.getColor());

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

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager toastManager, long visibilityTime) {
        if (this.shouldResetTimer) {
            this.shouldResetTimer = false;
            this.totalDuration = visibilityTime + DURATION;
        }

        double actualDuration = (this.totalDuration - DURATION) + DURATION * toastManager.getNotificationDisplayTimeMultiplier();

        this.visibility = (visibilityTime < actualDuration) ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long visibilityTime) {
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND_SPRITE,
            0,
            0,
            this.width(),
            this.height()
        );

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
    }

    public static <E extends Enum<E> & OptionEnum> EnumOptionToast<E> dispatch(EnumOption<E> option, @NullPermitted String translationKey) {
        EnumOptionToast<E> toast = new EnumOptionToast<>(option, translationKey);

        Minecraft.getInstance().getToastManager().addToast(toast);

        return toast;
    }

    public static <E extends Enum<E> & OptionEnum> EnumOptionToast<E> dispatch(EnumOption<E> option) {
        return EnumOptionToast.dispatch(option, null);
    }
}
