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

package dev.fixyl.componentviewer.config.option;

import java.util.Objects;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.ValueTextGetter;
import net.minecraft.text.Text;

public class IntegerOption extends AdvancedOption<Integer> {
    private final Integer minValue;
    private final Integer maxValue;

    private IntegerOption(IntegerOptionBuilder builder) {
        super(builder);

        Objects.requireNonNull(builder.minValue, "Min value not specified");
        Objects.requireNonNull(builder.maxValue, "Max value not specified");

        if (builder.minValue > builder.maxValue) {
            throw new IllegalArgumentException("Min value is greater than max value");
        }

        if (this.defaultValue < builder.minValue || this.defaultValue > builder.maxValue) {
            throw new IllegalArgumentException(String.format("Default value not within specified range: %s to %s", builder.minValue, builder.maxValue));
        }

        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;

        this.postConstruct();
    }

    public int getIntValue() {
        Integer value = this.getValue();
        return (value != null) ? value.intValue() : 0;
    }

    public int getIntDefaultValue() {
        return (this.defaultValue != null) ? this.defaultValue.intValue() : 0;
    }

    @Override
    protected SimpleOption<Integer> createSimpleOption(String translationkey, SimpleOption.TooltipFactory<Integer> tooltipFactory, SimpleOption.ValueTextGetter<Integer> valueTextGetter, Integer defaultValue, Consumer<Integer> changeCallback) {
        return new SimpleOption<>(
                translationkey,
                tooltipFactory,
                valueTextGetter,
                new SimpleOption.ValidatingIntSliderCallbacks(this.minValue, this.maxValue),
                Codec.intRange(this.minValue, this.maxValue),
                defaultValue,
                changeCallback
        );
    }

    @Override
    protected ValueTextGetter<Integer> getDefaultValueTextGetter() {
        return (optionText, value) -> Text.empty();
    }

    public static IntegerOptionBuilder create(String id) {
        return new IntegerOptionBuilder(id);
    }

    public static class IntegerOptionBuilder extends AdvancedOptionBuilder<Integer, IntegerOption, IntegerOptionBuilder> {
        private Integer minValue;
        private Integer maxValue;

        public IntegerOptionBuilder(String id) {
            super(id);
        }

        public IntegerOptionBuilder setIntegerRange(Integer minValue, Integer maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            return this;
        }

        @Override
        public IntegerOption build() {
            return new IntegerOption(this);
        }

        @Override
        protected IntegerOptionBuilder self() {
            return this;
        }
    }
}
