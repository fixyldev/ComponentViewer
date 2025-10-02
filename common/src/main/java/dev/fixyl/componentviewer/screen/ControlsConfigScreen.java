package dev.fixyl.componentviewer.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;

import dev.fixyl.componentviewer.config.Configs;

public class ControlsConfigScreen extends ConfigScreen {

    public ControlsConfigScreen(Screen lastScreen, Configs configs) {
        super(lastScreen, configs, "componentviewer.config.controls.title");
    }

    @Override
    protected void addElements() {
        this.addRedirect("controls.keybinds", () -> new KeyBindsScreen(this, this.options));
        this.addConfigs(
            this.configs.controlsAllowScrolling,
            this.configs.controlsAlternativeCopyModifierKey,
            this.configs.controlsAllowCyclingOptionsWhileInScreen
        );
    }
}
