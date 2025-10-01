package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum TooltipFormatting implements OptionEnum {

    @SerializedName("snbt") SNBT(0, "componentviewer.config.tooltip.formatting.snbt"),
    @SerializedName("json") JSON(1, "componentviewer.config.tooltip.formatting.json"),
    @SerializedName("object") OBJECT(2, "componentviewer.config.tooltip.formatting.object");

    private final int id;
    private final String translationKey;

    private TooltipFormatting(int id, String translationKey) {
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
