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

package dev.fixyl.componentviewer.screen;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.Configs;

// Suppress the SonarQube warning for a too
// deeply nested class inheritance tree
@SuppressWarnings("java:S110")
public class TooltipConfigScreen extends ConfigScreen {
    public TooltipConfigScreen(Screen parentScreen) {
        super(parentScreen, "componentviewer.config.tooltip.title");
    }

    @Override
    protected List<ClickableWidget> getWidgets() {
        Configs configs = ComponentViewer.getInstance().configs;

        return this.createConfigWidgets(
            configs.tooltipDisplay,
            configs.tooltipPurpose,
            configs.tooltipComponents,
            configs.tooltipComponentValues,
            configs.tooltipFormatting,
            configs.tooltipIndentation,
            configs.tooltipColoredFormatting,
            configs.tooltipAdvancedTooltips
        );
    }
}
