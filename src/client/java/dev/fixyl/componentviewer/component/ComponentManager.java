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

package dev.fixyl.componentviewer.component;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.Config;
import dev.fixyl.componentviewer.option.DisplayOption;

public final class ComponentManager {
    private static ComponentManager instance;

    private final ComponentDisplay componentDisplay;

    private Components components;
    private int componentIndex;
    private boolean previousAltDown;

    private ComponentManager() {
        this.componentDisplay = ComponentDisplay.getInstance();

        this.componentIndex = 0;
        this.previousAltDown = false;
    }

    public static ComponentManager getInstance() {
        if (ComponentManager.instance == null)
            ComponentManager.instance = new ComponentManager();

        return ComponentManager.instance;
    }

    public void itemTooltipCallbackListener(ItemStack itemStack, TooltipContext tooltipContext, TooltipType tooltipType, List<Text> tooltipLines) {
        if (ComponentViewer.minecraftClient.player == null)
            return;

        if (Config.ADVANCED_TOOLTIPS.getValue() && !tooltipType.isAdvanced())
            return;

        switch (Config.DISPLAY.getValue()) {
            case DisplayOption.NEVER:
                return;
            case DisplayOption.HOLD:
                if (!Screen.hasControlDown())
                    return;
            case DisplayOption.ALWAYS:
                break;
            default:
                throw new IllegalArgumentException("Illegal DisplayOption enum value: " + Config.DISPLAY.getValue());
        }

        this.components = Components.getComponents(itemStack);

        this.swapComponentIndex();

        if (!this.componentDisplay.displayComponentTypes(this.components, this.componentIndex, tooltipLines))
            return;

        if (!Config.COMPONENT_VALUES.getValue())
            return;

        if (!this.components.modifiedComponents().isEmpty())
            this.componentDisplay.displayComponentValue(this.components.modifiedComponents().get(this.componentIndex), tooltipLines);
    }

    private void swapComponentIndex() {
        if (!Config.COMPONENT_VALUES.getValue())
            return;

        if (!Screen.hasAltDown())
            this.previousAltDown = false;

        if (!this.previousAltDown && Screen.hasAltDown()) {
            if (Screen.hasShiftDown())
                this.componentIndex--;
            else
                this.componentIndex++;
            this.previousAltDown = true;
        }

        if (this.componentIndex >= this.components.modifiedComponents().size())
            this.componentIndex = 0;
        else if (this.componentIndex < 0)
            this.componentIndex = this.components.modifiedComponents().size() - 1;
    }
}
