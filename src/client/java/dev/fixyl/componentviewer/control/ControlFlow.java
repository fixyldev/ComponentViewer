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

package dev.fixyl.componentviewer.control;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;

import dev.fixyl.componentviewer.component.ComponentManager;
import dev.fixyl.componentviewer.component.Components;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.option.TooltipDisplay;
import dev.fixyl.componentviewer.tooltip.Tooltip;

public final class ControlFlow {
    private final ComponentManager componentManager;
    private final StateManager stateManager;

    public ControlFlow() {
        this.componentManager = new ComponentManager();
        this.stateManager = new StateManager();
    }

    public void onTooltip(ItemStack itemStack, Tooltip tooltip, TooltipType tooltipType) {
        if (Configs.TOOLTIP_ADVANCED_TOOLTIPS.booleanValue() && !tooltipType.isAdvanced()) {
            return;
        }

        TooltipDisplay tooltipDisplay = Configs.TOOLTIP_DISPLAY.value();
        if (tooltipDisplay == TooltipDisplay.NEVER || tooltipDisplay == TooltipDisplay.HOLD && !Screen.hasControlDown()) {
            return;
        }

        Components components;
        switch (Configs.TOOLTIP_COMPONENTS.value()) {
            case ALL -> components = this.componentManager.getAllComponents(itemStack);
            case DEFAULT -> components = this.componentManager.getDefaultComponents(itemStack);
            case CHANGES -> components = this.componentManager.getChangedComponents(itemStack);
            default -> throw new IllegalStateException(String.format("Unexpected enum value: %s", Configs.TOOLTIP_COMPONENTS.value()));
        }

        this.stateManager.cycleSelectedIndex(components.size());

        if (!tooltip.isEmpty()) {
            tooltip.addSpacer();
        }

        tooltip.addComponentSelection(components, (Configs.TOOLTIP_COMPONENT_VALUES.booleanValue()) ? this.stateManager.getSelectedIndex() : -1);

        if (!components.isEmpty() && Configs.TOOLTIP_COMPONENT_VALUES.booleanValue()) {
            tooltip.addSpacer().addComponentValue(components.get(this.stateManager.getSelectedIndex()), Configs.TOOLTIP_FORMATTING.value(), Configs.TOOLTIP_INDENTATION.intValue(), Configs.TOOLTIP_COLORED_VALUES.booleanValue());
        }
    }
}
