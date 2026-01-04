package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.OptionEnum;

import dev.fixyl.componentviewer.control.component.ComponentContext;

public enum TooltipComponents implements OptionEnum {

    @SerializedName("all") ALL(0, "componentviewer.config.tooltip.components.all", ComponentContext.NORMAL),
    @SerializedName("default") DEFAULT(1, "componentviewer.config.tooltip.components.default", ComponentContext.PROTOTYPE),
    @SerializedName("changes") CHANGES(2, "componentviewer.config.tooltip.components.changes", ComponentContext.PATCH);

    private final int id;
    private final String translationKey;
    private final ComponentContext componentContext;

    private TooltipComponents(int id, String translationKey, ComponentContext componentContext) {
        this.id = id;
        this.translationKey = translationKey;
        this.componentContext = componentContext;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.translationKey;
    }

    public ComponentContext getComponentContext() {
        return this.componentContext;
    }
}
