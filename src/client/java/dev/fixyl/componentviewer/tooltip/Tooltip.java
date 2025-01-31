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

package dev.fixyl.componentviewer.tooltip;

import java.util.List;
import java.util.Map;

import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.fixyl.componentviewer.component.Components;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.FormattingException;
import dev.fixyl.componentviewer.option.TooltipComponents;

public class Tooltip {
    private static final Style HEADER_STYLE = Style.EMPTY.withColor(Formatting.GRAY);

    private static final Style COMPONENT_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);
    private static final Style SELECTED_COMPONENT_STYLE = Style.EMPTY.withColor(Formatting.DARK_GREEN);
    private static final Style REMOVED_COMPONENT_STYLE = Style.EMPTY.withStrikethrough(true);

    private static final Style ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);

    private static final String CONTENT_INDENTATION = " ";

    private static final Map<TooltipComponents, String> COMPONENT_SELECTION_TRANSLATION_KEYS = Map.of(
        TooltipComponents.ALL, "componentviewer.tooltip.purpose.components.selection.all",
        TooltipComponents.DEFAULT, "componentviewer.tooltip.purpose.components.selection.default",
        TooltipComponents.CHANGES, "componentviewer.tooltip.purpose.components.selection.changes"
    );

    private static final Map<TooltipComponents, String> EMPTY_COMPONENT_SELECTION_TRANSLATION_KEYS = Map.of(
        TooltipComponents.ALL, "componentviewer.tooltip.purpose.components.selection.all.empty",
        TooltipComponents.DEFAULT, "componentviewer.tooltip.purpose.components.selection.default.empty",
        TooltipComponents.CHANGES, "componentviewer.tooltip.purpose.components.selection.changes.empty"
    );

    private final List<Text> lines;

    public Tooltip(List<Text> lines) {
        this.lines = lines;
    }

    public int size() {
        return this.lines.size();
    }

    public boolean isEmpty() {
        return this.lines.isEmpty();
    }

    public Tooltip addSpacer() {
        this.lines.add(Text.empty());

        return this;
    }

    public Tooltip addHeader(String translationKey) {
        this.lines.add(Text.translatable(translationKey).fillStyle(Tooltip.HEADER_STYLE));

        return this;
    }

    public Tooltip addComponentSelection(Components components, int indexOfSelected) {
        if (components.isEmpty()) {
            this.addHeader(Tooltip.EMPTY_COMPONENT_SELECTION_TRANSLATION_KEYS.get(components.componentsType()));
            return this;
        }

        this.addHeader(Tooltip.COMPONENT_SELECTION_TRANSLATION_KEYS.get(components.componentsType()));

        // Double the indentation if more than one component needs to be displayed
        String indentationOfSelected = Tooltip.CONTENT_INDENTATION.repeat(Math.min(components.size(), 2));

        // Add all component types
        for (int index = 0; index < components.size(); index++) {
            MutableText componentTypeText = Text.literal(components.get(index).type().toString()).fillStyle(Tooltip.COMPONENT_STYLE);

            if (index == indexOfSelected) {
                componentTypeText.fillStyle(Tooltip.SELECTED_COMPONENT_STYLE);
            }

            if (components.isRemovedComponent(index)) {
                componentTypeText.fillStyle(Tooltip.REMOVED_COMPONENT_STYLE);
            }

            this.lines.add(Text.literal((index == indexOfSelected) ? indentationOfSelected : Tooltip.CONTENT_INDENTATION).append(componentTypeText));
        }

        return this;
    }

    public <T> Tooltip addComponentValue(Component<T> component, Formatter formatter, int formattingIndentation, boolean coloredFormatting) {
        this.addHeader("componentviewer.tooltip.purpose.components.value");

        try {
            this.lines.addAll(formatter.componentToText(component, formattingIndentation, coloredFormatting, Tooltip.CONTENT_INDENTATION));
        } catch (FormattingException e) {
            this.addFormattingException();
        }

        return this;
    }

    public Tooltip addItemStack(ItemStack itemStack, Formatter formatter, int formattingIndentation, boolean coloredFormatting) {
        this.addHeader("componentviewer.tooltip.purpose.item_stack");

        try {
            this.lines.addAll(formatter.itemStackToText(itemStack, formattingIndentation, coloredFormatting, Tooltip.CONTENT_INDENTATION));
        } catch (FormattingException e) {
            this.addFormattingException();
        }

        return this;
    }

    private void addFormattingException() {
        this.lines.add(Text.literal(Tooltip.CONTENT_INDENTATION).append(Text.translatable("componentviewer.tooltip.formatting_exception").fillStyle(Tooltip.ERROR_STYLE)));
    }
}
