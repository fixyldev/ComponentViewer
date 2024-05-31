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

import dev.fixyl.componentviewer.config.type.AbstractConfig;
import dev.fixyl.componentviewer.config.type.BooleanConfig;
import dev.fixyl.componentviewer.config.type.DisplayConfig;
import dev.fixyl.componentviewer.config.type.IntegerConfig;
import dev.fixyl.componentviewer.config.type.ModeConfig;
import dev.fixyl.componentviewer.option.DisplayOption;
import dev.fixyl.componentviewer.option.ModeOption;

public final class Config {
    private Config() {}

    public static final AbstractConfig<ModeOption> MODE = new ModeConfig(ModeOption.SNBT, "componentviewer.config.mode", "componentviewer.config.mode.tooltip");
    public static final AbstractConfig<DisplayOption> DISPLAY = new DisplayConfig(DisplayOption.HOLD, "componentviewer.config.display", "componentviewer.config.display.tooltip");
    public static final AbstractConfig<Integer> INDENT_SIZE = new IntegerConfig(4, 0, 8, "componentviewer.config.indent_size", "componentviewer.config.indent_size.tooltip", "componentviewer.config.indent_size.value", "componentviewer.config.indent_size.off");
    public static final AbstractConfig<Boolean> COLORED_SNBT = new BooleanConfig(false, "componentviewer.config.colored_snbt", "componentviewer.config.colored_snbt.tooltip");
    public static final AbstractConfig<Boolean> COMPONENT_CHANGES = new BooleanConfig(false, "componentviewer.config.component_changes", "componentviewer.config.component_changes.tooltip");
    public static final AbstractConfig<Boolean> COMPONENT_VALUES = new BooleanConfig(true, "componentviewer.config.component_values", "componentviewer.config.component_values.tooltip");
    public static final AbstractConfig<Boolean> ADVANCED_TOOLTIPS = new BooleanConfig(false, "componentviewer.config.advanced_tooltips", "componentviewer.config.advanced_tooltips.tooltip");
}
