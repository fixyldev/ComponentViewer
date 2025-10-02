package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum ClipboardCopy implements OptionEnum {

    @SerializedName("component_value") COMPONENT_VALUE(0, "componentviewer.config.clipboard.copy.component_value"),
    @SerializedName("item_stack") ITEM_STACK(1, "componentviewer.config.clipboard.copy.item_stack"),
    @SerializedName("give_command") GIVE_COMMAND(2, "componentviewer.config.clipboard.copy.give_command"),
    @SerializedName("disabled") DISABLED(3, "componentviewer.config.clipboard.copy.disabled");

    private final int id;
    private final String translationKey;

    private ClipboardCopy(int id, String translationKey) {
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
