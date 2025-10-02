package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum TooltipComponents implements OptionEnum {

    @SerializedName("all") ALL(0, "componentviewer.config.tooltip.components.all"),
    @SerializedName("default") DEFAULT(1, "componentviewer.config.tooltip.components.default"),
    @SerializedName("changes") CHANGES(2, "componentviewer.config.tooltip.components.changes");

    private final int id;
    private final String translationKey;

    private TooltipComponents(int id, String translationKey) {
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
