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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.config.option.AdvancedOption;

public abstract class ConfigScreen extends SimpleOptionsScreen {
    private static final int WIDGET_WIDTH = 150;

    private final Map<ClickableWidget, AdvancedOption<?>> advancedOptions;

    protected ConfigScreen(Screen parentScreen, @Nullable String translationKey) {
        super(
            parentScreen,
            MinecraftClient.getInstance().options,
            Text.translatable(Objects.toString(translationKey)),
            new SimpleOption<?>[0]
        );

        this.advancedOptions = new HashMap<>();
    }

    @Override
    protected void init() {
        super.init();

        List<ClickableWidget> widgets = this.getWidgets();
        this.buttonList.addAll(widgets);
    }

    protected final <T> ClickableWidget createConfigWidget(AdvancedOption<T> option) {
        ClickableWidget optionWidget = option.createWidget(
            0,
            0,
            ConfigScreen.WIDGET_WIDTH,
            value -> this.updateOptionWidgets()
        );

        ConfigScreen.updateOptionWidget(optionWidget, option);

        this.advancedOptions.put(optionWidget, option);

        return optionWidget;
    }

    protected final List<ClickableWidget> createConfigWidgets(AdvancedOption<?>... options) {
        return Arrays.stream(options)
                     .map(this::createConfigWidget)
                     .toList();
    }

    protected final ClickableWidget createRedirectWidget(@Nullable String translationKey, Supplier<Screen> screenSupplier) {
        return ButtonWidget.builder(
            Text.translatable(Objects.toString(translationKey)),
            buttonWidget -> this.client.setScreen(screenSupplier.get())
        ).build();
    }

    protected abstract List<ClickableWidget> getWidgets();

    private final void updateOptionWidgets() {
        this.advancedOptions.forEach(ConfigScreen::updateOptionWidget);
    }

    private static final <T> void updateOptionWidget(ClickableWidget optionWidget, AdvancedOption<T> option) {
        boolean active = option.isDependencyFulfilled();

        optionWidget.active = active;
        optionWidget.setTooltip((active) ? option.getTooltip() : null);
    }
}
