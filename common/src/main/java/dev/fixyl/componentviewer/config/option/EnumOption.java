package dev.fixyl.componentviewer.config.option;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import com.mojang.serialization.Codec;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.OptionInstance.CaptionBasedToString;
import net.minecraft.client.OptionInstance.TooltipSupplier;
import net.minecraft.client.OptionInstance.ValueUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public class EnumOption<E extends Enum<E> & EnumOption.OptionEnum> extends AdvancedOption<E> {

    private final Class<E> enumClass;
    private final List<E> enumConstants;

    private EnumOption(EnumOptionBuilder<E> builder) {
        super(builder);

        this.enumClass = this.defaultValue.getDeclaringClass();
        this.enumConstants = List.of(this.enumClass.getEnumConstants());

        this.postConstruct();
    }

    @Override
    public Type getType() {
        return this.enumClass;
    }

    public void cycleValue() {
        E currentValue = this.option.get();
        int currentIndex = this.enumConstants.indexOf(currentValue);

        int nextIndex = (currentIndex + 1) % this.enumConstants.size();
        E nextValue = this.enumConstants.get(nextIndex);

        this.setValue(nextValue);
    }

    @Override
    protected OptionInstance<E> createOptionInstance(String translationkey, TooltipSupplier<E> tooltipSupplier, CaptionBasedToString<E> captionBasedToString, E defaultValue, ValueUpdateListener<E> onValueChanged) {
        return new OptionInstance<>(
            translationkey,
            tooltipSupplier,
            captionBasedToString,
            new OptionInstance.Enum<>(this.enumConstants, OptionEnum.getCodec(this.enumClass)),
            defaultValue,
            onValueChanged
        );
    }

    @Override
    protected CaptionBasedToString<E> getDefaultCaptionBasedToString() {
        return (enumOptionName, enumValue) -> enumValue.getCaption();
    }

    public static <E extends Enum<E> & OptionEnum> EnumOptionBuilder<E> create(String id) {
        return new EnumOptionBuilder<>(id);
    }

    public static class EnumOptionBuilder<E extends Enum<E> & OptionEnum> extends AdvancedOptionBuilder<E, EnumOption<E>, EnumOptionBuilder<E>> {

        public EnumOptionBuilder(String id) {
            super(id);
        }

        @Override
        public EnumOption<E> build() {
            return new EnumOption<>(this);
        }

        @Override
        protected EnumOptionBuilder<E> self() {
            return this;
        }
    }

    public static interface OptionEnum extends StringRepresentable {

        String getTranslationKey();

        default Component getCaption() {
            return Component.translatable(this.getTranslationKey());
        }

        static <E extends Enum<E> & OptionEnum> Codec<E> getCodec(Class<E> enumClass) {
            return StringRepresentable.fromEnum(enumClass::getEnumConstants);
        }

        static <E extends Enum<E> & OptionEnum> String createSerializedName(E enumValue) {
            String enumConstantName = enumValue.name();

            Class<E> enumClass = enumValue.getDeclaringClass();

            try {
                Field field = enumClass.getField(enumConstantName);
                SerializedName serializedName = field.getAnnotation(SerializedName.class);

                return (serializedName == null) ? enumConstantName : serializedName.value();
            } catch (NoSuchFieldException _) {
                throw new EnumConstantNotPresentException(enumClass, enumConstantName);
            }
        }
    }
}
