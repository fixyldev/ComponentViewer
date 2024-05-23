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
import java.util.Optional;

import net.minecraft.component.Component;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.component.ComponentDisplay;
import dev.fixyl.componentviewer.config.Config;

public class SnbtFormatter extends AbstractFormatter {
    private NbtTextFormatter nbtTextFormatter;

    private MutableText textPart;

    private boolean colored;

    public SnbtFormatter() {
        this.initializeNewFormatter();
    }

    private void initializeNewFormatter() {
        this.nbtTextFormatter = new NbtTextFormatter(this.getIndentPrefix());
    }

    @Override
    public void setIndentSize(Integer indentSize) {
        if (this.getIndentSize() == indentSize)
            return;

        super.setIndentSize(indentSize);

        this.initializeNewFormatter();
    }

    public List<Text> formatComponent(Component<?> component, boolean colored) {
        this.colored = colored;

        this.setIndentSize(Config.INDENT_SIZE.getValue());

        NbtElement nbtElement = component.encode(ComponentViewer.minecraftClient.player.getRegistryManager().getOps(NbtOps.INSTANCE)).getOrThrow();
        Text text = this.nbtTextFormatter.apply(nbtElement);

        return this.processText(text);
    }

    private List<Text> processText(Text text) {
        this.textList = new ArrayList<Text>(AbstractFormatter.INITIAL_TEXT_LIST_CAPACITY);

        this.textPart = Text.literal(ComponentDisplay.GENERAL_INDENT_PREFIX);
        text.visit((style, string) -> this.processSegment(style, string), Style.EMPTY);

        this.textList.add((Text)this.textPart);

        return this.textList;
    }

    private Optional<Object> processSegment(Style style, String string) {
        if (string.equals("\n")) {
            this.textList.add((Text)this.textPart);
            this.textPart = Text.literal(ComponentDisplay.GENERAL_INDENT_PREFIX);

            return Optional.empty();
        }

        this.textPart.append((Text)Text.literal(string).setStyle(this.colored ? style : Style.EMPTY.withFormatting(ComponentDisplay.GENERAL_FORMATTING)));

        return Optional.empty();
    }
}
