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

import dev.fixyl.componentviewer.config.type.BooleanConfig;
import dev.fixyl.componentviewer.config.type.EnumConfig;
import dev.fixyl.componentviewer.config.type.IntegerConfig;
import dev.fixyl.componentviewer.option.TooltipComponents;
import dev.fixyl.componentviewer.option.TooltipDisplay;
import dev.fixyl.componentviewer.option.TooltipFormatting;

public final class Configs {
    private Configs() {}

    public static final EnumConfig<TooltipDisplay> TOOLTIP_DISPLAY = EnumConfig.builder(TooltipDisplay.class, "tooltip.display")
            .setDefaultValue(TooltipDisplay.HOLD)
            .setTranslationKeys("componentviewer.config.tooltip.display", "componentviewer.config.tooltip.display.description")
            .build();
    public static final EnumConfig<TooltipComponents> TOOLTIP_COMPONENTS = EnumConfig.builder(TooltipComponents.class, "tooltip.components")
            .setDefaultValue(TooltipComponents.ALL)
            .setTranslationKeys("componentviewer.config.tooltip.components", "componentviewer.config.tooltip.components.description")
            .setDependency(() -> Configs.TOOLTIP_DISPLAY.value() != TooltipDisplay.NEVER)
            .build();
    public static final BooleanConfig TOOLTIP_COMPONENT_VALUES = BooleanConfig.builder("tooltip.component_values")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.tooltip.component_values", "componentviewer.config.tooltip.component_values.description")
            .setDependency(() -> Configs.TOOLTIP_DISPLAY.value() != TooltipDisplay.NEVER)
            .build();
    public static final EnumConfig<TooltipFormatting> TOOLTIP_FORMATTING = EnumConfig.builder(TooltipFormatting.class, "tooltip.formatting")
            .setDefaultValue(TooltipFormatting.SNBT)
            .setTranslationKeys("componentviewer.config.tooltip.formatting", "componentviewer.config.tooltip.formatting.description")
            .setDependency(() -> Configs.TOOLTIP_DISPLAY.value() != TooltipDisplay.NEVER && Configs.TOOLTIP_COMPONENT_VALUES.booleanValue())
            .build();
    public static final IntegerConfig TOOLTIP_INDENTATION = IntegerConfig.builder("tooltip.indentation")
            .setDefaultValue(4)
            .setIntegerRange(0, 8)
            .setTranslationKeys("componentviewer.config.tooltip.indentation", "componentviewer.config.tooltip.indentation.description")
            .setTranslationKeyOverwrite(value -> (value == 0) ? "componentviewer.config.tooltip.indentation.off" : "componentviewer.config.tooltip.indentation.value")
            .setDependency(() -> Configs.TOOLTIP_DISPLAY.value() != TooltipDisplay.NEVER && Configs.TOOLTIP_COMPONENT_VALUES.booleanValue())
            .build();
    public static final BooleanConfig TOOLTIP_COLORED_VALUES = BooleanConfig.builder("tooltip.colored_values")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.tooltip.colored_values", "componentviewer.config.tooltip.colored_values.description")
            .setDependency(() -> Configs.TOOLTIP_DISPLAY.value() != TooltipDisplay.NEVER && Configs.TOOLTIP_COMPONENT_VALUES.booleanValue())
            .build();
    public static final BooleanConfig TOOLTIP_ADVANCED_TOOLTIPS = BooleanConfig.builder("tooltip.advanced_tooltips")
            .setDefaultValue(false)
            .setTranslationKeys("componentviewer.config.tooltip.advanced_tooltips", "componentviewer.config.tooltip.advanced_tooltips.description")
            .setDependency(() -> Configs.TOOLTIP_DISPLAY.value() != TooltipDisplay.NEVER)
            .build();
}
