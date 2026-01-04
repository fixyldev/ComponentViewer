package dev.fixyl.componentviewer.config.enums;

import com.google.gson.annotations.SerializedName;

import dev.fixyl.componentviewer.config.option.EnumOption.OptionEnum;
import dev.fixyl.componentviewer.control.component.ComponentContext;

public enum TooltipComponents implements OptionEnum {

    @SerializedName("all") ALL("componentviewer.config.tooltip.components.all", ComponentContext.NORMAL),
    @SerializedName("default") DEFAULT("componentviewer.config.tooltip.components.default", ComponentContext.PROTOTYPE),
    @SerializedName("changes") CHANGES("componentviewer.config.tooltip.components.changes", ComponentContext.PATCH);

    private final String serializedName;
    private final String translationKey;
    private final ComponentContext componentContext;

    private TooltipComponents(String translationKey, ComponentContext componentContext) {
        this.serializedName = OptionEnum.createSerializedName(this);
        this.translationKey = translationKey;
        this.componentContext = componentContext;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public ComponentContext getComponentContext() {
        return this.componentContext;
    }
}
