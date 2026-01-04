package dev.fixyl.componentviewer.control.keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.control.component.ItemStackComponents;
import dev.fixyl.componentviewer.control.notification.CopyToast;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.FormattingException;
import dev.fixyl.componentviewer.formatting.SnbtFormatter;

public class Clipboard {

    private static final String GIVE_COMMAND_BASE = "give";

    private final SnbtFormatter snbtFormatter;

    public Clipboard() {
        this.snbtFormatter = new SnbtFormatter();
    }

    public <T> void copyComponentValue(TypedDataComponent<T> component, Formatter formatter, int indentation, boolean successNotification) {
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

    public void copyGiveCommand(ItemStack itemStack, String targetSelector, boolean prependSlash, boolean includeCount, boolean explicitGive, boolean successNotification) {
        StringBuilder commandString = new StringBuilder();

        if (prependSlash) {
            commandString.append('/');
        }

        commandString.append(GIVE_COMMAND_BASE).append(' ')
            .append(targetSelector).append(' ')
            .append(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));

        try {
            List<String> componentList = null;
            ItemStackComponents patchedComponents = ItemStackComponents.getPatchedComponents(itemStack);

            if (explicitGive) {
                ItemStackComponents allComponents = ItemStackComponents.getComponents(itemStack);

                componentList = this.createGiveCommandComponentList(allComponents);
                componentList.addAll(this.createGiveCommandComponentList(
                    patchedComponents,
                    ItemStackComponents::wasRemoved
                ));
            } else {
                componentList = this.createGiveCommandComponentList(patchedComponents);
            }

            if (!componentList.isEmpty()) {
                commandString.append(componentList);
            }
        } catch (FormattingException e) {
            CopyToast.dispatch(CopyToast.Type.FORMATTING_EXCEPTION);
            return;
        }

        if (includeCount) {
            commandString.append(' ').append(itemStack.getCount());
        }

        this.setClipboard(commandString.toString());

        if (successNotification) {
            CopyToast.dispatch(CopyToast.Type.GIVE_COMMAND, itemStack);
        }
    }

    private void setClipboard(String content) {
        Minecraft.getInstance().keyboardHandler.setClipboard(content);
    }

    private List<String> createGiveCommandComponentList(
        ItemStackComponents components,
        BiPredicate<ItemStackComponents, DataComponentType<?>> filter
    ) {
        List<String> componentList = new ArrayList<>(components.size());

        StringBuilder componentString = new StringBuilder();

        for (DataComponentType<?> dataComponentType : components.getComponentTypes()) {
            // Skip components that can't be encoded and
            // skip components which fail the filter predicate
            if (
                dataComponentType.codec() == null
                || !filter.test(components, dataComponentType)
            ) {
                continue;
            }

            componentString.setLength(0);

            if (components.wasRemoved(dataComponentType)) {
                componentString.append('!').append(dataComponentType);
            } else {
                componentString.append(dataComponentType).append('=').append(
                    this.snbtFormatter.componentToString(components.getTypedComponent(dataComponentType), 0)
                );
            }

            componentList.add(componentString.toString());
        }

        return componentList;
    }

    private List<String> createGiveCommandComponentList(ItemStackComponents components) {
        return this.createGiveCommandComponentList(
            components,
            (itemStackComponents, dataComponentType) -> true
        );
    }
}
