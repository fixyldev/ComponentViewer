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

package dev.fixyl.componentviewer.config.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.TooltipFactory;
import net.minecraft.client.option.SimpleOption.ValueTextGetter;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;

public abstract class AbstractConfig<T> {
    private static final String ID_REGEX = "^[a-z]++(?:_[a-z]++)*+(?:\\.[a-z]++(?:_[a-z]++)*+)*+$";

    protected final String id;
    protected final T defaultValue;
    protected final String nameTranslationKey;
    protected final TooltipFactory<T> tooltipFactory;
    protected final ValueTextGetter<T> valueTextGetter;
    protected final BooleanSupplier configDependency;

    protected SimpleOption<T> simpleOption;
    protected ClickableWidget configWidget;

    protected AbstractConfig(AbstractConfigBuilder<T, ?, ?> builder) {
        AbstractConfig.assertNull(builder.id, builder.defaultValue);

        if (!builder.id.matches(AbstractConfig.ID_REGEX)) {
            ComponentViewer.logger.error("Invalid config id '{}'! Config ids should follow the regex {}", builder.id, AbstractConfig.ID_REGEX);
            throw new IllegalArgumentException("Invalid config id");
        }

        this.id = builder.id;
        this.defaultValue = builder.defaultValue;
        this.nameTranslationKey = (builder.nameTranslationKey == null) ? builder.id : builder.nameTranslationKey;
        this.tooltipFactory = (builder.tooltipTranslationKey == null) ? SimpleOption.emptyTooltip() : SimpleOption.constantTooltip(Text.translatable(builder.tooltipTranslationKey));
        this.valueTextGetter = this.createValueTextGetter(builder.translationKeyOverwrite);
        this.configDependency = builder.configDependency;
    }

    public Type type() {
        Type type = getClass().getGenericSuperclass();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments()[0];
        }

        return null;
    }

    public String id() {
        return this.id;
    }

    public T value() {
        return this.simpleOption.getValue();
    }

    public T defaultValue() {
        return this.defaultValue;
    }

    public void setValue(T value) {
        if (value == null) {
            this.setValueToDefault();
            return;
        }

        this.simpleOption.setValue(value);
    }

    public void setValueToDefault() {
        this.simpleOption.setValue(this.defaultValue);
    }

    protected abstract ValueTextGetter<T> getDefaultValueTextGetter();

    private ValueTextGetter<T> createValueTextGetter(Function<T, String> translationKeyOverwrite) {
        if (translationKeyOverwrite == null)
            return this.getDefaultValueTextGetter();

        return (optionText, value) -> {
            String translationKey = translationKeyOverwrite.apply(value);

            if (translationKey == null)
                return Text.literal(String.valueOf((Object) null));

            return Text.translatable(translationKey, value);
        };
    }

    protected abstract SimpleOption<T> createSimpleOption();

    private boolean isDependent() {
        return this.configDependency != null;
    }

    private void updateActiveStatus() {
        if (!this.isDependent() || this.configWidget == null)
            return;

        boolean active = this.configDependency.getAsBoolean();

        this.configWidget.active = active;
        this.configWidget.setTooltip((active) ? this.tooltipFactory.apply(this.value()) : null);
    }

    private ClickableWidget getConfigWidget() {
        if (this.configWidget != null)
            return this.configWidget;

        this.configWidget = this.simpleOption.createWidget(ComponentViewer.minecraftClient.options, 0, 0, 150, value -> ConfigScreen.updateDependentConfigs());

        if (this.isDependent()) {
            this.updateActiveStatus();
            ConfigScreen.dependentConfigs.add(this);
        }

        return this.configWidget;
    }

    protected static void assertNull(Object... objects) {
        for (Object obj : objects) {
            if (obj != null)
                continue;

            ComponentViewer.logger.error("At least one necessary config parameter is missing or null!");
            throw new IllegalArgumentException("Config parameter missing");
        }
    }

    public abstract static class AbstractConfigBuilder<T, C extends AbstractConfig<T>, B extends AbstractConfigBuilder<T, C, B>> {
        protected String id;
        protected T defaultValue;
        protected String nameTranslationKey;
        protected String tooltipTranslationKey;
        protected Function<T, String> translationKeyOverwrite;
        protected BooleanSupplier configDependency;

        protected AbstractConfigBuilder(String id) {
            this.id = id;
        }

        public B setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this.self();
        }

        public B setTranslationKeys(String nameTranslationKey, String tooltipTranslationKey) {
            this.nameTranslationKey = nameTranslationKey;
            this.tooltipTranslationKey = tooltipTranslationKey;
            return this.self();
        }

        public B setTranslationKeys(String nameTranslationKey) {
            return this.setTranslationKeys(nameTranslationKey, null);
        }

        public B setTranslationKeyOverwrite(Function<T, String> translationKeyOverwrite) {
            this.translationKeyOverwrite = translationKeyOverwrite;
            return this.self();
        }

        public B setDependency(BooleanSupplier configDependency) {
            this.configDependency = configDependency;
            return this.self();
        }

        public abstract C build();

        protected abstract B self();
    }

    public abstract static class ConfigScreen extends GameOptionsScreen {
        private static Set<AbstractConfig<?>> dependentConfigs = new HashSet<>();
        private static Set<ClickableWidget> deployedConfigWidgets = new HashSet<>();

        private List<ClickableWidget> queuedWidgets = new ArrayList<>();

        protected ConfigScreen(Screen parentScreen, String titleTranslationKey) {
            super(parentScreen, ComponentViewer.minecraftClient.options, Text.translatable(String.valueOf(titleTranslationKey)));
        }

        protected void addConfig(AbstractConfig<?> config) {
            ClickableWidget configWidget = config.getConfigWidget();

            this.queuedWidgets.add(configWidget);
            ConfigScreen.deployedConfigWidgets.add(configWidget);
        }

        protected void addConfigs(AbstractConfig<?>... configs) {
            for (AbstractConfig<?> config : configs)
                this.addConfig(config);
        }

        protected void addRedirect(Screen screen, String nameTranslationKey) {
            this.queuedWidgets.add(ButtonWidget.builder(Text.translatable(String.valueOf(nameTranslationKey)), buttonWidget -> this.client.setScreen(screen)).build());
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

        @Override
        public final void onDisplayed() {
            for (ClickableWidget configWidget : ConfigScreen.deployedConfigWidgets)
                configWidget.setFocused(false);

            if (this.client != null && this.client.getNavigationType().isMouse() && !this.children().isEmpty())
                this.children().getLast().setFocused(false);
        }

        private static void updateDependentConfigs() {
            for (AbstractConfig<?> dependentConfig : ConfigScreen.dependentConfigs)
                dependentConfig.updateActiveStatus();
        }
    }
}
