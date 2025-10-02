package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum ClipboardFormatting implements OptionEnum {

    @SerializedName("sync") SYNC(0, "componentviewer.config.clipboard.formatting.sync"),
    @SerializedName("snbt") SNBT(1, "componentviewer.config.clipboard.formatting.snbt"),
    @SerializedName("json") JSON(2, "componentviewer.config.clipboard.formatting.json"),
    @SerializedName("object") OBJECT(3, "componentviewer.config.clipboard.formatting.object");

    private final int id;
    private final String translationKey;

    private ClipboardFormatting(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.translationKey;
    }
}
