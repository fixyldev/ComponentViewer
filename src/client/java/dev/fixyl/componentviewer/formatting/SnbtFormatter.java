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

package dev.fixyl.componentviewer.formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.text.MutableText;

import org.jetbrains.annotations.Nullable;

import dev.fixyl.componentviewer.util.ResultCache;

public class SnbtFormatter implements CodecBasedFormatter {
    private static final String NO_CODEC_REPR = "{}";
    private static final Style NO_CODEC_REPR_STYLE = Style.EMPTY.withColor(Formatting.WHITE);

    private final ResultCache<String> stringResultCache;
    private final ResultCache<List<Text>> textResultCache;

    public SnbtFormatter() {
        this.stringResultCache = new ResultCache<>();
        this.textResultCache = new ResultCache<>();
    }

    @Override
    public <T> String codecToString(T value, @Nullable Codec<T> codec, int indentation, String linePrefix) {
        return this.stringResultCache.cache(() -> {
            if (codec == null) {
                return linePrefix + SnbtFormatter.NO_CODEC_REPR;
            }

            String formattedString = SnbtFormatter.getFormattedText(value, codec, indentation).getString();

            if (!linePrefix.isEmpty()) {
                return linePrefix + formattedString.replace("\n", System.lineSeparator() + linePrefix);
            }

            return formattedString;
        }, value, codec, indentation, linePrefix);
    }

    @Override
    public <T> List<Text> codecToText(T value, @Nullable Codec<T> codec, int indentation, boolean colored, String linePrefix) {
        return Collections.unmodifiableList(this.textResultCache.cache(() -> {
            if (codec != null) {
                Text text = SnbtFormatter.getFormattedText(value, codec, indentation);
                return SnbtFormatter.convertToTextList(text, colored, linePrefix);
            }

            Text noCodecText = Text.literal(SnbtFormatter.NO_CODEC_REPR).fillStyle((colored) ? SnbtFormatter.NO_CODEC_REPR_STYLE : Formatter.NO_COLOR_STYLE);

            if (linePrefix.isEmpty()) {
                return List.of(noCodecText);
            }

            MutableText startOfLine = Text.literal(linePrefix);
            if (!colored) {
                startOfLine.fillStyle(Formatter.NO_COLOR_STYLE);
            }

            return List.of(startOfLine.append(noCodecText));
        }, value, codec, indentation, colored, linePrefix));
    }

    private static <T> Text getFormattedText(T value, Codec<T> codec, int indentation) {
        String prefix = " ".repeat(indentation);

        NbtTextFormatter nbtTextFormatter = new NbtTextFormatter(prefix);

        NbtElement nbtElement = codec.encodeStart(MinecraftClient.getInstance().player.getRegistryManager().getOps(NbtOps.INSTANCE), value).getOrThrow(FormattingException::new);

        return nbtTextFormatter.apply(nbtElement);
    }

    private static List<Text> convertToTextList(Text text, boolean colored, String linePrefix) {
        List<Text> textList = new ArrayList<>();

        MutableText startOfLine = Text.literal(linePrefix);
        if (!colored) {
            startOfLine.fillStyle(Formatter.NO_COLOR_STYLE);
        }

        // This must be encapsulated in an array, otherwise
        // the re-assigning inside the for-loop wouldn't work
        MutableText[] textLine = { startOfLine.copy() };

        text.visit((style, string) -> {
            String[] stringArray = string.split("(?=\\n)|(?<=\\n)");

            for (String stringSegment : stringArray) {
                if (!stringSegment.equals("\n")) {
                    textLine[0].append(Text.literal(stringSegment).fillStyle((colored) ? style : Formatter.NO_COLOR_STYLE));
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
