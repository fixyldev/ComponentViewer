package dev.fixyl.componentviewer.screen;

import net.minecraft.client.gui.screens.Screen;

import dev.fixyl.componentviewer.config.Configs;

public class MainConfigScreen extends ConfigScreen {

    public MainConfigScreen(Screen lastScreen, Configs configs) {
        super(lastScreen, configs, "componentviewer.config.title");
    }

    @Override
    protected void addElements() {
        this.addRedirect("componentviewer.config.tooltip", () -> new TooltipConfigScreen(this, this.configs));
        this.addRedirect("componentviewer.config.clipboard", () -> new ClipboardConfigScreen(this, this.configs));
        this.addRedirect("componentviewer.config.controls", () -> new ControlsConfigScreen(this, this.configs));
        this.addConfig(this.configs.disableMod);
    }
}
