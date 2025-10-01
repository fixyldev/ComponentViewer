package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum TooltipPurpose implements OptionEnum {

    @SerializedName("components") COMPONENTS(0, "componentviewer.config.tooltip.purpose.components"),
    @SerializedName("item_stack") ITEM_STACK(1, "componentviewer.config.tooltip.purpose.item_stack");

    private final int id;
    private final String translationKey;

    private TooltipPurpose(int id, String translationKey) {
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
