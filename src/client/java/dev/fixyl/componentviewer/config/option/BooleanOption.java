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

import java.util.function.Consumer;

import net.minecraft.client.option.SimpleOption;

public class BooleanOption extends AdvancedOption<Boolean> {
    private BooleanOption(BooleanOptionBuilder builder) {
        super(builder);

        this.postConstruct();
    }

    public boolean getBooleanValue() {
        Boolean value = this.getValue();
        return (value != null) && value.booleanValue();
    }

    public boolean getBooleanDefaultValue() {
        return (this.defaultValue != null) && this.defaultValue.booleanValue();
    }

    @Override
    protected SimpleOption<Boolean> createSimpleOption(String translationkey, SimpleOption.TooltipFactory<Boolean> tooltipFactory, SimpleOption.ValueTextGetter<Boolean> valueTextGetter, Boolean defaultValue, Consumer<Boolean> changeCallback) {
        return SimpleOption.ofBoolean(
            translationkey,
            tooltipFactory,
            valueTextGetter,
            defaultValue,
            changeCallback
        );
    }

    @Override
    protected SimpleOption.ValueTextGetter<Boolean> getDefaultValueTextGetter() {
        return SimpleOption.BOOLEAN_TEXT_GETTER;
    }

    public static BooleanOptionBuilder create(String id) {
        return new BooleanOptionBuilder(id);
    }

    public static class BooleanOptionBuilder extends AdvancedOptionBuilder<Boolean, BooleanOption, BooleanOptionBuilder> {
        public BooleanOptionBuilder(String id) {
            super(id);
        }

        @Override
        public BooleanOption build() {
            return new BooleanOption(this);
        }

        @Override
        protected BooleanOptionBuilder self() {
            return this;
        }
    }
}
