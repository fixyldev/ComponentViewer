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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;

public abstract class AdvancedOption<T> {
    private static final String ID_REGEX = "^[a-z]++(?:_[a-z]++)*+(?:\\.[a-z]++(?:_[a-z]++)*+)*+$";

    protected SimpleOption<T> simpleOption;

    protected final String id;
    protected final T defaultValue;
    protected final String translationKey;
    protected final SimpleOption.TooltipFactory<T> tooltipFactory;
    @Nullable protected final Function<T, String> translationKeyOverwrite;
    @Nullable protected final BooleanSupplier dependencyFulfillmentSupplier;
    protected final Consumer<T> changeCallback;

    protected AdvancedOption(AdvancedOptionBuilder<T, ?, ?> builder) {
        Objects.requireNonNull(builder.id, "Option id not specified");

        if (!builder.id.matches(AdvancedOption.ID_REGEX)) {
            throw new IllegalArgumentException(String.format("Invalid option id '%s'", builder.id));
        }

        this.id = builder.id;
        this.defaultValue = Objects.requireNonNull(builder.defaultValue, "Default value not specified");
        this.translationKey = Objects.toString(builder.translationKey);
        this.tooltipFactory = (builder.descriptionTranslationKey != null) ? SimpleOption.constantTooltip(Text.translatable(builder.descriptionTranslationKey)) : SimpleOption.emptyTooltip();
        this.translationKeyOverwrite = builder.translationKeyOverwrite;
        this.dependencyFulfillmentSupplier = builder.dependencyFulfillmentSupplier;
        this.changeCallback = Objects.requireNonNullElse(builder.changeCallback, value -> {});
    }

    protected final void postConstruct() {
        this.simpleOption = this.createSimpleOption(
            this.translationKey,
            this.tooltipFactory,
            AdvancedOption.createValueTextGetter(this.translationKeyOverwrite, this::getDefaultValueTextGetter),
            this.defaultValue,
            this.changeCallback
        );
    }

    public Type getType() {
        Type type = this.getClass().getGenericSuperclass();

        if (type instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getActualTypeArguments()[0];
        }

        throw new IllegalStateException("AdvancedOption doesn't have a type although it's necessary?");
    }

    public SimpleOption<T> getSimpleOption() {
        return this.simpleOption;
    }

    public String getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public T getValue() {
        return this.simpleOption.getValue();
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void setValue(@Nullable T value) {
        if (value == null) {
            this.resetValue();
            return;
        }

        this.simpleOption.setValue(value);
    }

    public void resetValue() {
        this.simpleOption.setValue(this.defaultValue);
    }

    public Tooltip getTooltip() {
        return this.tooltipFactory.apply(this.getValue());
    }

    public boolean isDependent() {
        return this.dependencyFulfillmentSupplier != null;
    }

    public boolean isDependencyFulfilled() {
        return !this.isDependent() || this.dependencyFulfillmentSupplier.getAsBoolean();
    }

    protected abstract SimpleOption<T> createSimpleOption(String translationkey, SimpleOption.TooltipFactory<T> tooltipFactory, SimpleOption.ValueTextGetter<T> valueTextGetter, T defaultValue, Consumer<T> changeCallback);

    protected abstract SimpleOption.ValueTextGetter<T> getDefaultValueTextGetter();

    private static <T> SimpleOption.ValueTextGetter<T> createValueTextGetter(@Nullable Function<T, String> translationKeyOverwrite, Supplier<SimpleOption.ValueTextGetter<T>> defaultSupplier) {
        if (translationKeyOverwrite == null) {
            return defaultSupplier.get();
        }

        return (optionText, value) -> {
            String translationKey = translationKeyOverwrite.apply(value);

            if (translationKey == null) {
                return Text.literal(Objects.toString(null));
            }

            return Text.translatable(translationKey, value);
        };
    }

    public abstract static class AdvancedOptionBuilder<T, O extends AdvancedOption<T>, B extends AdvancedOptionBuilder<T, O, B>> {
        protected String id;
        protected T defaultValue;
        @Nullable protected String translationKey;
        @Nullable protected String descriptionTranslationKey;
        @Nullable protected Function<T, String> translationKeyOverwrite;
        @Nullable protected BooleanSupplier dependencyFulfillmentSupplier;
        @Nullable protected Consumer<T> changeCallback;

        protected AdvancedOptionBuilder(String id) {
            this.id = id;
        }

        public B setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this.self();
        }

        public B setTranslationKey(@Nullable String translationKey) {
            this.translationKey = translationKey;
            return this.self();
        }

        public B setDescriptionTranslationKey(@Nullable String descriptionTranslationKey) {
            this.descriptionTranslationKey = descriptionTranslationKey;
            return this.self();
        }

        public B setTranslationKeyOverwrite(@Nullable Function<T, String> translationKeyOverwrite) {
            this.translationKeyOverwrite = translationKeyOverwrite;
            return this.self();
        }

        public B setDependency(@Nullable BooleanSupplier dependencyFulfillmentSupplier) {
            this.dependencyFulfillmentSupplier = dependencyFulfillmentSupplier;
            return this.self();
        }

        public B setChangeCallback(@Nullable Consumer<T> changeCallback) {
            this.changeCallback = changeCallback;
            return this.self();
        }

        public abstract O build();

        protected abstract B self();
    }
}
