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

import java.util.Arrays;

import com.mojang.serialization.Codec;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.option.DisplayOption;

public class DisplayConfig extends AbstractConfig<DisplayOption> {
    public DisplayConfig(DisplayOption defaultValue, String translationKey, String tooltipTranslationKey) {
        super(defaultValue, translationKey, tooltipTranslationKey);

        this.simpleOption = new SimpleOption<DisplayOption>(
            this.translationKey,
            SimpleOption.constantTooltip(Text.translatable(this.tooltipTranslationKey)),
            SimpleOption.enumValueText(),
            new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(DisplayOption.values()), Codec.INT.xmap(DisplayOption::byId, DisplayOption::getId)),
            this.defaultValue,
            value -> ComponentViewer.configManager.writeConfigFile()
        );
    }
}
