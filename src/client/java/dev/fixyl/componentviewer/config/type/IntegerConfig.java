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

import com.mojang.serialization.Codec;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class IntegerConfig extends AbstractConfig<Integer> {
    private final Integer minValue;
    private final Integer maxValue;
    private final String translationKeyValue;
    private final String translationKeyOff;

    public IntegerConfig(Integer defaultValue, Integer minValue, Integer maxValue, String translationKey, String tooltipTranslationKey, String translationKeyValue, String translationKeyOff) {
        super(defaultValue, translationKey, tooltipTranslationKey);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.translationKeyValue = translationKeyValue;
        this.translationKeyOff = translationKeyOff;

        this.simpleOption = new SimpleOption<Integer>(
            this.translationKey,
            SimpleOption.constantTooltip((Text)Text.translatable(this.tooltipTranslationKey)),
            (optionText, value) -> {
                if (value > 0)
                    return (Text)Text.translatable(this.translationKeyValue, value);
                return (Text)Text.translatable(this.translationKeyOff, value);
            },
            new SimpleOption.ValidatingIntSliderCallbacks(this.minValue, this.maxValue),
            Codec.intRange(this.minValue, this.maxValue),
            this.defaultValue,
            value -> {}
        );
    }

    @Override
    public void setValue(Integer value) {
        value = Math.max(this.minValue, Math.min(this.maxValue, value));

        super.setValue(value);
    }
}
