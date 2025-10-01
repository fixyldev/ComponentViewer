package dev.fixyl.componentviewer.formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.util.ResultCache;

public class JsonFormatter implements CodecBasedFormatter {

    private static final String NO_CODEC_REPR = "{}";

    private static final Pattern STRING_ESCAPE_PATTERN = Pattern.compile("[\\\\\"]");
    private static final String STRING_ESCAPE_REPLACEMENT = "\\\\$0";

    private static final Map<JsonType, Style> JSON_STYLES = Map.ofEntries(
        Map.entry(JsonType.SPECIAL, Style.EMPTY.withColor(ChatFormatting.WHITE)),
        Map.entry(JsonType.KEY, Style.EMPTY.withColor(ChatFormatting.AQUA)),
        Map.entry(JsonType.STRING, Style.EMPTY.withColor(ChatFormatting.GREEN)),
        Map.entry(JsonType.NUMBER, Style.EMPTY.withColor(ChatFormatting.GOLD)),
        Map.entry(JsonType.BOOLEAN, Style.EMPTY.withColor(ChatFormatting.GOLD)),
        Map.entry(JsonType.NULL, Style.EMPTY.withColor(ChatFormatting.BLUE))
    );

    private final ResultCache<String> stringResultCache;
    private final ResultCache<List<Component>> textResultCache;

    private final Map<Integer, String> newLinePrefixCache;

    private int indentation;
    private boolean colored;
    private String linePrefix;

    private String indentPrefix;
    private boolean isNewLinePrefixSet;

    private List<Component> textList;
    private MutableComponent textLine;
    private int indentLevel;

    public JsonFormatter() {
        this.stringResultCache = new ResultCache<>();
        this.textResultCache = new ResultCache<>();

        this.newLinePrefixCache = new HashMap<>();

        this.isNewLinePrefixSet = false;
    }

    @Override
    public <T> String codecToString(T value, @NullPermitted Codec<T> codec, int indentation, String linePrefix) {
        return this.stringResultCache.cache(() -> {
            List<Component> formattedTextList = this.getFormattedTextList(value, codec, indentation, false, linePrefix);

            return formattedTextList.stream().map(Component::getString).collect(Collectors.joining(System.lineSeparator()));
        }, value, codec, indentation, linePrefix);
    }

    @Override
    public <T> List<Component> codecToText(T value, @NullPermitted Codec<T> codec, int indentation, boolean colored, String linePrefix) {
        return Collections.unmodifiableList(this.textResultCache.cache(
            () -> this.getFormattedTextList(value, codec, indentation, colored, linePrefix),
            value, codec, indentation, colored, linePrefix
        ));
    }

    private Style getStyle(JsonType jsonType) {
        if (!this.colored) {
            return NO_COLOR_STYLE;
        }

        return JSON_STYLES.get(jsonType);
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

    private <T> List<Component> getFormattedTextList(T value, @NullPermitted Codec<T> codec, int indentation, boolean colored, String linePrefix) {
        this.updateNewLinePrefix(indentation, linePrefix);
        this.colored = colored;

        this.textList = new ArrayList<>();
        this.textLine = Component.literal(linePrefix);
        this.indentLevel = 0;

        if (codec == null) {
            this.textLine.append(Component.literal(NO_CODEC_REPR).withStyle(this.getStyle()));
            this.textList.add(this.textLine);
            return this.textList;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        DynamicOps<JsonElement> ops = (
            (player == null)
                ? JsonOps.INSTANCE
                : player.registryAccess().createSerializationContext(JsonOps.INSTANCE)
        );

        JsonElement jsonElement = codec.encodeStart(ops, value).getOrThrow(FormattingException::new);

        this.walkJson(jsonElement);

        if (this.indentLevel != 0) {
            throw new FormattingException(String.format(
                "Indent level must end up being zero! But it was %s.",
                this.indentLevel
            ));
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
        this.textLine.append(Component.literal("{").withStyle(this.getStyle()));

        if (!jsonObject.isEmpty()) {
            this.createNewLine(1);

            Iterator<Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, JsonElement> entry = iterator.next();

                this.textLine.append(Component.literal("\"").withStyle(this.getStyle()))
                    .append(Component.literal(entry.getKey()).withStyle(this.getStyle(JsonType.KEY)))
                    .append(Component.literal("\": ").withStyle(this.getStyle()));
                this.walkJson(entry.getValue());

                if (!iterator.hasNext()) {
                    break;
                }

                this.textLine.append(Component.literal(",").withStyle(this.getStyle()));
                this.createNewLine(0);
            }

            this.createNewLine(-1);
        }

        this.textLine.append(Component.literal("}").withStyle(this.getStyle()));
    }

    private void processJsonArray(JsonArray jsonArray) {
        this.textLine.append(Component.literal("[").withStyle(this.getStyle()));

        if (!jsonArray.isEmpty()) {
            this.createNewLine(1);

            Iterator<JsonElement> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JsonElement jsonElement = iterator.next();

                this.walkJson(jsonElement);

                if (!iterator.hasNext()) {
                    break;
                }

                this.textLine.append(Component.literal(",").withStyle(this.getStyle()));
                this.createNewLine(0);
            }

            this.createNewLine(-1);
        }

        this.textLine.append(Component.literal("]").withStyle(this.getStyle()));
    }

    private void processJsonPrimitive(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isString()) {
            this.textLine.append(Component.literal("\"").withStyle(this.getStyle()))
                .append(Component.literal(JsonFormatter.escapeString(jsonPrimitive.getAsString())).withStyle(this.getStyle(JsonType.STRING)))
                .append(Component.literal("\"").withStyle(this.getStyle()));
        } else if (jsonPrimitive.isNumber()) {
            this.textLine.append(Component.literal(jsonPrimitive.getAsString()).withStyle(this.getStyle(JsonType.NUMBER)));
        } else if (jsonPrimitive.isBoolean()) {
            this.textLine.append(Component.literal(jsonPrimitive.getAsString()).withStyle(this.getStyle(JsonType.BOOLEAN)));
        } else {
            throw new FormattingException("Unknown JSON primitive");
        }
    }

    private void processJsonNull() {
        this.textLine.append(Component.literal("null").withStyle(this.getStyle(JsonType.NULL)));
    }

    private void createNewLine(int indentChange) {
        this.indentLevel += indentChange;

        if (this.indentation > 0) {
            this.textList.add(this.textLine);
            this.textLine = Component.literal(this.getNewLinePrefix());
        } else if (indentChange == 0) {
            this.textLine.append(Component.literal(" "));
        }
    }

    private static String escapeString(String string) {
        Matcher matcher = STRING_ESCAPE_PATTERN.matcher(string);

        return matcher.replaceAll(STRING_ESCAPE_REPLACEMENT);
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
