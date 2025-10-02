package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum TooltipDisplay implements OptionEnum {

    @SerializedName("hold") HOLD(0, "componentviewer.config.tooltip.display.hold"),
    @SerializedName("always") ALWAYS(1, "componentviewer.config.tooltip.display.always"),
    @SerializedName("never") NEVER(2, "componentviewer.config.tooltip.display.never");

    private final int id;
    private final String translationKey;

    private TooltipDisplay(int id, String translationKey) {
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
