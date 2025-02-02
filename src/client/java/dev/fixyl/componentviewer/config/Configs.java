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

package dev.fixyl.componentviewer.config;

import java.util.EnumSet;

import org.slf4j.Logger;

import dev.fixyl.componentviewer.config.enums.ClipboardCopy;
import dev.fixyl.componentviewer.config.enums.ClipboardFormatting;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.config.enums.TooltipDisplay;
import dev.fixyl.componentviewer.config.enums.TooltipFormatting;
import dev.fixyl.componentviewer.config.enums.TooltipPurpose;
import dev.fixyl.componentviewer.config.option.AdvancedOption;
import dev.fixyl.componentviewer.config.option.BooleanOption;
import dev.fixyl.componentviewer.config.option.EnumOption;
import dev.fixyl.componentviewer.config.option.IntegerOption;
import dev.fixyl.componentviewer.config.option.Options;

public final class Configs implements Options {
    private static final String CONFIG_FILENAME = "componentviewer-config.json";

    private final ConfigManager configManager;

    public Configs(Logger logger) {
        this.configManager = new ConfigManager(Configs.CONFIG_FILENAME, logger);
        this.configManager.addOptions(this);
        this.configManager.readFromFile();
    }

    @Override
    public AdvancedOption<?>[] getOptions() {
        return new AdvancedOption<?>[] {
            this.tooltipDisplay,
            this.tooltipPurpose,
            this.tooltipComponents,
            this.tooltipComponentValues,
            this.tooltipFormatting,
            this.tooltipIndentation,
            this.tooltipColoredFormatting,
            this.tooltipAdvancedTooltips,
            this.clipboardCopy,
            this.clipboardFormatting,
            this.clipboardIndentation,
            this.clipboardPrependSlash,
            this.clipboardIncludeCount,
            this.clipboardSuccessNotification
        };
    }

    private <T> void changeCallback(T value) {
        this.configManager.writeToFile();
    }

    public final EnumOption<TooltipDisplay> tooltipDisplay = EnumOption.<TooltipDisplay>create("tooltip.display")
        .setDefaultValue(TooltipDisplay.HOLD)
        .setTranslationKey("componentviewer.config.tooltip.display")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.display.description")
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipPurpose> tooltipPurpose = EnumOption.<TooltipPurpose>create("tooltip.purpose")
        .setDefaultValue(TooltipPurpose.COMPONENTS)
        .setTranslationKey("componentviewer.config.tooltip.purpose")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.purpose.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER)
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipComponents> tooltipComponents = EnumOption.<TooltipComponents>create("tooltip.components")
        .setDefaultValue(TooltipComponents.ALL)
        .setTranslationKey("componentviewer.config.tooltip.components")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.components.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption tooltipComponentValues = BooleanOption.create("tooltip.component_values")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.tooltip.component_values")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.component_values.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS)
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipFormatting> tooltipFormatting = EnumOption.<TooltipFormatting>create("tooltip.formatting")
        .setDefaultValue(TooltipFormatting.SNBT)
        .setTranslationKey("componentviewer.config.tooltip.formatting")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.formatting.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && ((this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS && this.tooltipComponentValues.getBooleanValue()) || this.tooltipPurpose.getValue() == TooltipPurpose.ITEM_STACK))
        .setChangeCallback(this::changeCallback)
        .build();
    public final IntegerOption tooltipIndentation = IntegerOption.create("tooltip.indentation")
        .setDefaultValue(4)
        .setIntegerRange(0, 8)
        .setTranslationKey("componentviewer.config.tooltip.indentation")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.indentation.description")
        .setTranslationKeyOverwrite(value -> (value == 0) ? "componentviewer.config.tooltip.indentation.off" : "componentviewer.config.tooltip.indentation.value")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && ((this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS && this.tooltipComponentValues.getBooleanValue()) || this.tooltipPurpose.getValue() == TooltipPurpose.ITEM_STACK))
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption tooltipColoredFormatting = BooleanOption.create("tooltip.colored_formatting")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.tooltip.colored_formatting")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.colored_formatting.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && ((this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS && this.tooltipComponentValues.getBooleanValue()) || this.tooltipPurpose.getValue() == TooltipPurpose.ITEM_STACK))
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption tooltipAdvancedTooltips = BooleanOption.create("tooltip.advanced_tooltips")
        .setDefaultValue(false)
        .setTranslationKey("componentviewer.config.tooltip.advanced_tooltips")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.advanced_tooltips.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER)
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<ClipboardCopy> clipboardCopy = EnumOption.<ClipboardCopy>create("clipboard.copy")
        .setDefaultValue(ClipboardCopy.COMPONENT_VALUE)
        .setTranslationKey("componentviewer.config.clipboard.copy")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.copy.description")
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<ClipboardFormatting> clipboardFormatting = EnumOption.<ClipboardFormatting>create("clipboard.formatting")
        .setDefaultValue(ClipboardFormatting.SYNC)
        .setTranslationKey("componentviewer.config.clipboard.formatting")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.formatting.description")
        .setDependency(() -> EnumSet.of(ClipboardCopy.COMPONENT_VALUE, ClipboardCopy.ITEM_STACK).contains(this.clipboardCopy.getValue()))
        .setChangeCallback(this::changeCallback)
        .build();
    public final IntegerOption clipboardIndentation = IntegerOption.create("clipboard.indentation")
        .setDefaultValue(-1)
        .setIntegerRange(-1, 8)
        .setTranslationKey("componentviewer.config.clipboard.indentation")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.indentation.description")
        .setTranslationKeyOverwrite(value -> switch (Integer.signum(value)) {
            case -1 -> "componentviewer.config.clipboard.indentation.sync";
            case 0 -> "componentviewer.config.clipboard.indentation.off";
            case 1 -> "componentviewer.config.clipboard.indentation.value";
            default -> throw new IllegalStateException(String.format("Unexpected int value: %s", value));
        })
        .setDependency(() -> EnumSet.of(ClipboardCopy.COMPONENT_VALUE, ClipboardCopy.ITEM_STACK).contains(this.clipboardCopy.getValue()))
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption clipboardPrependSlash = BooleanOption.create("clipboard.prepend_slash")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.clipboard.prepend_slash")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.prepend_slash.description")
        .setDependency(() -> this.clipboardCopy.getValue() == ClipboardCopy.GIVE_COMMAND)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption clipboardIncludeCount = BooleanOption.create("clipboard.include_count")
        .setDefaultValue(false)
        .setTranslationKey("componentviewer.config.clipboard.include_count")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.include_count.description")
        .setDependency(() -> this.clipboardCopy.getValue() == ClipboardCopy.GIVE_COMMAND)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption clipboardSuccessNotification = BooleanOption.create("clipboard.success_notification")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.clipboard.success_notification")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.success_notification.description")
        .setDependency(() -> this.clipboardCopy.getValue() != ClipboardCopy.DISABLED)
        .setChangeCallback(this::changeCallback)
        .build();
}
