package dev.fixyl.componentviewer.formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.util.ResultCache;

public class SnbtFormatter implements CodecBasedFormatter {

    private static final String LF = "\n";

    private static final String NO_CODEC_REPR = "{}";
    private static final Style NO_CODEC_REPR_STYLE = Style.EMPTY.withColor(ChatFormatting.WHITE);

    private final ResultCache<String> stringResultCache;
    private final ResultCache<List<Component>> textResultCache;

    public SnbtFormatter() {
        this.stringResultCache = new ResultCache<>();
        this.textResultCache = new ResultCache<>();
    }

    @Override
    public <T> String codecToString(T value, @NullPermitted Codec<T> codec, int indentation, String linePrefix) {
        return this.stringResultCache.cache(() -> {
            if (codec == null) {
                return linePrefix + NO_CODEC_REPR;
            }

            String formattedString = SnbtFormatter.getFormattedText(value, codec, indentation).getString();

            if (!linePrefix.isEmpty()) {
                formattedString = linePrefix + formattedString.replace(LF, System.lineSeparator() + linePrefix);
            } else if (!System.lineSeparator().equals(LF)) {
                formattedString = formattedString.replace(LF, System.lineSeparator());
            }

            return formattedString;
        }, value, codec, indentation, linePrefix);
    }

    @Override
    public <T> List<Component> codecToText(T value, @NullPermitted Codec<T> codec, int indentation, boolean colored, String linePrefix) {
        return Collections.unmodifiableList(this.textResultCache.cache(() -> {
            if (codec != null) {
                Component text = SnbtFormatter.getFormattedText(value, codec, indentation);
                return SnbtFormatter.convertToTextList(text, colored, linePrefix);
            }

            Component noCodecText = Component.literal(NO_CODEC_REPR).withStyle((colored) ? NO_CODEC_REPR_STYLE : NO_COLOR_STYLE);

            if (linePrefix.isEmpty()) {
                return List.of(noCodecText);
            }

            MutableComponent startOfLine = Component.literal(linePrefix);
            if (!colored) {
                startOfLine.withStyle(NO_COLOR_STYLE);
            }

            return List.of(startOfLine.append(noCodecText));
        }, value, codec, indentation, colored, linePrefix));
    }

    private static <T> Component getFormattedText(T value, Codec<T> codec, int indentation) {
        String prefix = " ".repeat(indentation);

        LocalPlayer player = Minecraft.getInstance().player;
        DynamicOps<Tag> ops = (
            (player == null)
                ? NbtOps.INSTANCE
                : player.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        );

        Tag nbtTag = codec.encodeStart(ops, value).getOrThrow(FormattingException::new);
        TextComponentTagVisitor textComponentTagVisitor = new TextComponentTagVisitor(prefix);

        return textComponentTagVisitor.visit(nbtTag);
    }

    private static List<Component> convertToTextList(Component text, boolean colored, String linePrefix) {
        List<Component> textList = new ArrayList<>();

        MutableComponent startOfLine = Component.literal(linePrefix);
        if (!colored) {
            startOfLine.withStyle(NO_COLOR_STYLE);
        }

        // This must be encapsulated in an array, otherwise
        // the re-assigning inside the for-loop wouldn't work
        MutableComponent[] textLine = { startOfLine.copy() };

        text.visit((style, string) -> {
            String[] stringArray = string.split("(?=\\n)|(?<=\\n)");

            for (String stringSegment : stringArray) {
                if (!stringSegment.equals(LF)) {
                    textLine[0].append(Component.literal(stringSegment).withStyle((colored) ? style : NO_COLOR_STYLE));
                    continue;
                }

                textList.add(textLine[0]);
                textLine[0] = startOfLine.copy();
            }

            return Optional.empty();
        }, Style.EMPTY);

        textList.add(textLine[0]);

        return textList;
    }
}
