/*
 * MIT License
 *
 * Copyright (c) 2024 fixyldev
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

package dev.fixyl.componentviewer.config.type;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;

public class BooleanConfig extends AbstractConfig<Boolean> {
    public BooleanConfig(Boolean defaultValue, String translationKey, String tooltipTranslationKey) {
        super(defaultValue, translationKey, tooltipTranslationKey);

        this.simpleOption = SimpleOption.ofBoolean(
            this.translationKey,
            SimpleOption.constantTooltip(Text.translatable(this.tooltipTranslationKey)),
            this.defaultValue,
            value -> ComponentViewer.configManager.writeConfigFile()
        );
    }
}
