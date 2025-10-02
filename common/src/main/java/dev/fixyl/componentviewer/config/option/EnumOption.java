package dev.fixyl.componentviewer.config.option;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import com.mojang.serialization.Codec;

import net.minecraft.client.OptionInstance;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public class EnumOption<E extends Enum<E> & OptionEnum> extends AdvancedOption<E> {

    private final Class<E> enumClass;
    private final IntFunction<E> enumByIdFunction;

    private EnumOption(EnumOptionBuilder<E> builder) {
        super(builder);

        this.enumClass = this.defaultValue.getDeclaringClass();
        this.enumByIdFunction = ByIdMap.continuous(E::getId, this.getEnumConstants(), ByIdMap.OutOfBoundsStrategy.WRAP);

        this.postConstruct();
    }

    @Override
    public Type getType() {
        return this.enumClass;
    }

    public E[] getEnumConstants() {
        return this.enumClass.getEnumConstants();
    }

    public void cycleValue() {
        int nextId = this.option.get().getId() + 1;
        E nextValue = this.getEnumById(nextId);
        this.option.set(nextValue);
    }

    @Override
    protected OptionInstance<E> createOptionInstance(String translationkey, OptionInstance.TooltipSupplier<E> tooltipSupplier, OptionInstance.CaptionBasedToString<E> captionBasedToString, E defaultValue, Consumer<E> changeCallback) {
        return new OptionInstance<>(
            translationkey,
            tooltipSupplier,
            captionBasedToString,
            new OptionInstance.Enum<>(Arrays.asList(this.getEnumConstants()), Codec.INT.xmap(this::getEnumById, E::getId)),
            defaultValue,
            changeCallback
        );
    }

    @Override
    protected OptionInstance.CaptionBasedToString<E> getDefaultCaptionBasedToString() {
        return OptionInstance.forOptionEnum();
    }

    private E getEnumById(int id) {
        return this.enumByIdFunction.apply(id);
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
}
