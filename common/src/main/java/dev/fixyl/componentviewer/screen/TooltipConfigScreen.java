package dev.fixyl.componentviewer.screen;

import net.minecraft.client.gui.screens.Screen;

import dev.fixyl.componentviewer.config.Configs;

public class TooltipConfigScreen extends ConfigScreen {

    public TooltipConfigScreen(Screen lastScreen, Configs configs) {
        super(lastScreen, configs, "componentviewer.config.tooltip.title");
    }

    @Override
    protected void addElements() {
        this.addConfigs(
            this.configs.tooltipDisplay,
            this.configs.tooltipPurpose,
            this.configs.tooltipComponents,
            this.configs.tooltipShowAmount,
            this.configs.tooltipComponentValues,
            this.configs.tooltipKeepSelection,
            this.configs.tooltipFormatting,
            this.configs.tooltipIndentation,
            this.configs.tooltipColoredFormatting,
            this.configs.tooltipInjectMethod,
            this.configs.tooltipAdvancedTooltips
        );
    }
}
