package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

public enum DisableMod implements OptionEnum {

    @SerializedName("never") NEVER(0, "componentviewer.config.disable_mod.never"),
    @SerializedName("in_survival") IN_SURVIVAL(1, "componentviewer.config.disable_mod.in_survival"),
    @SerializedName("on_server") ON_SERVER(2, "componentviewer.config.disable_mod.on_server"),
    @SerializedName("both") BOTH(3, "componentviewer.config.disable_mod.both");

    private final int id;
    private final String translationKey;

    private DisableMod(int id, String translationKey) {
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
