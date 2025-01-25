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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import com.mojang.serialization.Codec;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.ValueTextGetter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public class EnumOption<E extends Enum<E> & TranslatableOption> extends AdvancedOption<E> {
    private final Class<E> enumClass;
    private final IntFunction<E> enumByIdFunction;

    private EnumOption(EnumOptionBuilder<E> builder) {
        super(builder);

        this.enumClass = this.defaultValue.getDeclaringClass();
        this.enumByIdFunction = ValueLists.createIdToValueFunction(E::getId, this.getEnumConstants(), ValueLists.OutOfBoundsHandling.WRAP);

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
        int nextId = this.simpleOption.getValue().getId() + 1;
        E nextValue = this.getEnumById(nextId);
        this.simpleOption.setValue(nextValue);
    }

    @Override
    protected SimpleOption<E> createSimpleOption(String translationkey, SimpleOption.TooltipFactory<E> tooltipFactory, SimpleOption.ValueTextGetter<E> valueTextGetter, E defaultValue, Consumer<E> changeCallback) {
        return new SimpleOption<>(
                translationkey,
                tooltipFactory,
                valueTextGetter,
                new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(this.getEnumConstants()), Codec.INT.xmap(this::getEnumById, E::getId)),
                defaultValue,
                changeCallback
        );
    }

    @Override
    protected ValueTextGetter<E> getDefaultValueTextGetter() {
        return SimpleOption.enumValueText();
    }

    private E getEnumById(int id) {
        return this.enumByIdFunction.apply(id);
    }

    public static <E extends Enum<E> & TranslatableOption> EnumOptionBuilder<E> create(String id) {
        return new EnumOptionBuilder<>(id);
    }

    public static class EnumOptionBuilder<E extends Enum<E> & TranslatableOption> extends AdvancedOptionBuilder<E, EnumOption<E>, EnumOptionBuilder<E>> {
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
