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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.IntFunction;

import com.mojang.serialization.Codec;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.ValueTextGetter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

import dev.fixyl.componentviewer.ComponentViewer;

public class EnumConfig<E extends Enum<E> & TranslatableOption> extends AbstractConfig<E> {
    private final Class<E> enumClass;
    private final IntFunction<E> enumByIdFunction;

    private EnumConfig(EnumConfigBuilder<E> builder) {
        super(builder);

        AbstractConfig.assertNull(builder.enumClass);

        this.enumClass = builder.enumClass;
        this.enumByIdFunction = ValueLists.createIdToValueFunction(E::getId, this.enumValues(), ValueLists.OutOfBoundsHandling.WRAP);

        this.simpleOption = this.createSimpleOption();
    }

    public static <E extends Enum<E> & TranslatableOption> EnumConfigBuilder<E> builder(Class<E> enumClass, String id) {
        return new EnumConfigBuilder<>(enumClass, id);
    }

    @Override
    public Type type() {
        return this.enumClass;
    }

    @Override
    protected ValueTextGetter<E> getDefaultValueTextGetter() {
        return SimpleOption.enumValueText();
    }

    @Override
    protected SimpleOption<E> createSimpleOption() {
        return new SimpleOption<>(
            this.nameTranslationKey,
            this.tooltipFactory,
            this.valueTextGetter,
            new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(this.enumValues()), Codec.INT.xmap(this::enumById, E::getId)),
            this.defaultValue,
            value -> ComponentViewer.configManager.writeConfigFile()
        );
    }

    private E[] enumValues() {
        return this.enumClass.getEnumConstants();
    }

    private E enumById(int id) {
        return this.enumByIdFunction.apply(id);
    }

    public static class EnumConfigBuilder<E extends Enum<E> & TranslatableOption> extends AbstractConfigBuilder<E, EnumConfig<E>, EnumConfigBuilder<E>> {
        private Class<E> enumClass;

        private EnumConfigBuilder(Class<E> enumClass, String id) {
            super(id);

            this.enumClass = enumClass;
        }

        @Override
        public EnumConfig<E> build() {
            return new EnumConfig<>(this);
        }

        @Override
        protected EnumConfigBuilder<E> self() {
            return this;
        }
    }
}
