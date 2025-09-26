package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.OptionEnum;

public enum ClipboardSelector implements OptionEnum {

    @SerializedName("everyone") EVERYONE(0, "componentviewer.config.clipboard.selector.everyone"),
    @SerializedName("nearest") NEAREST(1, "componentviewer.config.clipboard.selector.nearest"),
    @SerializedName("self") SELF(2, "componentviewer.config.clipboard.selector.self"),
    @SerializedName("player") PLAYER(3, "componentviewer.config.clipboard.selector.player") {
        @Override
        public Component getCaption() {
            return Component.translatable(this.getKey(), Minecraft.getInstance().getGameProfile().getName());
        }
    };

    private final int id;
    private final String translationKey;

    private ClipboardSelector(int id, String translationKey) {
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
