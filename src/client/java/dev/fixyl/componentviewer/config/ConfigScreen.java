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
import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.config.option.AdvancedOption;

public abstract class ConfigScreen extends GameOptionsScreen {
    private static final int WIDGET_WIDTH = 150;

    private final List<ClickableWidget> queuedWidgets;
    private final Map<ClickableWidget, AdvancedOption<?>> options;

    protected ConfigScreen(Screen parentScreen, @Nullable String translationKey) {
        super(
                parentScreen,
                MinecraftClient.getInstance().options,
                Text.translatable(Objects.toString(translationKey))
        );

        this.queuedWidgets = new ArrayList<>();
        this.options = new HashMap<>();
    }

    protected final void addConfig(AdvancedOption<?> option) {
        ClickableWidget optionWidget = option.getSimpleOption().createWidget(
                this.gameOptions,
                0,
                0,
                ConfigScreen.WIDGET_WIDTH,
                value -> this.updateOptionWidgets()
        );

        ConfigScreen.updateOptionWidget(optionWidget, option);

        this.queuedWidgets.add(optionWidget);
        this.options.put(optionWidget, option);
    }

    protected final void addConfigs(AdvancedOption<?>... options) {
        for (AdvancedOption<?> option : options) {
            this.addConfig(option);
        }
    }

    protected final void addRedirect(@Nullable String translationKey, Supplier<Screen> screenSupplier) {
        this.queuedWidgets.add(ButtonWidget.builder(
                Text.translatable(Objects.toString(translationKey)),
                buttonWidget -> this.client.setScreen(screenSupplier.get())
        ).build());
    }

    @Override
    protected final void addOptions() {
        this.addElements();
        this.deployWidgets();
    }

    protected abstract void addElements();

    private void deployWidgets() {
        this.body.addAll(this.queuedWidgets);
        this.queuedWidgets.clear();
    }

    private void updateOptionWidgets() {
        this.options.forEach(ConfigScreen::updateOptionWidget);
    }

    private static <T> void updateOptionWidget(ClickableWidget optionWidget, AdvancedOption<T> option) {
        boolean active = option.isDependencyFulfilled();

        optionWidget.active = active;
        optionWidget.setTooltip((active) ? option.getTooltip() : null);
    }
}
