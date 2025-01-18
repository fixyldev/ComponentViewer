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
import net.minecraft.component.Component;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;

import dev.fixyl.componentviewer.clipboard.Clipboard;
import dev.fixyl.componentviewer.component.Components;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.ObjectFormatter;
import dev.fixyl.componentviewer.formatting.SnbtFormatter;
import dev.fixyl.componentviewer.option.ClipboardCopy;
import dev.fixyl.componentviewer.option.TooltipDisplay;
import dev.fixyl.componentviewer.tooltip.Tooltip;

public final class ControlFlow {
    private final StateManager stateManager;
    private final Clipboard clipboard;

    private final Formatter snbtFormatter;
    private final Formatter objectFormatter;

    public ControlFlow() {
        this.stateManager = new StateManager();
        this.clipboard = new Clipboard();

        this.snbtFormatter = new SnbtFormatter();
        this.objectFormatter = new ObjectFormatter();
    }

    public void onTooltip(ItemStack itemStack, Tooltip tooltip, TooltipType tooltipType) {
        boolean shouldPerformCopyAction = this.stateManager.shouldPerformCopyAction();

        if (Configs.CLIPBOARD_COPY.value() == ClipboardCopy.GIVE_COMMAND && shouldPerformCopyAction) {
            this.clipboard.copyGiveCommand(itemStack, Configs.CLIPBOARD_PREPEND_SLASH.booleanValue(), Configs.CLIPBOARD_INCLUDE_CLOUNT.booleanValue());
        }

        if (!this.shouldDisplayToolip(tooltipType)) {
            return;
        }

        Components components = this.getComponents(itemStack);
        int selectedComponentIndex = this.stateManager.cycleSelectedComponentIndex(components.size());
        boolean tooltipComponentValues = Configs.TOOLTIP_COMPONENT_VALUES.booleanValue();

        if (!tooltip.isEmpty()) {
            tooltip.addSpacer();
        }

        tooltip.addComponentSelection(components, (tooltipComponentValues) ? selectedComponentIndex : -1);

        if (components.isEmpty() || !tooltipComponentValues) {
            return;
        }

        Component<?> selectedComponent = components.get(selectedComponentIndex);
        int tooltipIndentation = Configs.TOOLTIP_INDENTATION.intValue();

        tooltip.addSpacer().addComponentValue(selectedComponent, this.getTooltipFormatter(selectedComponent), tooltipIndentation, Configs.TOOLTIP_COLORED_VALUES.booleanValue());

        if (Configs.CLIPBOARD_COPY.value() == ClipboardCopy.COMPONENT_VALUE && shouldPerformCopyAction) {
            int clipboardIndentation = Configs.CLIPBOARD_INDENTATION.intValue();

            this.clipboard.copyComponentValue(selectedComponent, this.getClipboardFormatter(selectedComponent), (clipboardIndentation == -1) ? tooltipIndentation : clipboardIndentation);
        }
    }

    private boolean shouldDisplayToolip(TooltipType tooltipType) {
        TooltipDisplay tooltipDisplay = Configs.TOOLTIP_DISPLAY.value();
        if (tooltipDisplay == TooltipDisplay.NEVER || tooltipDisplay == TooltipDisplay.HOLD && !Screen.hasControlDown()) {
            return false;
        }

        return tooltipType.isAdvanced() || !Configs.TOOLTIP_ADVANCED_TOOLTIPS.booleanValue();
    }

    private Components getComponents(ItemStack itemStack) {
        return switch (Configs.TOOLTIP_COMPONENTS.value()) {
            case ALL -> Components.getAllComponents(itemStack);
            case DEFAULT -> Components.getDefaultComponents(itemStack);
            case CHANGES -> Components.getChangedComponents(itemStack);
        };
    }

    private <T> Formatter getTooltipFormatter(Component<T> component) {
        return switch (Configs.TOOLTIP_FORMATTING.value()) {
            case SNBT -> this.snbtFormatter;
            case OBJECT -> (component.value() instanceof NbtComponent) ? this.snbtFormatter : this.objectFormatter;
        };
    }

    private <T> Formatter getClipboardFormatter(Component<T> component) {
        return switch (Configs.CLIPBOARD_FORMATTING.value()) {
            case SYNC -> this.getTooltipFormatter(component);
            case SNBT -> this.snbtFormatter;
            case OBJECT -> (component.value() instanceof NbtComponent) ? this.snbtFormatter : this.objectFormatter;
        };
    }
}
