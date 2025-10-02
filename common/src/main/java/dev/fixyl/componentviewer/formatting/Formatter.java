package dev.fixyl.componentviewer.formatting;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public interface Formatter {

    static final Style NO_COLOR_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);

    <T> String componentToString(TypedDataComponent<T> component, int indentation, String linePrefix);

    <T> List<Component> componentToText(TypedDataComponent<T> component, int indentation, boolean colored, String linePrefix);

    String itemStackToString(ItemStack itemStack, int indentation, String linePrefix);

    List<Component> itemStackToText(ItemStack itemStack, int indentation, boolean colored, String linePrefix);

    default <T> String componentToString(TypedDataComponent<T> component, int indentation) {
        return this.componentToString(component, indentation, "");
    }

    default <T> List<Component> componentToText(TypedDataComponent<T> component, int indentation, boolean colored) {
        return this.componentToText(component, indentation, colored, "");
    }

    default String itemStackToString(ItemStack itemStack, int indentation) {
        return this.itemStackToString(itemStack, indentation, "");
    }

    default List<Component> itemStackToText(ItemStack itemStack, int indentation, boolean colored) {
        return this.itemStackToText(itemStack, indentation, colored, "");
    }
}
