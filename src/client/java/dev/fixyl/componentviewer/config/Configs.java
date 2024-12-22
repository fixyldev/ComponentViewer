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

package dev.fixyl.componentviewer.config;

import dev.fixyl.componentviewer.config.type.BooleanConfig;
import dev.fixyl.componentviewer.config.type.EnumConfig;
import dev.fixyl.componentviewer.config.type.IntegerConfig;
import dev.fixyl.componentviewer.option.TooltipsDisplay;
import dev.fixyl.componentviewer.option.TooltipsFormatting;

public final class Configs {
    private Configs() {}

    public static final EnumConfig<TooltipsDisplay> TOOLTIPS_DISPLAY = EnumConfig.builder(TooltipsDisplay.class, "tooltips.display")
            .setDefaultValue(TooltipsDisplay.HOLD)
            .setTranslationKeys("componentviewer.config.tooltips.display", "componentviewer.config.tooltips.display.tooltip")
            .build();
    public static final EnumConfig<TooltipsFormatting> TOOLTIPS_FORMATTING = EnumConfig.builder(TooltipsFormatting.class, "tooltips.formatting")
            .setDefaultValue(TooltipsFormatting.SNBT)
            .setTranslationKeys("componentviewer.config.tooltips.formatting", "componentviewer.config.tooltips.formatting.tooltip")
            .setDependency(() -> Configs.TOOLTIPS_DISPLAY.value() != TooltipsDisplay.NEVER)
            .build();
    public static final BooleanConfig TOOLTIPS_COMPONENT_CHANGES = BooleanConfig.builder("tooltips.component_changes")
            .setDefaultValue(false)
            .setTranslationKeys("componentviewer.config.tooltips.component_changes", "componentviewer.config.tooltips.component_changes.tooltip")
            .setDependency(() -> Configs.TOOLTIPS_DISPLAY.value() != TooltipsDisplay.NEVER)
            .build();
    public static final BooleanConfig TOOLTIPS_COMPONENT_VALUES = BooleanConfig.builder("tooltips.component_values")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.tooltips.component_values", "componentviewer.config.tooltips.component_values.tooltip")
            .setDependency(() -> Configs.TOOLTIPS_DISPLAY.value() != TooltipsDisplay.NEVER)
            .build();
    public static final IntegerConfig TOOLTIPS_INDENTATION = IntegerConfig.builder("tooltips.indentation")
            .setDefaultValue(4)
            .setIntegerRange(0, 8)
            .setTranslationKeys("componentviewer.config.tooltips.indentation", "componentviewer.config.tooltips.indentation.tooltip")
            .setTranslationKeyOverwrite(value -> (value == 0) ? "componentviewer.config.tooltips.indentation.off" : "componentviewer.config.tooltips.indentation.value")
            .setDependency(() -> Configs.TOOLTIPS_DISPLAY.value() != TooltipsDisplay.NEVER && Configs.TOOLTIPS_COMPONENT_VALUES.booleanValue())
            .build();
    public static final BooleanConfig TOOLTIPS_COLORED_SNBT = BooleanConfig.builder("tooltips.colored_snbt")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.tooltips.colored_snbt", "componentviewer.config.tooltips.colored_snbt.tooltip")
            .setDependency(() -> Configs.TOOLTIPS_DISPLAY.value() != TooltipsDisplay.NEVER && Configs.TOOLTIPS_FORMATTING.value() == TooltipsFormatting.SNBT && Configs.TOOLTIPS_COMPONENT_VALUES.booleanValue())
            .build();
    public static final BooleanConfig TOOLTIPS_ADVANCED_TOOLTIPS = BooleanConfig.builder("tooltips.advanced_tooltips")
            .setDefaultValue(false)
            .setTranslationKeys("componentviewer.config.tooltips.advanced_tooltips", "componentviewer.config.tooltips.advanced_tooltips.tooltip")
            .setDependency(() -> Configs.TOOLTIPS_DISPLAY.value() != TooltipsDisplay.NEVER)
            .build();
}
