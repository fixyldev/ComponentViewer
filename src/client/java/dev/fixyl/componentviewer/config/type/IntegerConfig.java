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
import net.minecraft.client.option.SimpleOption.ValueTextGetter;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;

public class IntegerConfig extends AbstractConfig<Integer> {
    private final Integer minValue;
    private final Integer maxValue;

    private IntegerConfig(IntegerConfigBuilder builder) {
        super(builder);

        AbstractConfig.assertNull(builder.minValue, builder.maxValue);

        if (builder.minValue > builder.maxValue) {
            ComponentViewer.logger.error("Invalid integer range {}-{}! The min value must be less than or equal to the max value.", builder.minValue, builder.maxValue);
            throw new IllegalArgumentException("Invalid integer range");
        }

        if (this.defaultValue < builder.minValue || this.defaultValue > builder.maxValue) {
            ComponentViewer.logger.error("Invalid default value {}! The default value must be within the valid integer range {}-{}.", this.defaultValue, builder.minValue, builder.maxValue);
            throw new IllegalArgumentException("Invalid default value");
        }

        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;

        this.simpleOption = this.createSimpleOption();
    }

    public static IntegerConfigBuilder builder(String id) {
        return new IntegerConfigBuilder(id);
    }

    @Override
    public void setValue(Integer value) {
        value = Math.max(this.minValue, Math.min(this.maxValue, value));

        super.setValue(value);
    }

    @Override
    protected ValueTextGetter<Integer> getDefaultValueTextGetter() {
        return (optionText, value) -> Text.empty();
    }

    @Override
    protected SimpleOption<Integer> createSimpleOption() {
        return new SimpleOption<>(
            this.nameTranslationKey,
            this.tooltipFactory,
            this.valueTextGetter,
            new SimpleOption.ValidatingIntSliderCallbacks(this.minValue, this.maxValue),
            Codec.intRange(this.minValue, this.maxValue),
            this.defaultValue,
            value -> ComponentViewer.configManager.writeConfigFile()
        );
    }

    public static class IntegerConfigBuilder extends AbstractConfigBuilder<Integer, IntegerConfig, IntegerConfigBuilder> {
        private Integer minValue;
        private Integer maxValue;

        private IntegerConfigBuilder(String id) {
            super(id);
        }

        public IntegerConfigBuilder setIntegerRange(Integer minValue, Integer maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            return this;
        }

        @Override
        public IntegerConfig build() {
            return new IntegerConfig(this);
        }

        @Override
        protected IntegerConfigBuilder self() {
            return this;
        }
    }
}
