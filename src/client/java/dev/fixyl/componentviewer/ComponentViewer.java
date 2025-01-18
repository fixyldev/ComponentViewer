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

package dev.fixyl.componentviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.fixyl.componentviewer.config.ConfigManager;
import dev.fixyl.componentviewer.control.ControlFlow;
import dev.fixyl.componentviewer.event.TooltipCallback;
import dev.fixyl.componentviewer.keybind.KeyBindings;
import dev.fixyl.componentviewer.screen.MainConfigScreen;

public final class ComponentViewer implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ComponentViewer");
    public static final ConfigManager CONFIG_MANAGER = new ConfigManager("componentviewer-config.json");

    private final ControlFlow controlFlow;
    private final KeyBindings keyBindings;

    public ComponentViewer() {
        this.controlFlow = new ControlFlow();
        this.keyBindings = new KeyBindings();
    }

    @Override
    public void onInitializeClient() {
        TooltipCallback.EVENT.register(this.controlFlow::onTooltip);

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (this.keyBindings.configKey.isPressed()) {
                minecraftClient.setScreen(new MainConfigScreen(null));
            }
        });
    }
}
