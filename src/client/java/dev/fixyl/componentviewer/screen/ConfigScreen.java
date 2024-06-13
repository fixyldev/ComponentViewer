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

package dev.fixyl.componentviewer.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.Config;

public class ConfigScreen extends GameOptionsScreen {
    public ConfigScreen(Screen parentScreen) {
        super(parentScreen, ComponentViewer.minecraftClient.options, Text.translatable("componentviewer.config.title"));
    }

    @Override
    protected void addOptions() {
        this.body.addAll(
            Config.MODE.getSimpleOption(),
            Config.DISPLAY.getSimpleOption(),
            Config.INDENT_SIZE.getSimpleOption(),
            Config.COLORED_SNBT.getSimpleOption(),
            Config.COMPONENT_CHANGES.getSimpleOption(),
            Config.COMPONENT_VALUES.getSimpleOption(),
            Config.ADVANCED_TOOLTIPS.getSimpleOption()
        );
    }
}
