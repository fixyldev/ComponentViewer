package dev.fixyl.componentviewer.screen;

import net.minecraft.client.gui.screens.Screen;

import dev.fixyl.componentviewer.config.Configs;

public class ClipboardConfigScreen extends ConfigScreen {

    public ClipboardConfigScreen(Screen lastScreen, Configs configs) {
        super(lastScreen, configs, "componentviewer.config.clipboard.title");
    }

    @Override
    protected void addElements() {
        this.addConfigs(
            this.configs.clipboardCopy,
            this.configs.clipboardFormatting,
            this.configs.clipboardIndentation,
            this.configs.clipboardSelector,
            this.configs.clipboardPrependSlash,
            this.configs.clipboardIncludeCount,
            this.configs.clipboardSuccessNotification
        );
    }
}
