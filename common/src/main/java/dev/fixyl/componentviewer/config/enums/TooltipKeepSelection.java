package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum TooltipKeepSelection implements OptionEnum {

    @SerializedName("index") INDEX(0, "componentviewer.config.tooltip.keep_selection.index"),
    @SerializedName("type") TYPE(1, "componentviewer.config.tooltip.keep_selection.type"),
    @SerializedName("never") NEVER(2, "componentviewer.config.tooltip.keep_selection.never");

    private final int id;
    private final String translationKey;

    private TooltipKeepSelection(int id, String translationKey) {
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
