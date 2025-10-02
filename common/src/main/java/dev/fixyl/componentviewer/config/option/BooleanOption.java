package dev.fixyl.componentviewer.config.option;

import java.util.function.Consumer;

import net.minecraft.client.OptionInstance;

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
    protected OptionInstance<Boolean> createOptionInstance(String translationkey, OptionInstance.TooltipSupplier<Boolean> tooltipSupplier, OptionInstance.CaptionBasedToString<Boolean> captionBasedToString, Boolean defaultValue, Consumer<Boolean> changeCallback) {
        return OptionInstance.createBoolean(
            translationkey,
            tooltipSupplier,
            captionBasedToString,
            defaultValue,
            changeCallback
        );
    }

    @Override
    protected OptionInstance.CaptionBasedToString<Boolean> getDefaultCaptionBasedToString() {
        return OptionInstance.BOOLEAN_TO_STRING;
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
