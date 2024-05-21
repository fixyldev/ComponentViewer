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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import dev.fixyl.componentviewer.option.*;

@Environment(EnvType.CLIENT)
public class ConfigJson {
    public ModeOption mode;
    public DisplayOption display;
    public Integer indent_size;
    public Boolean changed_components;
    public Boolean advanced_tooltips;
    public Boolean ignore_errors;

    public void setConfigValues() {
        Config.MODE.setValue(this.mode);
        Config.DISPLAY.setValue(this.display);
        Config.INDENT_SIZE.setValue(this.indent_size);
        Config.CHANGED_COMPONENTS.setValue(this.changed_components);
        Config.ADVANCED_TOOLTIPS.setValue(this.advanced_tooltips);
        Config.IGNORE_ERRORS.setValue(this.ignore_errors);
    }

    public static ConfigJson getConfigValues() {
        ConfigJson configJson = new ConfigJson();

        configJson.mode = Config.MODE.getValue();
        configJson.display = Config.DISPLAY.getValue();
        configJson.indent_size = Config.INDENT_SIZE.getValue();
        configJson.changed_components = Config.CHANGED_COMPONENTS.getValue();
        configJson.advanced_tooltips = Config.ADVANCED_TOOLTIPS.getValue();
        configJson.ignore_errors = Config.IGNORE_ERRORS.getValue();

        return configJson;
    }
}
