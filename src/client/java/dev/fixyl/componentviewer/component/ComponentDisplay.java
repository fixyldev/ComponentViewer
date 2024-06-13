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

package dev.fixyl.componentviewer.component;

import java.util.List;

import net.minecraft.component.Component;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.fixyl.componentviewer.component.formatter.ClassFormatter;
import dev.fixyl.componentviewer.component.formatter.SnbtFormatter;
import dev.fixyl.componentviewer.config.Config;
import dev.fixyl.componentviewer.option.ModeOption;

public final class ComponentDisplay {
    private static ComponentDisplay instance;

    public static final String GENERAL_INDENT_PREFIX = " ";

    public static final Style HEADER_STYLE = Style.EMPTY.withColor(Formatting.GRAY);

    public static final Style COMPONENT_TYPE_GENERAL_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);
    public static final Style COMPONENT_TYPE_HIGHLIGHTED_STYLE = Style.EMPTY.withColor(Formatting.DARK_GREEN);
    public static final Style COMPONENT_TYPE_REMOVED_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY).withStrikethrough(true);

    public static final Style COMPONENT_VALUE_GENERAL_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);

    private final SnbtFormatter snbtFormatter;
    private final ClassFormatter classFormatter;

    private ComponentDisplay() {
        this.snbtFormatter = new SnbtFormatter();
        this.classFormatter = new ClassFormatter();
    }

    public static ComponentDisplay getInstance() {
        if (ComponentDisplay.instance == null)
            ComponentDisplay.instance = new ComponentDisplay();

        return ComponentDisplay.instance;
    }

    public boolean displayComponentTypes(Components components, int componentIndex, List<Text> tooltipLines) {
        tooltipLines.add(Text.empty());

        if (components.isEmpty()) {
            tooltipLines.add(Text.translatable(Config.COMPONENT_CHANGES.getValue() ? "componentviewer.tooltips.components.empty.changes" : "componentviewer.tooltips.components.empty.general").setStyle(ComponentDisplay.HEADER_STYLE));
            return false;
        }

        tooltipLines.add(Text.translatable(Config.COMPONENT_CHANGES.getValue() ? "componentviewer.tooltips.components.header.changes" : "componentviewer.tooltips.components.header.general").setStyle(ComponentDisplay.HEADER_STYLE));

        if (Config.COMPONENT_CHANGES.getValue())
            this.displayRemovedComponents(components.removedComponents(), tooltipLines);

        this.displayModifiedComponents(components.modifiedComponents(), componentIndex, components.size() > 1, tooltipLines);

        return true;
    }

    public void displayComponentValue(Component<?> component, List<Text> tooltipLines) {
        tooltipLines.add(Text.empty());

        List<Text> textList;

        switch (Config.MODE.getValue()) {
            case ModeOption.SNBT -> {
                if (component.type().getCodec() == null) {
                    tooltipLines.add(Text.translatable("componentviewer.tooltips.components.error.no_codec").setStyle(ComponentDisplay.HEADER_STYLE));
                    return;
                }

                textList = this.snbtFormatter.formatComponent(component, Config.COLORED_SNBT.getValue());
            }

            case ModeOption.CLASS -> {
                if (component.value() instanceof NbtComponent)
                    textList = this.snbtFormatter.formatComponent(component, false);
                else
                    textList = this.classFormatter.formatComponent(component);
            }

            default -> throw new IllegalArgumentException("Illegal ModeOption enum value: " + Config.MODE.getValue());
        }

        tooltipLines.add(Text.translatable("componentviewer.tooltips.components.value").setStyle(ComponentDisplay.HEADER_STYLE));
        tooltipLines.addAll(textList);
    }

    private void displayRemovedComponents(List<Component<?>> removedComponents, List<Text> tooltipLines) {
        for (Component<?> component : removedComponents) {
            MutableText componentTypeText = Text.literal(component.type().toString())
                    .setStyle(ComponentDisplay.COMPONENT_TYPE_REMOVED_STYLE);

            tooltipLines.add(Text.literal(ComponentDisplay.GENERAL_INDENT_PREFIX).append(componentTypeText));
        }
    }

    private void displayModifiedComponents(List<Component<?>> modifiedComponents, int componentIndex, boolean indentOnSelected, List<Text> tooltipLines) {
        for (int index = 0; index < modifiedComponents.size(); index++) {
            MutableText componentTypeText = Text.literal(modifiedComponents.get(index).type().toString());

            if (index == componentIndex && Config.COMPONENT_VALUES.getValue()) {
                componentTypeText.setStyle(ComponentDisplay.COMPONENT_TYPE_HIGHLIGHTED_STYLE);

                if (indentOnSelected)
                    componentTypeText = Text.literal(ComponentDisplay.GENERAL_INDENT_PREFIX).append(componentTypeText);
            } else
                componentTypeText.setStyle(ComponentDisplay.COMPONENT_TYPE_GENERAL_STYLE);

            tooltipLines.add(Text.literal(ComponentDisplay.GENERAL_INDENT_PREFIX).append(componentTypeText));
        }
    }
}
