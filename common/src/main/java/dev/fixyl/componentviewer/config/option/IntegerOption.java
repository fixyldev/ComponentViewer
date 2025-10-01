package dev.fixyl.componentviewer.config.option;

import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

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
    protected OptionInstance<Integer> createOptionInstance(String translationkey, OptionInstance.TooltipSupplier<Integer> tooltipSupplier, OptionInstance.CaptionBasedToString<Integer> captionBasedToString, Integer defaultValue, Consumer<Integer> changeCallback) {
        OptionInstance.IntRange intRange = new OptionInstance.IntRange(this.minValue, this.maxValue);

        return new OptionInstance<>(
            translationkey,
            tooltipSupplier,
            captionBasedToString,
            intRange,
            intRange.codec(),
            defaultValue,
            changeCallback
        );
    }

    @Override
    protected OptionInstance.CaptionBasedToString<Integer> getDefaultCaptionBasedToString() {
        return (optionText, value) -> Component.empty();
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
