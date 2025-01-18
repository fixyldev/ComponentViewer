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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public abstract class ConfigScreen extends GameOptionsScreen {
    private static final int WIDGET_WIDTH = 150;

    private List<ClickableWidget> queuedWidgets = new ArrayList<>();
    private Map<ClickableWidget, Config<?>> dependentConfigWidgets = new HashMap<>();

    protected ConfigScreen(Screen parentScreen, String titleTranslationKey) {
        super(parentScreen, MinecraftClient.getInstance().options, Text.translatable(String.valueOf(titleTranslationKey)));
    }

    protected final void addConfig(Config<?> config) {
        ClickableWidget configWidget = config.simpleOption.createWidget(this.gameOptions, 0, 0, ConfigScreen.WIDGET_WIDTH, value -> this.updateDependentConfigWidgets());

        if (config.isDependent()) {
            this.updateDependentConfigWidgetState(configWidget, config);
            this.dependentConfigWidgets.put(configWidget, config);
        }

        this.queuedWidgets.add(configWidget);
    }

    protected final void addConfigs(Config<?>... configs) {
        for (Config<?> config : configs) {
            this.addConfig(config);
        }
    }

    protected final void addRedirect(String nameTranslationKey, Supplier<Screen> screenSupplier) {
        this.queuedWidgets.add(ButtonWidget.builder(Text.translatable(String.valueOf(nameTranslationKey)), buttonWidget -> this.client.setScreen(screenSupplier.get())).build());
    }

    private void deployWidgets() {
        this.body.addAll(this.queuedWidgets);
        this.queuedWidgets.clear();
    }

    protected abstract void addElements();

    @Override
    protected final void addOptions() {
        this.addElements();
        this.deployWidgets();
    }

    private void updateDependentConfigWidgetState(ClickableWidget configWidget, Config<?> config) {
        boolean active = config.dependencyFulfilledSupplier.getAsBoolean();

        configWidget.active = active;
        configWidget.setTooltip((active) ? config.getTooltip() : null);
    }

    private void updateDependentConfigWidgets() {
        this.dependentConfigWidgets.forEach(this::updateDependentConfigWidgetState);
    }
}
