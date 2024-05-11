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

package dev.fixyl.componentviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComponentViewer implements ClientModInitializer {
	final private static int INITIAL_COMPONENT_LIST_CAPACITY = 16;
	final private static int INDENTATION = 4;

	private static int componentIndex = 0;
	private static boolean previousAltDown = false;
	private static ComponentFormatter componentFormatter = new ComponentFormatter(INDENTATION);

	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register(ComponentViewer::addComponentsTooltip);
	}

	private static void addComponentsTooltip(ItemStack itemStack, TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
		if (!Screen.hasControlDown())
			return;

		ComponentMap componentMap = itemStack.getComponents();

		if (componentMap.size() == 0) {
			lines.add((Text)Text.empty());
			lines.add((Text)Text.translatable("componentviewer.tooltips.components.empty").formatted(Formatting.GRAY));
			return;
		}

		List<Component<?>> componentList = new ArrayList<Component<?>>(ComponentViewer.INITIAL_COMPONENT_LIST_CAPACITY);
		for (Component<?> component : componentMap)
			componentList.add(component);
		componentList.sort(Comparator.comparing(component -> component.type().toString()));

		if (!Screen.hasAltDown())
			ComponentViewer.previousAltDown = false;

		if (!ComponentViewer.previousAltDown && Screen.hasAltDown()) {
			if (Screen.hasShiftDown())
				ComponentViewer.componentIndex--;
			else
				ComponentViewer.componentIndex++;
				ComponentViewer.previousAltDown = true;
		}

		if (ComponentViewer.componentIndex >= componentMap.size())
			ComponentViewer.componentIndex = 0;
		else if (ComponentViewer.componentIndex < 0)
			ComponentViewer.componentIndex = componentMap.size() - 1;

		lines.add((Text)Text.empty());
		lines.add((Text)Text.translatable("componentviewer.tooltips.components.header").formatted(Formatting.GRAY));

		for (int index = 0; index < componentList.size(); index++) {
			String componentType = componentList.get(index).type().toString();

			Text line;
			if (index == ComponentViewer.componentIndex)
				line = (Text)Text.literal("  " + componentType).formatted(Formatting.DARK_GREEN);
			else
				line = (Text)Text.literal(" " + componentType).formatted(Formatting.DARK_GRAY);

			lines.add(line);
		}

		lines.add((Text)Text.empty());
		lines.add((Text)Text.translatable("componentviewer.tooltips.components.value").formatted(Formatting.GRAY));

		String componentValue = componentList.get(ComponentViewer.componentIndex).value().toString();
		for (String componentValuePart : ComponentViewer.componentFormatter.formatComponentValue(componentValue)) {
			lines.add((Text)Text.literal(" " + componentValuePart).formatted(Formatting.DARK_GRAY));
		}
	}
}
