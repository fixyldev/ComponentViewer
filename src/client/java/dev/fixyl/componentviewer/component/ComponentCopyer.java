package dev.fixyl.componentviewer.component;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.component.formatter.AbstractFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ComponentCopyer {
    public static String getItemString(ItemStack item, List<Component<?>> components) {
        NbtTextFormatter nbtTextFormatter = new NbtTextFormatter("");
        StringBuilder result = new StringBuilder(item.getItem().toString()).append("[");
        for(int i = 0; i < components.size(); i++) {
            Component<?> component = components.get(i);

            List<Text> texts = formatComponent(component);
            StringBuilder subresult = new StringBuilder();
            for(Text text : texts) {
                subresult.append(text.getString());
            }

            result.append(component.type().toString()).append("=").append(subresult.toString().replace("\n", ""));
            if(i != components.size() - 1) {
                result.append(",");
            }
        }
        result.append("] ").append(item.getCount());

        return result.toString();
    }

    public static void copyItem(ItemStack item, List<Component<?>> components) {
        MinecraftClient.getInstance().keyboard.setClipboard(getItemString(item, components));
    }

    private static List<Text> formatComponent(Component<?> component) {
        NbtTextFormatter nbtTextFormatter = new NbtTextFormatter("");

        NbtElement nbtElement = component.encode(ComponentViewer.minecraftClient.player.getRegistryManager().getOps(NbtOps.INSTANCE)).getOrThrow();
        Text text = nbtTextFormatter.apply(nbtElement);

        return processText(text);
    }

    private static List<Text> processText(Text text) {
        ArrayList<Text> textList = new ArrayList<Text>(AbstractFormatter.INITIAL_TEXT_LIST_CAPACITY);

        AtomicReference<MutableText> textPart = new AtomicReference<>(Text.literal(""));
        text.visit((style, str) -> {
            String[] stringArray = str.split("(?=\\n)|(?<=\\n)");

            for (String stringPart : stringArray) {
                if (stringPart.equals("\n")) {
                    textList.add(textPart.get());
                    textPart.set(Text.literal(""));
                    continue;
                }

                textPart.get().append(Text.literal(stringPart).setStyle(ComponentDisplay.COMPONENT_VALUE_GENERAL_STYLE));
            }

            return Optional.empty();
        }, Style.EMPTY);

        textList.add(textPart.get());

        return textList;
    }
}
