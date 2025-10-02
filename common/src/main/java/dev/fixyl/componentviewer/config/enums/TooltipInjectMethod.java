package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum TooltipInjectMethod implements OptionEnum {

    @SerializedName("replace") REPLACE(1, "componentviewer.config.tooltip.inject_method.replace"),
    @SerializedName("append") APPEND(0, "componentviewer.config.tooltip.inject_method.append");

    private final int id;
    private final String translationKey;

    private TooltipInjectMethod(int id, String translationKey) {
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
