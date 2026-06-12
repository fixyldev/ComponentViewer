package dev.fixyl.componentviewer.config.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.OptionInstance.CaptionBasedToString;
import net.minecraft.client.OptionInstance.TooltipSupplier;
import net.minecraft.client.OptionInstance.ValueUpdateListener;

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
    protected OptionInstance<Boolean> createOptionInstance(String translationkey, TooltipSupplier<Boolean> tooltipSupplier, CaptionBasedToString<Boolean> captionBasedToString, Boolean defaultValue, ValueUpdateListener<Boolean> onValueChanged) {
        return OptionInstance.createBoolean(
            translationkey,
            tooltipSupplier,
            captionBasedToString,
            defaultValue,
            onValueChanged
        );
    }

    @Override
    protected CaptionBasedToString<Boolean> getDefaultCaptionBasedToString() {
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
