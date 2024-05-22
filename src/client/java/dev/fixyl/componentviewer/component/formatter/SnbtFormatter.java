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

package dev.fixyl.componentviewer.component.formatter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.component.Component;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.Config;

public class SnbtFormatter extends AbstractFormatter {
    private NbtOrderedStringFormatter generalFormatter;

    public SnbtFormatter() {
        this.initializeNewFormatter();
    }

    private void initializeNewFormatter() {
        this.generalFormatter = new NbtOrderedStringFormatter(this.getIndentPrefix(), 0, Lists.newArrayList());
    }

    @Override
    public void setIndentSize(Integer indentSize) {
        if (this.getIndentSize() == indentSize)
            return;

        super.setIndentSize(indentSize);

        this.initializeNewFormatter();
    }

    public List<Text> formatGeneral(Component<?> component) {
        AbstractFormatter.setFormattingError(false);
        this.setIndentSize(Config.INDENT_SIZE.getValue());

        List<Text> textList = new ArrayList<Text>(AbstractFormatter.INITIAL_TEXT_LIST_CAPACITY);

        NbtElement nbtElement = component.encode(ComponentViewer.minecraftClient.player.getRegistryManager().getOps(NbtOps.INSTANCE)).getOrThrow();
        String[] lineArray = this.generalFormatter.apply(nbtElement).split("\n");

        for (String line : lineArray) {
            textList.add((Text)Text.literal(" " + line).formatted(AbstractFormatter.GENERAL_FORMATTING));
        }

        return textList;
    }
}
