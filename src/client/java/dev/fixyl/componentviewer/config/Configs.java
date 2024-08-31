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
import dev.fixyl.componentviewer.option.DisplayOption;
import dev.fixyl.componentviewer.option.ModeOption;

public final class Configs {
    private Configs() {}

    public static final EnumConfig<DisplayOption> DISPLAY = EnumConfig.builder(DisplayOption.class, "tooltips.display")
            .setDefaultValue(DisplayOption.HOLD)
            .setTranslationKeys("componentviewer.config.tooltips.display", "componentviewer.config.tooltips.display.tooltip")
            .build();
    public static final EnumConfig<ModeOption> MODE = EnumConfig.builder(ModeOption.class, "tooltips.mode")
            .setDefaultValue(ModeOption.SNBT)
            .setTranslationKeys("componentviewer.config.tooltips.mode", "componentviewer.config.tooltips.mode.tooltip")
            .setDependency(() -> Configs.DISPLAY.value() != DisplayOption.NEVER)
            .build();
    public static final BooleanConfig COMPONENT_CHANGES = BooleanConfig.builder("tooltips.component_changes")
            .setDefaultValue(false)
            .setTranslationKeys("componentviewer.config.tooltips.component_changes", "componentviewer.config.tooltips.component_changes.tooltip")
            .setDependency(() -> Configs.DISPLAY.value() != DisplayOption.NEVER)
            .build();
    public static final BooleanConfig COMPONENT_VALUES = BooleanConfig.builder("tooltips.component_values")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.tooltips.component_values", "componentviewer.config.tooltips.component_values.tooltip")
            .setDependency(() -> Configs.DISPLAY.value() != DisplayOption.NEVER)
            .build();
    public static final IntegerConfig INDENT_SIZE = IntegerConfig.builder("tooltips.indent_size")
            .setDefaultValue(4)
            .setIntegerRange(0, 8)
            .setTranslationKeys("componentviewer.config.tooltips.indent_size", "componentviewer.config.tooltips.indent_size.tooltip")
            .setTranslationKeyOverwrite(value -> (value == 0) ? "componentviewer.config.tooltips.indent_size.off" : "componentviewer.config.tooltips.indent_size.value")
            .setDependency(() -> Configs.DISPLAY.value() != DisplayOption.NEVER && Configs.COMPONENT_VALUES.value())
            .build();
    public static final BooleanConfig COLORED_SNBT = BooleanConfig.builder("tooltips.colored_snbt")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.tooltips.colored_snbt", "componentviewer.config.tooltips.colored_snbt.tooltip")
            .setDependency(() -> Configs.DISPLAY.value() != DisplayOption.NEVER && Configs.MODE.value() == ModeOption.SNBT && Configs.COMPONENT_VALUES.value())
            .build();
    public static final BooleanConfig ADVANCED_TOOLTIPS = BooleanConfig.builder("tooltips.advanced_tooltips")
            .setDefaultValue(false)
            .setTranslationKeys("componentviewer.config.tooltips.advanced_tooltips", "componentviewer.config.tooltips.advanced_tooltips.tooltip")
            .setDependency(() -> Configs.DISPLAY.value() != DisplayOption.NEVER)
            .build();
}
