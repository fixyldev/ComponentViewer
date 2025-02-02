/*
 * MIT License
 *
 * Copyright (c) 2025 fixyldev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.fixyl.componentviewer.control.notification;

import java.util.Objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TranslatableOption;

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.config.option.EnumOption;

public class EnumOptionToast<E extends Enum<E> & TranslatableOption> implements Toast {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("minecraft", "toast/advancement");
    private static final long DURATION = 2000L;

    private static final int TEXT_LEFT_MARGIN = 8;
    private static final int TEXT_FIRST_ROW = 7;
    private static final int TEXT_SECOND_ROW = 18;

    private static final int FIRST_ROW_COLOR = Formatting.DARK_AQUA.getColorValue();
    private static final int SECOND_ROW_COLOR = Formatting.WHITE.getColorValue();

    private final EnumOption<E> option;
    private final String translationKey;

    private long totalDuration;
    private boolean shouldResetTimer;
    private Toast.Visibility visibility;

    public EnumOptionToast(EnumOption<E> option, @Nullable String translationKey) {
        this.option = option;
        this.translationKey = Objects.requireNonNullElse(translationKey, option.getTranslationKey());

        this.totalDuration = EnumOptionToast.DURATION;
        this.shouldResetTimer = false;
        this.visibility = Toast.Visibility.SHOW;
    }

    public void resetTimer() {
        this.shouldResetTimer = true;
    }

    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public Toast.Visibility draw(DrawContext drawContext, ToastManager toastManager, long elapsedTime) {
        drawContext.drawGuiTexture(
            EnumOptionToast.BACKGROUND_TEXTURE,
            0,
            0,
            this.getWidth(),
            this.getHeight()
        );

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        drawContext.drawText(
            textRenderer,
            Text.translatable(this.translationKey),
            EnumOptionToast.TEXT_LEFT_MARGIN,
            EnumOptionToast.TEXT_FIRST_ROW,
            EnumOptionToast.FIRST_ROW_COLOR,
            false
        );

        drawContext.drawText(
            textRenderer,
            Text.translatable(this.option.getValue().getTranslationKey()),
            EnumOptionToast.TEXT_LEFT_MARGIN,
            EnumOptionToast.TEXT_SECOND_ROW,
            EnumOptionToast.SECOND_ROW_COLOR,
            false
        );

        this.updateVisibility(toastManager, elapsedTime);

        return this.visibility;
    }

    private void updateVisibility(ToastManager toastManager, long elapsedTime) {
        if (this.shouldResetTimer) {
            this.shouldResetTimer = false;
            this.totalDuration = elapsedTime + EnumOptionToast.DURATION;
        }

        double actualDuration = (this.totalDuration - EnumOptionToast.DURATION) + EnumOptionToast.DURATION * toastManager.getNotificationDisplayTimeMultiplier();

        this.visibility = (elapsedTime < actualDuration) ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    public static <E extends Enum<E> & TranslatableOption> EnumOptionToast<E> dispatch(EnumOption<E> option, @Nullable String translationKey) {
        EnumOptionToast<E> toast = new EnumOptionToast<>(option, translationKey);

        MinecraftClient.getInstance().getToastManager().add(toast);

        return toast;
    }

    public static <E extends Enum<E> & TranslatableOption> EnumOptionToast<E> dispatch(EnumOption<E> option) {
        return EnumOptionToast.dispatch(option, null);
    }
}
