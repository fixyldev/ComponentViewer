package dev.fixyl.componentviewer.formatting;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;

public interface CodecBasedFormatter extends Formatter {

    <T> String codecToString(T value, @NullPermitted Codec<T> codec, int indentation, String linePrefix);

    <T> List<Component> codecToText(T value, @NullPermitted Codec<T> codec, int indentation, boolean colored, String linePrefix);

    default <T> String codecToString(T value, @NullPermitted Codec<T> codec, int indentation) {
        return this.codecToString(value, codec, indentation, "");
    }

    default <T> List<Component> codecToText(T value, @NullPermitted Codec<T> codec, int indentation, boolean colored) {
        return this.codecToText(value, codec, indentation, colored, "");
    }

    @Override
    default <T> String componentToString(TypedDataComponent<T> component, int indentation, String linePrefix) {
        return this.codecToString(component.value(), component.type().codec(), indentation, linePrefix);
    }

    @Override
    default <T> List<Component> componentToText(TypedDataComponent<T> component, int indentation, boolean colored, String linePrefix) {
        return this.codecToText(component.value(), component.type().codec(), indentation, colored, linePrefix);
    }

    @Override
    default String itemStackToString(ItemStack itemStack, int indentation, String linePrefix) {
        return this.codecToString(itemStack, ItemStack.CODEC, indentation, linePrefix);
    }

    @Override
    default List<Component> itemStackToText(ItemStack itemStack, int indentation, boolean colored, String linePrefix) {
        return this.codecToText(itemStack, ItemStack.CODEC, indentation, colored, linePrefix);
    }
}
