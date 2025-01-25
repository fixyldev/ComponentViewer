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

package dev.fixyl.componentviewer.keyboard.keybinding;

import net.minecraft.client.toast.Toast;
import net.minecraft.util.TranslatableOption;

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.config.option.EnumOption;
import dev.fixyl.componentviewer.notification.EnumOptionToast;

public class EnumOptionKeyBinding<E extends Enum<E> & TranslatableOption> extends AdvancedKeyBinding {
    private final EnumOption<E> option;

    @Nullable private EnumOptionToast<E> optionToast;

    public EnumOptionKeyBinding(String translationKey, int code, String category, EnumOption<E> option) {
        super(translationKey, code, category);

        this.option = option;
    }

    public void cycleValueOnPressed() {
        if (this.optionToast != null && this.optionToast.getVisibility() == Toast.Visibility.HIDE) {
            this.optionToast = null;
        }

        this.onPressed(() -> {
            this.option.cycleValue();

            if (this.optionToast == null) {
                this.optionToast = EnumOptionToast.dispatch(option, this.getTranslationKey());
            } else {
                this.optionToast.resetTimer();
            }
        });
    }
}
