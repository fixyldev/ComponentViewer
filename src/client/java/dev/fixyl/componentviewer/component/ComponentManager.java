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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.fixyl.componentviewer.component.formatter.AbstractFormatter;
import dev.fixyl.componentviewer.component.formatter.ClassFormatter;
import dev.fixyl.componentviewer.component.formatter.SnbtFormatter;
import dev.fixyl.componentviewer.config.Config;
import dev.fixyl.componentviewer.option.DisplayOption;
import dev.fixyl.componentviewer.option.ModeOption;

public class ComponentManager {
    private static final int INITIAL_COMPONENT_LIST_CAPACITY = 16;
    private static final Formatting CONTRAST_FORMATTING = Formatting.GRAY;

    private final SnbtFormatter snbtFormatter;
    private final ClassFormatter classFormatter;

    private int componentIndex;
	private boolean previousAltDown;

    private List<Component<?>> componentList;
    private List<Text> tooltipLines;

    public ComponentManager() {
        this.snbtFormatter = new SnbtFormatter();
        this.classFormatter = new ClassFormatter();

        this.componentIndex = 0;
        this.previousAltDown = false;
    }

    public void itemTooltipCallbackListener(ItemStack itemStack, TooltipContext tooltipContext, TooltipType tooltipType, List<Text> tooltipLines) {
        if (Config.ADVANCED_TOOLTIPS.getValue() && !tooltipType.isAdvanced())
            return;

        switch (Config.DISPLAY.getValue()) {
            case DisplayOption.NEVER:
                return;
            case DisplayOption.HOLD:
                if (!Screen.hasControlDown())
                    return;
            default:
        }

        this.componentList = this.getComponents(itemStack);
        this.tooltipLines = tooltipLines;

        if (componentList.isEmpty()) {
            this.tooltipLines.add((Text)Text.empty());
			this.tooltipLines.add((Text)Text.translatable(Config.COMPONENT_CHANGES.getValue() ? "componentviewer.tooltips.components.empty.changes" : "componentviewer.tooltips.components.empty.general").formatted(ComponentManager.CONTRAST_FORMATTING));
			return;
        }

        this.swapComponentIndex();

        this.displayComponentTypes();
        this.displayComponentValue();
    }

    private List<Component<?>> getComponents(ItemStack itemStack) {
        List<Component<?>> componentList = new ArrayList<Component<?>>(ComponentManager.INITIAL_COMPONENT_LIST_CAPACITY);
        ComponentMap componentMap = itemStack.getComponents();

        for (Component<?> component : componentMap)
            componentList.add(component);
        componentList.sort(Comparator.comparing(component -> component.type().toString()));

        if (Config.COMPONENT_CHANGES.getValue()) {
            List<Component<?>> defaultComponentList = new ArrayList<Component<?>>(ComponentManager.INITIAL_COMPONENT_LIST_CAPACITY);
            ComponentMap defaultComponentMap = itemStack.getDefaultComponents();

            for (Component<?> defaultComponent : defaultComponentMap)
                defaultComponentList.add(defaultComponent);

            componentList.removeAll(defaultComponentList);
        }

        return componentList;
    }

    private void swapComponentIndex() {
        if (!Screen.hasAltDown())
			this.previousAltDown = false;

		if (!this.previousAltDown && Screen.hasAltDown()) {
			if (Screen.hasShiftDown())
                this.componentIndex--;
			else
                this.componentIndex++;
            this.previousAltDown = true;
		}

		if (this.componentIndex >= this.componentList.size())
            this.componentIndex = 0;
		else if (this.componentIndex < 0)
            this.componentIndex = this.componentList.size() - 1;
    }

    private void displayComponentTypes() {
		this.tooltipLines.add((Text)Text.empty());
		this.tooltipLines.add((Text)Text.translatable(Config.COMPONENT_CHANGES.getValue() ? "componentviewer.tooltips.components.header.changes" : "componentviewer.tooltips.components.header.general").formatted(ComponentManager.CONTRAST_FORMATTING));

		for (int index = 0; index < this.componentList.size(); index++) {
			String componentType = this.componentList.get(index).type().toString();

			Text line;
			if (index == this.componentIndex)
				line = (Text)Text.literal((this.componentList.size() == 1 ? " " : "  ") + componentType).formatted(Formatting.DARK_GREEN);
			else
				line = (Text)Text.literal(" " + componentType).formatted(Formatting.DARK_GRAY);

			this.tooltipLines.add(line);
		}
    }

    private void displayComponentValue() {
        Component<?> component = this.componentList.get(this.componentIndex);
        List<Text> textList = new ArrayList<Text>(AbstractFormatter.INITIAL_TEXT_LIST_CAPACITY);

        if (Config.MODE.getValue() == ModeOption.SNBT) {
            if (component.type().getCodec() == null) {
                this.tooltipLines.add((Text)Text.empty());
                this.tooltipLines.add((Text)Text.translatable("componentviewer.tooltips.components.error.no_codec").formatted(ComponentManager.CONTRAST_FORMATTING));
                return;
            }

            textList = this.snbtFormatter.formatGeneral(component);
        } else {
            textList = this.classFormatter.formatGeneral(component);
        }

        this.tooltipLines.add((Text)Text.empty());
		this.tooltipLines.add((Text)Text.translatable("componentviewer.tooltips.components.value").formatted(ComponentManager.CONTRAST_FORMATTING));

        this.tooltipLines.addAll(textList);

        if (AbstractFormatter.isFormattingError())
            this.tooltipLines.add((Text)Text.translatable("componentviewer.tooltips.components.error.formatting").formatted(ComponentManager.CONTRAST_FORMATTING));
    }
}
