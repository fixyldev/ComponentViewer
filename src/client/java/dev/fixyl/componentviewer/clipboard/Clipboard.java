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

package dev.fixyl.componentviewer.clipboard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;

import dev.fixyl.componentviewer.component.Components;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.FormattingException;
import dev.fixyl.componentviewer.formatting.SnbtFormatter;
import dev.fixyl.componentviewer.notification.CopyToast;

public class Clipboard {
    private static final String GIVE_COMMAND_BASE = "give @s";

    private final SnbtFormatter snbtFormatter;

    public Clipboard() {
        this.snbtFormatter = new SnbtFormatter();
    }

    public <T> void copyComponentValue(Component<T> component, Formatter formatter, int indentation, boolean successNotification) {
        try {
            String componentString = formatter.componentToString(component, indentation);
            this.setClipboard(componentString);
        } catch (FormattingException e) {
            CopyToast.dispatch(CopyToast.Type.FORMATTING_EXCEPTION);
            return;
        }

        if (successNotification) {
            CopyToast.dispatch(CopyToast.Type.COMPONENT_VALUE);
        }
    }

    public void copyItemStack(ItemStack itemStack, Formatter formatter, int indentation, boolean successNotification) {
        try {
            String itemStackString = formatter.itemStackToString(itemStack, indentation);
            this.setClipboard(itemStackString);
        } catch (FormattingException e) {
            CopyToast.dispatch(CopyToast.Type.FORMATTING_EXCEPTION);
            return;
        }

        if (successNotification) {
            CopyToast.dispatch(CopyToast.Type.ITEM_STACK, itemStack);
        }
    }

    public void copyGiveCommand(ItemStack itemStack, boolean prependSlash, boolean includeCount, boolean successNotification) {
        StringBuilder commandString = new StringBuilder();

        if (prependSlash) {
            commandString.append('/');
        }

        commandString.append(Clipboard.GIVE_COMMAND_BASE)
                     .append(' ')
                     .append(itemStack.getItem());

        Components components = Components.getChangedComponents(itemStack);

        if (!components.isEmpty()) {
            try {
                List<String> componentList = this.createGiveCommandComponentList(components);
                commandString.append(componentList);
            } catch (FormattingException e) {
                CopyToast.dispatch(CopyToast.Type.FORMATTING_EXCEPTION);
                return;
            }
        }

        if (includeCount) {
            commandString.append(' ')
                         .append(itemStack.getCount());
        }

        this.setClipboard(commandString.toString());

        if (successNotification) {
            CopyToast.dispatch(CopyToast.Type.GIVE_COMMAND, itemStack);
        }
    }

    private void setClipboard(String content) {
        MinecraftClient.getInstance().keyboard.setClipboard(content);
    }

    private List<String> createGiveCommandComponentList(Components components) {
        List<String> componentList = new ArrayList<>(components.size());

        StringBuilder componentString = new StringBuilder();

        for (int index = 0; index < components.size(); index++) {
            Component<?> component = components.get(index);

            // Skip components that are not encoded and therefore
            // cannot be used in give commands
            if (component.type().getCodec() == null) {
                continue;
            }

            componentString.setLength(0);

            if (components.isRemovedComponent(index)) {
                componentString.append('!')
                               .append(component.type());
            } else {
                componentString.append(component.type())
                               .append('=')
                               .append(this.snbtFormatter.componentToString(component, 0));
            }

            componentList.add(componentString.toString());
        }

        return componentList;
    }
}
