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
import dev.fixyl.componentviewer.config.type.DisplayConfig;
import dev.fixyl.componentviewer.config.type.IntegerConfig;
import dev.fixyl.componentviewer.config.type.ModeConfig;
import dev.fixyl.componentviewer.option.DisplayOption;
import dev.fixyl.componentviewer.option.ModeOption;

public final class Configs {
    private Configs() {}

    public static final ModeConfig MODE = ModeConfig.createBuilder("tooltips.mode")
            .setDefaultValue(ModeOption.SNBT)
            .setTranslationKeys("componentviewer.config.mode", "componentviewer.config.mode.tooltip")
            .build();
    public static final DisplayConfig DISPLAY = DisplayConfig.createBuilder("tooltips.display")
            .setDefaultValue(DisplayOption.HOLD)
            .setTranslationKeys("componentviewer.config.display", "componentviewer.config.display.tooltip")
            .build();
    public static final IntegerConfig INDENT_SIZE = IntegerConfig.createBuilder("tooltips.indent_size")
            .setDefaultValue(4)
            .setIntegerRange(0, 8)
            .setTranslationKeys("componentviewer.config.indent_size", "componentviewer.config.indent_size.tooltip")
            .setTranslationKeyOverwrite(value -> {
                if (value == 0)
                    return "componentviewer.config.indent_size.off";

                return "componentviewer.config.indent_size.value";
            })
            .build();
    public static final BooleanConfig COLORED_SNBT = BooleanConfig.createBuilder("tooltips.colored_snbt")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.colored_snbt", "componentviewer.config.colored_snbt.tooltip")
            .build();
    public static final BooleanConfig COMPONENT_CHANGES = BooleanConfig.createBuilder("tooltips.component_changes")
            .setDefaultValue(false)
            .setTranslationKeys("componentviewer.config.component_changes", "componentviewer.config.component_changes.tooltip")
            .build();
    public static final BooleanConfig COMPONENT_VALUES = BooleanConfig.createBuilder("tooltips.component_values")
            .setDefaultValue(true)
            .setTranslationKeys("componentviewer.config.component_values", "componentviewer.config.component_values.tooltip")
            .build();
    public static final BooleanConfig ADVANCED_TOOLTIPS = BooleanConfig.createBuilder("tooltips.advanced_tooltips")
            .setDefaultValue(false)
            .setTranslationKeys("componentviewer.config.advanced_tooltips", "componentviewer.config.advanced_tooltips.tooltip")
            .build();
}
