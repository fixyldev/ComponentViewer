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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.JsonOps;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.Component;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.fixyl.componentviewer.util.ResultCache;

public class JsonFormatter implements Formatter {
    private static final String NO_CODEC_REPR = "{}";

    private static final Map<JsonType, Style> JSON_STYLES = Map.ofEntries(
            Map.entry(JsonType.SPECIAL, Style.EMPTY.withColor(Formatting.WHITE)),
            Map.entry(JsonType.KEY, Style.EMPTY.withColor(Formatting.AQUA)),
            Map.entry(JsonType.STRING, Style.EMPTY.withColor(Formatting.GREEN)),
            Map.entry(JsonType.NUMBER, Style.EMPTY.withColor(Formatting.GOLD)),
            Map.entry(JsonType.BOOLEAN, Style.EMPTY.withColor(Formatting.GOLD)),
            Map.entry(JsonType.NULL, Style.EMPTY.withColor(Formatting.BLUE))
    );

    private final ResultCache<String> stringResultCache;
    private final ResultCache<List<Text>> textResultCache;

    private final Map<Integer, String> newLinePrefixCache;

    private int indentation;
    private boolean colored;
    private String linePrefix;

    private String indentPrefix;
    private boolean isNewLinePrefixSet;

    private List<Text> textList;
    private MutableText textLine;
    private int indentLevel;

    public JsonFormatter() {
        this.stringResultCache = new ResultCache<>();
        this.textResultCache = new ResultCache<>();

        this.newLinePrefixCache = new HashMap<>();

        this.isNewLinePrefixSet = false;
    }

    @Override
    public <T> String componentToString(Component<T> component, int indentation, String linePrefix) {
        return this.stringResultCache.cache(() -> {
            List<Text> formattedTextList = this.getFormattedTextList(component, indentation, false, linePrefix);

            return formattedTextList.stream().map(Text::getString).collect(Collectors.joining(System.lineSeparator()));
        }, component, indentation, linePrefix);
    }

    @Override
    public <T> List<Text> componentToText(Component<T> component, int indentation, boolean colored, String linePrefix) {
        return Collections.unmodifiableList(this.textResultCache.cache(() -> this.getFormattedTextList(component, indentation, colored, linePrefix), component, indentation, colored, linePrefix));
    }

    private Style getStyle(JsonType jsonType) {
        if (!this.colored) {
            return Formatter.NO_COLOR_STYLE;
        }

        return JsonFormatter.JSON_STYLES.get(jsonType);
    }

    private Style getStyle() {
        return this.getStyle(JsonType.SPECIAL);
    }

    private void updateNewLinePrefix(int indentation, String linePrefix) {
        if (this.isNewLinePrefixSet && this.indentation == indentation && this.linePrefix.equals(linePrefix)) {
            return;
        }

        this.indentation = indentation;
        this.indentPrefix = " ".repeat(indentation);
        this.linePrefix = linePrefix;

        this.isNewLinePrefixSet = true;

        if (this.newLinePrefixCache != null) {
            this.newLinePrefixCache.clear();
        }
    }

    private String getNewLinePrefix() {
        if (this.indentLevel <= 0) {
            return this.linePrefix;
        }

        return this.newLinePrefixCache.computeIfAbsent(this.indentLevel, key -> this.linePrefix + this.indentPrefix.repeat(key));
    }

    private <T> List<Text> getFormattedTextList(Component<T> component, int indentation, boolean colored, String linePrefix) {
        this.updateNewLinePrefix(indentation, linePrefix);
        this.colored = colored;

        this.textList = new ArrayList<>();
        this.textLine = Text.literal(linePrefix);
        this.indentLevel = 0;

        if (component.type().getCodec() == null) {
            this.textLine.append(Text.literal(JsonFormatter.NO_CODEC_REPR).fillStyle(this.getStyle()));
            this.textList.add(this.textLine);
            return this.textList;
        }

        JsonElement jsonElement = component.encode(MinecraftClient.getInstance().player.getRegistryManager().getOps(JsonOps.INSTANCE)).getOrThrow(FormattingException::new);

        this.walkJson(jsonElement);

        if (this.indentLevel != 0) {
            throw new FormattingException(String.format("Indent level must end up being zero! But it was %s.", this.indentLevel));
        }

        if (!this.textLine.getString().isEmpty()) {
            this.textList.add(this.textLine);
        }

        return this.textList;
    }

    private void walkJson(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            this.processJsonObject(jsonElement.getAsJsonObject());
        } else if (jsonElement.isJsonArray()) {
            this.processJsonArray(jsonElement.getAsJsonArray());
        } else if (jsonElement.isJsonPrimitive()) {
            this.processJsonPrimitive(jsonElement.getAsJsonPrimitive());
        } else if (jsonElement.isJsonNull()) {
            this.processJsonNull();
        } else {
            throw new FormattingException("Unknown JSON element");
        }
    }

    private void processJsonObject(JsonObject jsonObject) {
        this.textLine.append(Text.literal("{").fillStyle(this.getStyle()));

        if (!jsonObject.isEmpty()) {
            this.createNewLine(1);

            Iterator<Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, JsonElement> entry = iterator.next();

                this.textLine.append(Text.literal("\"").fillStyle(this.getStyle()))
                        .append(Text.literal(entry.getKey()).fillStyle(this.getStyle(JsonType.KEY)))
                        .append(Text.literal("\": ").fillStyle(this.getStyle()));
                this.walkJson(entry.getValue());

                if (!iterator.hasNext()) {
                    break;
                }

                this.textLine.append(Text.literal(",").fillStyle(this.getStyle()));
                this.createNewLine(0);
            }

            this.createNewLine(-1);
        }

        this.textLine.append(Text.literal("}").fillStyle(this.getStyle()));
    }

    private void processJsonArray(JsonArray jsonArray) {
        this.textLine.append(Text.literal("[").fillStyle(this.getStyle()));

        if (!jsonArray.isEmpty()) {
            this.createNewLine(1);

            Iterator<JsonElement> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JsonElement jsonElement = iterator.next();

                this.walkJson(jsonElement);

                if (!iterator.hasNext()) {
                    break;
                }

                this.textLine.append(Text.literal(",").fillStyle(this.getStyle()));
                this.createNewLine(0);
            }

            this.createNewLine(-1);
        }

        this.textLine.append(Text.literal("]").fillStyle(this.getStyle()));
    }

    private void processJsonPrimitive(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isString()) {
            this.textLine.append(Text.literal("\"").fillStyle(this.getStyle()))
                    .append(Text.literal(JsonFormatter.escapeString(jsonPrimitive.getAsString())).fillStyle(this.getStyle(JsonType.STRING)))
                    .append(Text.literal("\"").fillStyle(this.getStyle()));
        } else if (jsonPrimitive.isNumber()) {
            this.textLine.append(Text.literal(jsonPrimitive.getAsString()).fillStyle(this.getStyle(JsonType.NUMBER)));
        } else if (jsonPrimitive.isBoolean()) {
            this.textLine.append(Text.literal(jsonPrimitive.getAsString()).fillStyle(this.getStyle(JsonType.BOOLEAN)));
        } else {
            throw new FormattingException("Unknown JSON primitive");
        }
    }

    private void processJsonNull() {
        this.textLine.append(Text.literal("null").fillStyle(this.getStyle(JsonType.NULL)));
    }

    private void createNewLine(int indentChange) {
        this.indentLevel += indentChange;

        if (this.indentation > 0) {
            this.textList.add(this.textLine);
            this.textLine = Text.literal(this.getNewLinePrefix());
        } else if (indentChange == 0) {
            this.textLine.append(Text.literal(" "));
        }
    }

    private static String escapeString(String string) {
        return string.replace("\"", "\\\"");
    }

    private enum JsonType {
        SPECIAL,
        KEY,
        STRING,
        NUMBER,
        BOOLEAN,
        NULL
    }
}
