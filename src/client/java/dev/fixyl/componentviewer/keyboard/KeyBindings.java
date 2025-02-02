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

package dev.fixyl.componentviewer.keyboard;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import org.lwjgl.glfw.GLFW;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.enums.ClipboardCopy;
import dev.fixyl.componentviewer.config.enums.ClipboardFormatting;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.config.enums.TooltipDisplay;
import dev.fixyl.componentviewer.config.enums.TooltipFormatting;
import dev.fixyl.componentviewer.config.enums.TooltipPurpose;
import dev.fixyl.componentviewer.keyboard.keybinding.EnumOptionKeyBinding;
import dev.fixyl.componentviewer.keyboard.keybinding.AdvancedKeyBinding;
import dev.fixyl.componentviewer.screen.MainConfigScreen;

public final class KeyBindings {
    private static final String GENERAL_CATEGORY = "componentviewer.keybind.general";
    private static final String CONFIG_CATEGORY = "componentviewer.keybind.config";

    private final Configs configs = ComponentViewer.getInstance().configs;

    public KeyBindings() {
        KeyBindings.register(
            this.configScreenKey,
            this.tooltipDisplayConfigKey,
            this.tooltipPurposeConfigKey,
            this.tooltipComponentsConfigKey,
            this.tooltipFormattingConfigKey,
            this.clipboardCopyConfigKey,
            this.clipboardFormattingConfigKey
        );
    }

    public void onClientTick(MinecraftClient minecraftClient) {
        this.configScreenKey.onPressed(() -> minecraftClient.setScreen(new MainConfigScreen(null)));

        this.tooltipDisplayConfigKey.cycleValueOnPressed();
        this.tooltipPurposeConfigKey.cycleValueOnPressed();
        this.tooltipComponentsConfigKey.cycleValueOnPressed();
        this.tooltipFormattingConfigKey.cycleValueOnPressed();
        this.clipboardCopyConfigKey.cycleValueOnPressed();
        this.clipboardFormattingConfigKey.cycleValueOnPressed();
    }

    private static void register(KeyBinding... keyBindings) {
        for (KeyBinding keyBinding : keyBindings) {
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }

    public final AdvancedKeyBinding configScreenKey = new AdvancedKeyBinding(
        "componentviewer.keybind.general.config_screen",
        GLFW.GLFW_KEY_J,
        KeyBindings.GENERAL_CATEGORY
    );
    public final EnumOptionKeyBinding<TooltipDisplay> tooltipDisplayConfigKey = new EnumOptionKeyBinding<>(
        "componentviewer.keybind.config.tooltip_display",
        GLFW.GLFW_KEY_UNKNOWN,
        KeyBindings.CONFIG_CATEGORY,
        this.configs.tooltipDisplay
    );
    public final EnumOptionKeyBinding<TooltipPurpose> tooltipPurposeConfigKey = new EnumOptionKeyBinding<>(
        "componentviewer.keybind.config.tooltip_purpose",
        GLFW.GLFW_KEY_UNKNOWN,
        KeyBindings.CONFIG_CATEGORY,
        this.configs.tooltipPurpose
    );
    public final EnumOptionKeyBinding<TooltipComponents> tooltipComponentsConfigKey = new EnumOptionKeyBinding<>(
        "componentviewer.keybind.config.tooltip_components",
        GLFW.GLFW_KEY_UNKNOWN,
        KeyBindings.CONFIG_CATEGORY,
        this.configs.tooltipComponents
    );
    public final EnumOptionKeyBinding<TooltipFormatting> tooltipFormattingConfigKey = new EnumOptionKeyBinding<>(
        "componentviewer.keybind.config.tooltip_formatting",
        GLFW.GLFW_KEY_UNKNOWN,
        KeyBindings.CONFIG_CATEGORY,
        this.configs.tooltipFormatting
    );
    public final EnumOptionKeyBinding<ClipboardCopy> clipboardCopyConfigKey = new EnumOptionKeyBinding<>(
        "componentviewer.keybind.config.clipboard_copy",
        GLFW.GLFW_KEY_UNKNOWN,
        KeyBindings.CONFIG_CATEGORY,
        this.configs.clipboardCopy
    );
    public final EnumOptionKeyBinding<ClipboardFormatting> clipboardFormattingConfigKey = new EnumOptionKeyBinding<>(
        "componentviewer.keybind.config.clipboard_formatting",
        GLFW.GLFW_KEY_UNKNOWN,
        KeyBindings.CONFIG_CATEGORY,
        this.configs.clipboardFormatting
    );
}
