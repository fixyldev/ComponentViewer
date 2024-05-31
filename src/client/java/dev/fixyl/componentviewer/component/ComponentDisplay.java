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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.fixyl.componentviewer.component.formatter.ClassFormatter;
import dev.fixyl.componentviewer.component.formatter.SnbtFormatter;
import dev.fixyl.componentviewer.config.Config;
import dev.fixyl.componentviewer.option.ModeOption;

public class ComponentDisplay {
    public static final String GENERAL_INDENT_PREFIX = " ";

    public static final Formatting HEADER_FORMATTING = Formatting.GRAY;
    public static final Formatting GENERAL_FORMATTING = Formatting.DARK_GRAY;
    public static final Formatting HIGHLIGHTED_FORMATTING = Formatting.DARK_GREEN;

    private final SnbtFormatter snbtFormatter;
    private final ClassFormatter classFormatter;

    public ComponentDisplay() {
        this.snbtFormatter = new SnbtFormatter();
        this.classFormatter = new ClassFormatter();
    }

    public boolean displayComponentTypes(List<Component<?>> componentList, int componentIndex, List<Text> tooltipLines) {
        tooltipLines.add((Text)Text.empty());

        if (componentList.isEmpty()) {
			tooltipLines.add((Text)Text.translatable(Config.COMPONENT_CHANGES.getValue() ? "componentviewer.tooltips.components.empty.changes" : "componentviewer.tooltips.components.empty.general").formatted(ComponentDisplay.HEADER_FORMATTING));
			return false;
        }

		tooltipLines.add((Text)Text.translatable(Config.COMPONENT_CHANGES.getValue() ? "componentviewer.tooltips.components.header.changes" : "componentviewer.tooltips.components.header.general").formatted(ComponentDisplay.HEADER_FORMATTING));

		for (int index = 0; index < componentList.size(); index++) {
			String componentType = componentList.get(index).type().toString();

			if (index == componentIndex && Config.COMPONENT_VALUES.getValue())
                tooltipLines.add((Text)Text.literal((componentList.size() == 1 ? ComponentDisplay.GENERAL_INDENT_PREFIX : ComponentDisplay.GENERAL_INDENT_PREFIX.repeat(2)) + componentType).formatted(ComponentDisplay.HIGHLIGHTED_FORMATTING));
			else
                tooltipLines.add((Text)Text.literal(ComponentDisplay.GENERAL_INDENT_PREFIX + componentType).formatted(ComponentDisplay.GENERAL_FORMATTING));
		}

        return true;
    }

    public void displayComponentValue(Component<?> component, List<Text> tooltipLines) {
        tooltipLines.add((Text)Text.empty());

        List<Text> textList;

        switch (Config.MODE.getValue()) {
            case ModeOption.SNBT -> {
                if (component.type().getCodec() == null) {
                    tooltipLines.add((Text)Text.translatable("componentviewer.tooltips.components.error.no_codec").formatted(ComponentDisplay.HEADER_FORMATTING));
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

		tooltipLines.add((Text)Text.translatable("componentviewer.tooltips.components.value").formatted(ComponentDisplay.HEADER_FORMATTING));
        tooltipLines.addAll(textList);
    }
}
