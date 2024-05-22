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

package dev.fixyl.componentviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.MinecraftClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.fixyl.componentviewer.component.ComponentManager;
import dev.fixyl.componentviewer.config.ConfigManager;
import dev.fixyl.componentviewer.keybind.KeyBindings;
import dev.fixyl.componentviewer.screen.ConfigScreen;

public class ComponentViewer implements ClientModInitializer {
	public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
	public static final FabricLoader fabricLoader = FabricLoader.getInstance();

	public static final Logger logger = LoggerFactory.getLogger("ComponentViewer");

	public static final ConfigManager configManager = new ConfigManager();
	public static final ComponentManager componentManager = new ComponentManager();

	public static final KeyBindings keyBindings = new KeyBindings();

	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, tooltipLines) -> {
			ComponentViewer.componentManager.itemTooltipCallbackListener(itemStack, tooltipContext, tooltipType, tooltipLines);
		});

		ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
			if (ComponentViewer.keyBindings.CONFIG_KEY.isPressed()) {
				minecraftClient.setScreen(new ConfigScreen(null));
			}
		});
	}
}
