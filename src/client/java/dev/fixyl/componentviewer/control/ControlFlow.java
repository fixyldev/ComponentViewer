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

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.clipboard.Clipboard;
import dev.fixyl.componentviewer.component.Components;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.JsonFormatter;
import dev.fixyl.componentviewer.formatting.ObjectFormatter;
import dev.fixyl.componentviewer.formatting.SnbtFormatter;
import dev.fixyl.componentviewer.option.ClipboardCopy;
import dev.fixyl.componentviewer.option.TooltipDisplay;
import dev.fixyl.componentviewer.option.TooltipPurpose;
import dev.fixyl.componentviewer.tooltip.Tooltip;

public final class ControlFlow {
    private final Configs configs;

    private final StateManager stateManager;
    private final Clipboard clipboard;

    private final Formatter snbtFormatter;
    private final Formatter jsonFormatter;
    private final Formatter objectFormatter;

    public ControlFlow(Configs configs) {
        this.configs = configs;

        this.stateManager = new StateManager();
        this.clipboard = new Clipboard();

        this.snbtFormatter = new SnbtFormatter();
        this.jsonFormatter = new JsonFormatter();
        this.objectFormatter = new ObjectFormatter();
    }

    public void onTooltip(ItemStack itemStack, Tooltip tooltip, TooltipType tooltipType) {
        boolean shouldPerformCopyAction = this.stateManager.shouldPerformCopyAction();

        if (shouldPerformCopyAction) {
            ClipboardCopy clipboardCopy = this.configs.clipboardCopy.getValue();

            if (clipboardCopy == ClipboardCopy.ITEM_STACK) {
                this.copyItemStack(itemStack);
            } else if (clipboardCopy == ClipboardCopy.GIVE_COMMAND) {
                this.copyGiveCommand(itemStack);
            }
        }

        if (!this.shouldDisplayToolip(tooltipType)) {
            return;
        }

        if (!tooltip.isEmpty()) {
            tooltip.addSpacer();
        }

        if (this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS) {
            this.handleComponentPurpose(itemStack, tooltip, shouldPerformCopyAction);
        } else {
            this.handleItemStackPurpose(itemStack, tooltip);
        }
    }

    private boolean shouldDisplayToolip(TooltipType tooltipType) {
        TooltipDisplay tooltipDisplay = this.configs.tooltipDisplay.getValue();
        if (tooltipDisplay == TooltipDisplay.NEVER || tooltipDisplay == TooltipDisplay.HOLD && !Screen.hasControlDown()) {
            return false;
        }

        return tooltipType.isAdvanced() || !this.configs.tooltipAdvancedTooltips.getBooleanValue();
    }

    private void handleComponentPurpose(ItemStack itemStack, Tooltip tooltip, boolean shouldPerformCopyAction) {
        Components components = this.getComponents(itemStack);
        int selectedComponentIndex = this.stateManager.cycleSelectedComponentIndex(components.size());
        boolean tooltipComponentValues = this.configs.tooltipComponentValues.getBooleanValue();

        tooltip.addComponentSelection(components, (tooltipComponentValues) ? selectedComponentIndex : -1);

        if (components.isEmpty() || !tooltipComponentValues) {
            return;
        }

        Component<?> selectedComponent = components.get(selectedComponentIndex);

        tooltip.addSpacer().addComponentValue(
            selectedComponent,
            this.getTooltipFormatter(selectedComponent),
            this.getTooltipIndentation(),
            this.configs.tooltipColoredFormatting.getBooleanValue()
        );

        if (shouldPerformCopyAction && this.configs.clipboardCopy.getValue() == ClipboardCopy.COMPONENT_VALUE) {
            this.copyComponentValue(selectedComponent);
        }
    }

    private void handleItemStackPurpose(ItemStack itemStack, Tooltip tooltip) {
        tooltip.addItemStack(
            itemStack,
            this.getTooltipFormatter(),
            this.configs.tooltipIndentation.getIntValue(),
            this.configs.tooltipColoredFormatting.getBooleanValue()
        );
    }

    private <T> void copyComponentValue(Component<T> component) {
        this.clipboard.copyComponentValue(
            component,
            this.getClipboardFormatter(component),
            this.getClipboardIndentation(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private void copyItemStack(ItemStack itemStack) {
        this.clipboard.copyItemStack(
            itemStack,
            this.getClipboardFormatter(),
            this.getClipboardIndentation(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private void copyGiveCommand(ItemStack itemStack) {
        this.clipboard.copyGiveCommand(
            itemStack,
            this.configs.clipboardPrependSlash.getBooleanValue(),
            this.configs.clipboardIncludeCount.getBooleanValue(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private Components getComponents(ItemStack itemStack) {
        return switch (this.configs.tooltipComponents.getValue()) {
            case ALL -> Components.getAllComponents(itemStack);
            case DEFAULT -> Components.getDefaultComponents(itemStack);
            case CHANGES -> Components.getChangedComponents(itemStack);
        };
    }

    private Formatter getTooltipFormatter() {
        return this.getTooltipFormatter(null);
    }

    private <T> Formatter getTooltipFormatter(@Nullable Component<T> component) {
        return switch (this.configs.tooltipFormatting.getValue()) {
            case SNBT -> this.snbtFormatter;
            case JSON -> this.jsonFormatter;
            case OBJECT -> (component != null && component.value() instanceof NbtComponent) ? this.snbtFormatter : this.objectFormatter;
        };
    }

    private Formatter getClipboardFormatter() {
        return this.getClipboardFormatter(null);
    }

    private <T> Formatter getClipboardFormatter(@Nullable Component<T> component) {
        return switch (this.configs.clipboardFormatting.getValue()) {
            case SYNC -> this.getTooltipFormatter(component);
            case SNBT -> this.snbtFormatter;
            case JSON -> this.jsonFormatter;
            case OBJECT -> (component != null && component.value() instanceof NbtComponent) ? this.snbtFormatter : this.objectFormatter;
        };
    }

    private int getTooltipIndentation() {
        return this.configs.tooltipIndentation.getIntValue();
    }

    private int getClipboardIndentation() {
        int clipboardIndentation = this.configs.clipboardIndentation.getIntValue();

        return (clipboardIndentation == -1) ? this.getTooltipIndentation() : clipboardIndentation;
    }
}
