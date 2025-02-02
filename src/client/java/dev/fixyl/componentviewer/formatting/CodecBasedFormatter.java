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

package dev.fixyl.componentviewer.formatting;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;

public interface CodecBasedFormatter extends Formatter {
    public <T> String codecToString(T value, @Nullable Codec<T> codec, int indentation, String linePrefix);

    public <T> List<Text> codecToText(T value, @Nullable Codec<T> codec, int indentation, boolean colored, String linePrefix);

    public default <T> String codecToString(T value, @Nullable Codec<T> codec, int indentation) {
        return this.codecToString(value, codec, indentation, "");
    }

    public default <T> List<Text> codecToText(T value, @Nullable Codec<T> codec, int indentation, boolean colored) {
        return this.codecToText(value, codec, indentation, colored, "");
    }

    @Override
    public default <T> String componentToString(Component<T> component, int indentation, String linePrefix) {
        return this.codecToString(component.value(), component.type().getCodec(), indentation, linePrefix);
    }

    @Override
    public default <T> List<Text> componentToText(Component<T> component, int indentation, boolean colored, String linePrefix) {
        return this.codecToText(component.value(), component.type().getCodec(), indentation, colored, linePrefix);
    }

    @Override
    public default String itemStackToString(ItemStack itemStack, int indentation, String linePrefix) {
        return this.codecToString(itemStack, ItemStack.CODEC, indentation, linePrefix);
    }

    @Override
    public default List<Text> itemStackToText(ItemStack itemStack, int indentation, boolean colored, String linePrefix) {
        return this.codecToText(itemStack, ItemStack.CODEC, indentation, colored, linePrefix);
    }
}
