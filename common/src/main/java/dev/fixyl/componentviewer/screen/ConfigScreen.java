package dev.fixyl.componentviewer.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.option.AdvancedOption;

public abstract class ConfigScreen extends OptionsSubScreen {

    private static final int WIDGET_WIDTH = 150;

    protected final Configs configs;

    private final List<AbstractWidget> queuedWidgets;
    private final Map<AbstractWidget, AdvancedOption<?>> advancedOptions;

    protected ConfigScreen(Screen lastScreen, Configs configs, @NullPermitted String translationKey) {
        super(
            lastScreen,
            Minecraft.getInstance().options,
            Component.translatable(Objects.toString(translationKey))
        );

        this.configs = configs;

        this.queuedWidgets = new ArrayList<>();
        this.advancedOptions = new HashMap<>();
    }

    protected final <T> void addConfig(AdvancedOption<T> advancedOption) {
        AbstractWidget optionWidget = advancedOption.createWidget(
            0,
            0,
            WIDGET_WIDTH,
            value -> this.updateOptionWidgets()
        );

        ConfigScreen.updateOptionWidget(optionWidget, advancedOption);

        this.queuedWidgets.add(optionWidget);
        this.advancedOptions.put(optionWidget, advancedOption);
    }

    protected final void addConfigs(AdvancedOption<?>... advancedOptions) {
        for (AdvancedOption<?> advancedOption : advancedOptions) {
            this.addConfig(advancedOption);
        }
    }

    protected final void addRedirect(@NullPermitted String translationKey, Supplier<Screen> screenSupplier) {
        this.queuedWidgets.add(Button.builder(
            Component.translatable(Objects.toString(translationKey)),
            buttonWidget -> this.minecraft.setScreen(screenSupplier.get())
        ).build());
    }

    @Override
    protected final void addOptions() {
        this.addElements();
        this.deployWidgets();
    }

    protected abstract void addElements();

    private final void deployWidgets() {
        this.list.addSmall(this.queuedWidgets);
        this.queuedWidgets.clear();
    }

    private final void updateOptionWidgets() {
        this.advancedOptions.forEach(ConfigScreen::updateOptionWidget);
    }

    private static final <T> void updateOptionWidget(AbstractWidget optionWidget, AdvancedOption<T> advancedOption) {
        boolean active = advancedOption.isDependencyFulfilled();

        optionWidget.active = active;
        optionWidget.setTooltip((active) ? advancedOption.getTooltip() : null);
    }
}
