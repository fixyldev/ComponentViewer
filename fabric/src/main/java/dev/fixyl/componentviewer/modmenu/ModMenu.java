package dev.fixyl.componentviewer.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.screen.MainConfigScreen;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return lastScreen -> new MainConfigScreen(lastScreen, ComponentViewer.getInstance().configs);
    }
}
