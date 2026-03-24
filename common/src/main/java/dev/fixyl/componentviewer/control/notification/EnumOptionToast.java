package dev.fixyl.componentviewer.control.notification;

import java.util.Objects;

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

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.option.EnumOption;
import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;

public class EnumOptionToast<E extends Enum<E> & OptionEnum> implements Toast {

    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");
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
    private Toast.Visibility wantedVisibility;

    public EnumOptionToast(EnumOption<E> option, @NullPermitted String translationKey) {
        this.option = option;
        this.translationKey = Objects.requireNonNullElse(translationKey, option.getTranslationKey());

        this.totalDuration = DURATION;
        this.shouldResetTimer = false;
        this.wantedVisibility = Toast.Visibility.SHOW;
    }

    public void resetTimer() {
        this.shouldResetTimer = true;
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager toastManager, long fullyVisibleForMs) {
        if (this.shouldResetTimer) {
            this.shouldResetTimer = false;
            this.totalDuration = fullyVisibleForMs + DURATION;
        }

        double actualDuration = (this.totalDuration - DURATION) + DURATION * toastManager.getNotificationDisplayTimeMultiplier();

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

        graphics.text(
            font,
            Component.translatable(this.translationKey),
            TEXT_LEFT_MARGIN,
            TEXT_FIRST_ROW,
            FIRST_ROW_COLOR,
            false
        );

        graphics.text(
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
