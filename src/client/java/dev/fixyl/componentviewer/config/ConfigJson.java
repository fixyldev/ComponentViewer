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

import dev.fixyl.componentviewer.option.DisplayOption;
import dev.fixyl.componentviewer.option.ModeOption;

public class ConfigJson {
    private ModeOption mode;
    private DisplayOption display;
    private Integer indentSize;
    private Boolean coloredSnbt;
    private Boolean componentChanges;
    private Boolean componentValues;
    private Boolean advancedTooltips;

    public void setConfigValues() {
        Config.MODE.setValue(this.mode);
        Config.DISPLAY.setValue(this.display);
        Config.INDENT_SIZE.setValue(this.indentSize);
        Config.COLORED_SNBT.setValue(this.coloredSnbt);
        Config.COMPONENT_CHANGES.setValue(this.componentChanges);
        Config.COMPONENT_VALUES.setValue(this.componentValues);
        Config.ADVANCED_TOOLTIPS.setValue(this.advancedTooltips);
    }

    public static ConfigJson getConfigValues() {
        ConfigJson configJson = new ConfigJson();

        configJson.mode = Config.MODE.getValue();
        configJson.display = Config.DISPLAY.getValue();
        configJson.indentSize = Config.INDENT_SIZE.getValue();
        configJson.coloredSnbt = Config.COLORED_SNBT.getValue();
        configJson.componentChanges = Config.COMPONENT_CHANGES.getValue();
        configJson.componentValues = Config.COMPONENT_VALUES.getValue();
        configJson.advancedTooltips = Config.ADVANCED_TOOLTIPS.getValue();

        return configJson;
    }
}
