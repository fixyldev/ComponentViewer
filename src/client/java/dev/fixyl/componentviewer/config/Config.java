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

package dev.fixyl.componentviewer.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.TooltipFactory;
import net.minecraft.client.option.SimpleOption.ValueTextGetter;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;

public abstract class Config<T> {
    private static final String ID_REGEX = "^[a-z]++(?:_[a-z]++)*+(?:\\.[a-z]++(?:_[a-z]++)*+)*+$";

    protected final String id;
    protected final T defaultValue;
    protected final String nameTranslationKey;
    protected final TooltipFactory<T> tooltipFactory;
    protected final ValueTextGetter<T> valueTextGetter;
    protected final BooleanSupplier dependencyFulfilledSupplier;

    protected SimpleOption<T> simpleOption;

    protected Config(AbstractConfigBuilder<T, ?, ?> builder) {
        Config.assertNull(builder.id, builder.defaultValue);

        if (!builder.id.matches(Config.ID_REGEX)) {
            ComponentViewer.LOGGER.error("Invalid config id '{}'! Config ids should follow the regex {}", builder.id, Config.ID_REGEX);
            throw new IllegalArgumentException("Invalid config id");
        }

        this.id = builder.id;
        this.defaultValue = builder.defaultValue;
        this.nameTranslationKey = (builder.nameTranslationKey == null) ? builder.id : builder.nameTranslationKey;
        this.tooltipFactory = (builder.tooltipTranslationKey == null) ? SimpleOption.emptyTooltip() : SimpleOption.constantTooltip(Text.translatable(builder.tooltipTranslationKey));
        this.valueTextGetter = this.createValueTextGetter(builder.translationKeyOverwrite);
        this.dependencyFulfilledSupplier = builder.dependencyFulfilledSupplier;
    }

    public Type type() {
        Type type = getClass().getGenericSuperclass();

        if (type instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getActualTypeArguments()[0];
        }

        return null;
    }

    public String id() {
        return this.id;
    }

    public T value() {
        return this.simpleOption.getValue();
    }

    public T defaultValue() {
        return this.defaultValue;
    }

    public void setValue(T value) {
        if (value == null) {
            this.setValueToDefault();
            return;
        }

        this.simpleOption.setValue(value);
    }

    public void setValueToDefault() {
        this.simpleOption.setValue(this.defaultValue);
    }

    public boolean isDependent() {
        return this.dependencyFulfilledSupplier != null;
    }

    Tooltip getTooltip() {
        return this.tooltipFactory.apply(this.value());
    }

    protected abstract ValueTextGetter<T> getDefaultValueTextGetter();

    private ValueTextGetter<T> createValueTextGetter(Function<T, String> translationKeyOverwrite) {
        if (translationKeyOverwrite == null) {
            return this.getDefaultValueTextGetter();
        }

        return (optionText, value) -> {
            String translationKey = translationKeyOverwrite.apply(value);

            if (translationKey == null) {
                return Text.literal(String.valueOf((Object) null));
            }

            return Text.translatable(translationKey, value);
        };
    }

    protected abstract SimpleOption<T> createSimpleOption();

    protected static void assertNull(Object... objects) {
        for (Object obj : objects) {
            if (obj != null) {
                continue;
            }

            ComponentViewer.LOGGER.error("At least one necessary config parameter is missing or null!");
            throw new IllegalArgumentException("Config parameter missing");
        }
    }

    public abstract static class AbstractConfigBuilder<T, C extends Config<T>, B extends AbstractConfigBuilder<T, C, B>> {
        protected ConfigManager configManager;
        protected String id;
        protected T defaultValue;
        protected String nameTranslationKey;
        protected String tooltipTranslationKey;
        protected Function<T, String> translationKeyOverwrite;
        protected BooleanSupplier dependencyFulfilledSupplier;

        protected AbstractConfigBuilder(String id) {
            this.id = id;
        }

        public B setConfigManager(ConfigManager configManager) {
            this.configManager = configManager;
            return this.self();
        }

        public B setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this.self();
        }

        public B setTranslationKeys(String nameTranslationKey, String tooltipTranslationKey) {
            this.nameTranslationKey = nameTranslationKey;
            this.tooltipTranslationKey = tooltipTranslationKey;
            return this.self();
        }

        public B setTranslationKeys(String nameTranslationKey) {
            return this.setTranslationKeys(nameTranslationKey, null);
        }

        public B setTranslationKeyOverwrite(Function<T, String> translationKeyOverwrite) {
            this.translationKeyOverwrite = translationKeyOverwrite;
            return this.self();
        }

        public B setDependency(BooleanSupplier dependencyFulfilledSupplier) {
            this.dependencyFulfilledSupplier = dependencyFulfilledSupplier;
            return this.self();
        }

        public abstract C build();

        protected abstract B self();
    }
}
