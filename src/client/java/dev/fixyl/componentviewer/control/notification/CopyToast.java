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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class CopyToast implements Toast {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("toast/advancement");
    private static final long DURATION = 3000L;

    private static final int ITEM_LEFT_MARGIN = 8;
    private static final int ITEM_TOP_MARGIN = 8;
    private static final int TEXT_LEFT_MARGIN = 8;
    private static final int TEXT_LEFT_MARGIN_WITH_ITEM = 30;
    private static final int TEXT_FIRST_ROW = 7;
    private static final int TEXT_SECOND_ROW = 18;

    private static final int FIRST_ROW_COLOR_SUCCESS = Formatting.DARK_GREEN.getColorValue();
    private static final int SECOND_ROW_COLOR_SUCCESS = Formatting.GOLD.getColorValue();
    private static final int FIRST_ROW_COLOR_FAILURE = Formatting.RED.getColorValue();
    private static final int SECOND_ROW_COLOR_FAILURE = Formatting.DARK_AQUA.getColorValue();

    private final CopyToast.Type toastType;
    @Nullable private final ItemStack itemStack;

    private final String translationKey;
    private final int firstRowColor;
    private final int secondRowColor;
    private final int textLeftMargin;

    private Toast.Visibility visibility;

    public CopyToast(CopyToast.Type type, @Nullable ItemStack itemStack) {
        this.toastType = type;
        this.itemStack = itemStack;

        if (type.success) {
            this.translationKey = "componentviewer.notification.toast.copy.success";
            this.firstRowColor = CopyToast.FIRST_ROW_COLOR_SUCCESS;
            this.secondRowColor = CopyToast.SECOND_ROW_COLOR_SUCCESS;
        } else {
            this.translationKey = "componentviewer.notification.toast.copy.failure";
            this.firstRowColor = CopyToast.FIRST_ROW_COLOR_FAILURE;
            this.secondRowColor = CopyToast.SECOND_ROW_COLOR_FAILURE;
        }

        this.textLeftMargin = (itemStack == null) ? TEXT_LEFT_MARGIN : TEXT_LEFT_MARGIN_WITH_ITEM;

        this.visibility = Toast.Visibility.SHOW;
    }

    public CopyToast(CopyToast.Type type) {
        this(type, null);
    }

    @Override
    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager toastManager, long elapsedTime) {
        double actualDuration = CopyToast.DURATION * toastManager.getNotificationDisplayTimeMultiplier();

        this.visibility = (elapsedTime < actualDuration) ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    @Override
    public void draw(DrawContext drawContext, TextRenderer textRenderer, long startTime) {
        drawContext.drawGuiTexture(
            RenderLayer::getGuiTextured,
            CopyToast.BACKGROUND_TEXTURE,
            0,
            0,
            this.getWidth(),
            this.getHeight()
        );

        if (this.itemStack != null) {
            drawContext.drawItemWithoutEntity(
                this.itemStack,
                CopyToast.ITEM_LEFT_MARGIN,
                CopyToast.ITEM_TOP_MARGIN
            );
        }

        drawContext.drawText(
            textRenderer,
            Text.translatable(this.translationKey),
            this.textLeftMargin,
            CopyToast.TEXT_FIRST_ROW,
            this.firstRowColor,
            false
        );

        drawContext.drawText(
            textRenderer,
            Text.translatable(this.toastType.translationKey),
            this.textLeftMargin,
            CopyToast.TEXT_SECOND_ROW,
            this.secondRowColor,
            false
        );
    }

    @Override
    public CopyToast.Type getType() {
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

    public static CopyToast dispatch(CopyToast.Type type, @Nullable ItemStack itemStack) {
        CopyToast toast = new CopyToast(type, itemStack);

        MinecraftClient.getInstance().getToastManager().add(toast);

        return toast;
    }

    public static CopyToast dispatch(CopyToast.Type type) {
        return CopyToast.dispatch(type, null);
    }
}
