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
import net.minecraft.client.option.SimpleOption.ValueTextGetter;

import dev.fixyl.componentviewer.ComponentViewer;

public class BooleanConfig extends AbstractConfig<Boolean> {
    private BooleanConfig(BooleanConfigBuilder builder) {
        super(builder);

        this.simpleOption = this.createSimpleOption();
    }

    public static BooleanConfigBuilder builder(String id) {
        return new BooleanConfigBuilder(id);
    }

    public boolean booleanValue() {
        Boolean value = this.value();
        return (value != null) && value.booleanValue();
    }

    public boolean booleanDefaultValue() {
        return (this.defaultValue != null) && this.defaultValue.booleanValue();
    }

    @Override
    protected ValueTextGetter<Boolean> getDefaultValueTextGetter() {
        return SimpleOption.BOOLEAN_TEXT_GETTER;
    }

    @Override
    protected SimpleOption<Boolean> createSimpleOption() {
        return SimpleOption.ofBoolean(
            this.nameTranslationKey,
            this.tooltipFactory,
            this.valueTextGetter,
            this.defaultValue,
            value -> ComponentViewer.configManager.writeConfigFile()
        );
    }

    public static class BooleanConfigBuilder extends AbstractConfigBuilder<Boolean, BooleanConfig, BooleanConfigBuilder> {
        private BooleanConfigBuilder(String id) {
            super(id);
        }

        @Override
        public BooleanConfig build() {
            return new BooleanConfig(this);
        }

        @Override
        protected BooleanConfigBuilder self() {
            return this;
        }
    }
}
