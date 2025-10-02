package dev.fixyl.componentviewer.config;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.file.Path;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;

import org.slf4j.Logger;

import dev.fixyl.componentviewer.config.enums.ClipboardCopy;
import dev.fixyl.componentviewer.config.enums.ClipboardFormatting;
import dev.fixyl.componentviewer.config.enums.ClipboardSelector;
import dev.fixyl.componentviewer.config.enums.DisableMod;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.config.enums.TooltipDisplay;
import dev.fixyl.componentviewer.config.enums.TooltipFormatting;
import dev.fixyl.componentviewer.config.enums.TooltipInjectMethod;
import dev.fixyl.componentviewer.config.enums.TooltipPurpose;
import dev.fixyl.componentviewer.config.enums.TooltipKeepSelection;
import dev.fixyl.componentviewer.config.keymapping.ActionBoundKeyMapping;
import dev.fixyl.componentviewer.config.keymapping.AdvancedKeyMapping;
import dev.fixyl.componentviewer.config.keymapping.CycleSelectionKeyMapping;
import dev.fixyl.componentviewer.config.keymapping.AdvancedKeyMapping.ConflictContext;
import dev.fixyl.componentviewer.config.keymapping.EnumOptionKeyMapping;
import dev.fixyl.componentviewer.config.keymapping.KeyMappings;
import dev.fixyl.componentviewer.config.option.AdvancedOption;
import dev.fixyl.componentviewer.config.option.BooleanOption;
import dev.fixyl.componentviewer.config.option.EnumOption;
import dev.fixyl.componentviewer.config.option.IntegerOption;
import dev.fixyl.componentviewer.config.option.Options;
import dev.fixyl.componentviewer.control.Selection.CycleType;
import dev.fixyl.componentviewer.screen.MainConfigScreen;

public final class Configs implements Options, KeyMappings {

    private static final String CONFIG_FILENAME = "componentviewer-config.json";

    private final ConfigManager configManager;

    public Configs(Path configDir, Logger logger) {
        this.configManager = new ConfigManager(configDir.resolve(CONFIG_FILENAME).toFile(), logger);
        this.configManager.addOptions(this);
    }

    public void loadFromDisk() {
        this.configManager.readFromFile();
    }

    public void saveToDisk() {
        this.configManager.writeToFile();
    }

    @Override
    public AdvancedOption<?>[] getOptions() {
        return this.options;
    }

    @Override
    public AdvancedKeyMapping[] getKeyMappings() {
        return this.keyMappings;
    }

    public final EnumOption<DisableMod> disableMod = EnumOption.<DisableMod>create("disable_mod")
        .setDefaultValue(DisableMod.NEVER)
        .setTranslationKey("componentviewer.config.disable_mod")
        .setDescriptionTranslationKey("componentviewer.config.disable_mod.description")
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipDisplay> tooltipDisplay = EnumOption.<TooltipDisplay>create("tooltip.display")
        .setDefaultValue(TooltipDisplay.HOLD)
        .setTranslationKey("componentviewer.config.tooltip.display")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.display.description")
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipPurpose> tooltipPurpose = EnumOption.<TooltipPurpose>create("tooltip.purpose")
        .setDefaultValue(TooltipPurpose.COMPONENTS)
        .setTranslationKey("componentviewer.config.tooltip.purpose")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.purpose.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER)
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipComponents> tooltipComponents = EnumOption.<TooltipComponents>create("tooltip.components")
        .setDefaultValue(TooltipComponents.ALL)
        .setTranslationKey("componentviewer.config.tooltip.components")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.components.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption tooltipShowAmount = BooleanOption.create("tooltip.show_amount")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.tooltip.show_amount")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.show_amount.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption tooltipComponentValues = BooleanOption.create("tooltip.component_values")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.tooltip.component_values")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.component_values.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS)
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipKeepSelection> tooltipKeepSelection = EnumOption.<TooltipKeepSelection>create("tooltip.keep_selection")
        .setDefaultValue(TooltipKeepSelection.TYPE)
        .setTranslationKey("componentviewer.config.tooltip.keep_selection")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.keep_selection.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS && this.tooltipComponentValues.getBooleanValue())
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipFormatting> tooltipFormatting = EnumOption.<TooltipFormatting>create("tooltip.formatting")
        .setDefaultValue(TooltipFormatting.SNBT)
        .setTranslationKey("componentviewer.config.tooltip.formatting")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.formatting.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && ((this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS && this.tooltipComponentValues.getBooleanValue()) || this.tooltipPurpose.getValue() == TooltipPurpose.ITEM_STACK))
        .setChangeCallback(this::changeCallback)
        .build();
    public final IntegerOption tooltipIndentation = IntegerOption.create("tooltip.indentation")
        .setDefaultValue(4)
        .setIntegerRange(0, 8)
        .setTranslationKey("componentviewer.config.tooltip.indentation")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.indentation.description")
        .setTranslationKeyOverwrite(value -> (value == 0) ? "componentviewer.config.tooltip.indentation.off" : "componentviewer.config.tooltip.indentation.value")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && ((this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS && this.tooltipComponentValues.getBooleanValue()) || this.tooltipPurpose.getValue() == TooltipPurpose.ITEM_STACK))
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption tooltipColoredFormatting = BooleanOption.create("tooltip.colored_formatting")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.tooltip.colored_formatting")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.colored_formatting.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER && ((this.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS && this.tooltipComponentValues.getBooleanValue()) || this.tooltipPurpose.getValue() == TooltipPurpose.ITEM_STACK))
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<TooltipInjectMethod> tooltipInjectMethod = EnumOption.<TooltipInjectMethod>create("tooltip.inject_method")
        .setDefaultValue(TooltipInjectMethod.REPLACE)
        .setTranslationKey("componentviewer.config.tooltip.inject_method")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.inject_method.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption tooltipAdvancedTooltips = BooleanOption.create("tooltip.advanced_tooltips")
        .setDefaultValue(false)
        .setTranslationKey("componentviewer.config.tooltip.advanced_tooltips")
        .setDescriptionTranslationKey("componentviewer.config.tooltip.advanced_tooltips.description")
        .setDependency(() -> this.tooltipDisplay.getValue() != TooltipDisplay.NEVER)
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<ClipboardCopy> clipboardCopy = EnumOption.<ClipboardCopy>create("clipboard.copy")
        .setDefaultValue(ClipboardCopy.COMPONENT_VALUE)
        .setTranslationKey("componentviewer.config.clipboard.copy")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.copy.description")
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<ClipboardFormatting> clipboardFormatting = EnumOption.<ClipboardFormatting>create("clipboard.formatting")
        .setDefaultValue(ClipboardFormatting.SYNC)
        .setTranslationKey("componentviewer.config.clipboard.formatting")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.formatting.description")
        .setDependency(() -> EnumSet.of(ClipboardCopy.COMPONENT_VALUE, ClipboardCopy.ITEM_STACK).contains(this.clipboardCopy.getValue()))
        .setChangeCallback(this::changeCallback)
        .build();
    public final IntegerOption clipboardIndentation = IntegerOption.create("clipboard.indentation")
        .setDefaultValue(-1)
        .setIntegerRange(-1, 8)
        .setTranslationKey("componentviewer.config.clipboard.indentation")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.indentation.description")
        .setTranslationKeyOverwrite(value -> switch (Integer.signum(value)) {
            case -1 -> "componentviewer.config.clipboard.indentation.sync";
            case 0 -> "componentviewer.config.clipboard.indentation.off";
            case 1 -> "componentviewer.config.clipboard.indentation.value";
            default -> throw new IllegalStateException(String.format("Unexpected int value: %s", value));
        })
        .setDependency(() -> EnumSet.of(ClipboardCopy.COMPONENT_VALUE, ClipboardCopy.ITEM_STACK).contains(this.clipboardCopy.getValue()))
        .setChangeCallback(this::changeCallback)
        .build();
    public final EnumOption<ClipboardSelector> clipboardSelector = EnumOption.<ClipboardSelector>create("clipboard.selector")
        .setDefaultValue(ClipboardSelector.SELF)
        .setTranslationKey("componentviewer.config.clipboard.selector")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.selector.description")
        .setDependency(() -> this.clipboardCopy.getValue() == ClipboardCopy.GIVE_COMMAND)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption clipboardPrependSlash = BooleanOption.create("clipboard.prepend_slash")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.clipboard.prepend_slash")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.prepend_slash.description")
        .setDependency(() -> this.clipboardCopy.getValue() == ClipboardCopy.GIVE_COMMAND)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption clipboardIncludeCount = BooleanOption.create("clipboard.include_count")
        .setDefaultValue(false)
        .setTranslationKey("componentviewer.config.clipboard.include_count")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.include_count.description")
        .setDependency(() -> this.clipboardCopy.getValue() == ClipboardCopy.GIVE_COMMAND)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption clipboardSuccessNotification = BooleanOption.create("clipboard.success_notification")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.clipboard.success_notification")
        .setDescriptionTranslationKey("componentviewer.config.clipboard.success_notification.description")
        .setDependency(() -> this.clipboardCopy.getValue() != ClipboardCopy.DISABLED)
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption controlsAllowScrolling = BooleanOption.create("controls.allow_scrolling")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.controls.allow_scrolling")
        .setDescriptionTranslationKey("componentviewer.config.controls.allow_scrolling.description")
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption controlsAlternativeCopyModifierKey = BooleanOption.create("controls.alternative_copy_modifier_key")
        .setDefaultValue(false)
        .setTranslationKey("componentviewer.config.controls.alternative_copy_modifier_key")
        .setDescriptionTranslationKey("componentviewer.config.controls.alternative_copy_modifier_key.description")
        .setChangeCallback(this::changeCallback)
        .build();
    public final BooleanOption controlsAllowCyclingOptionsWhileInScreen = BooleanOption.create("controls.allow_cycling_options_while_in_screen")
        .setDefaultValue(true)
        .setTranslationKey("componentviewer.config.controls.allow_cycling_options_while_in_screen")
        .setDescriptionTranslationKey("componentviewer.config.controls.allow_cycling_options_while_in_screen.description")
        .setChangeCallback(this::changeCallback)
        .build();

    private final AdvancedOption<?>[] options = {
        this.disableMod,
        this.tooltipDisplay,
        this.tooltipPurpose,
        this.tooltipComponents,
        this.tooltipShowAmount,
        this.tooltipComponentValues,
        this.tooltipKeepSelection,
        this.tooltipFormatting,
        this.tooltipIndentation,
        this.tooltipColoredFormatting,
        this.tooltipInjectMethod,
        this.tooltipAdvancedTooltips,
        this.clipboardCopy,
        this.clipboardFormatting,
        this.clipboardIndentation,
        this.clipboardSelector,
        this.clipboardPrependSlash,
        this.clipboardIncludeCount,
        this.clipboardSuccessNotification,
        this.controlsAllowScrolling,
        this.controlsAlternativeCopyModifierKey,
        this.controlsAllowCyclingOptionsWhileInScreen
    };

    private <T> void changeCallback(T value) {
        this.saveToDisk();
    }

    public final ActionBoundKeyMapping keyConfigScreen = new ActionBoundKeyMapping(
        "key.category.componentviewer.controls.config_screen",
        GLFW_KEY_J,
        AdvancedKeyMapping.GENERAL_CATEGORY,
        ConflictContext.IN_GAME,
        () -> {
            Minecraft minecraftClient = Minecraft.getInstance();

            if (minecraftClient != null) {
                minecraftClient.setScreen(new MainConfigScreen(null, this));
            }
        }
    );
    public final AdvancedKeyMapping keyShowTooltip = new AdvancedKeyMapping(
        "key.category.componentviewer.controls.show_tooltip",
        GLFW_KEY_LEFT_ALT,
        AdvancedKeyMapping.GENERAL_CATEGORY,
        ConflictContext.IN_SCREEN
    );
    public final CycleSelectionKeyMapping keyNextComponent = new CycleSelectionKeyMapping(
        "key.category.componentviewer.controls.next_component",
        GLFW_KEY_DOWN,
        AdvancedKeyMapping.GENERAL_CATEGORY,
        ConflictContext.IN_SCREEN,
        CycleType.NEXT
    );
    public final CycleSelectionKeyMapping keyPreviousComponent = new CycleSelectionKeyMapping(
        "key.category.componentviewer.controls.previous_component",
        GLFW_KEY_UP,
        AdvancedKeyMapping.GENERAL_CATEGORY,
        ConflictContext.IN_SCREEN,
        CycleType.PREVIOUS
    );
    public final CycleSelectionKeyMapping keyFirstComponent = new CycleSelectionKeyMapping(
        "key.category.componentviewer.controls.first_component",
        GLFW_KEY_HOME,
        AdvancedKeyMapping.GENERAL_CATEGORY,
        ConflictContext.IN_SCREEN,
        CycleType.FIRST
    );
    public final CycleSelectionKeyMapping keyLastComponent = new CycleSelectionKeyMapping(
        "key.category.componentviewer.controls.last_component",
        GLFW_KEY_END,
        AdvancedKeyMapping.GENERAL_CATEGORY,
        ConflictContext.IN_SCREEN,
        CycleType.LAST
    );
    public final EnumOptionKeyMapping<TooltipDisplay> keyTooltipDisplayConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.tooltip_display",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.tooltipDisplay
    );
    public final EnumOptionKeyMapping<TooltipPurpose> keyTooltipPurposeConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.tooltip_purpose",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.tooltipPurpose
    );
    public final EnumOptionKeyMapping<TooltipComponents> keyTooltipComponentsConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.tooltip_components",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.tooltipComponents
    );
    public final EnumOptionKeyMapping<TooltipKeepSelection> keyTooltipKeepSelectionConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.tooltip_keep_selection",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.tooltipKeepSelection
    );
    public final EnumOptionKeyMapping<TooltipFormatting> keyTooltipFormattingConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.tooltip_formatting",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.tooltipFormatting
    );
    public final EnumOptionKeyMapping<TooltipInjectMethod> keyTooltipInjectMethodConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.tooltip_inject_method",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.tooltipInjectMethod
    );
    public final EnumOptionKeyMapping<ClipboardCopy> keyClipboardCopyConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.clipboard_copy",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.clipboardCopy
    );
    public final EnumOptionKeyMapping<ClipboardFormatting> keyClipboardFormattingConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.clipboard_formatting",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.clipboardFormatting
    );
    public final EnumOptionKeyMapping<ClipboardSelector> keyClipboardSelectorConfig = new EnumOptionKeyMapping<>(
        "key.category.componentviewer.controls.cycle_configs.clipboard_selector",
        GLFW_KEY_UNKNOWN,
        AdvancedKeyMapping.CONFIG_CATEGORY,
        this.clipboardSelector
    );

    private final AdvancedKeyMapping[] keyMappings = {
        this.keyConfigScreen,
        this.keyShowTooltip,
        this.keyNextComponent,
        this.keyPreviousComponent,
        this.keyFirstComponent,
        this.keyLastComponent,
        this.keyTooltipDisplayConfig,
        this.keyTooltipPurposeConfig,
        this.keyTooltipComponentsConfig,
        this.keyTooltipKeepSelectionConfig,
        this.keyTooltipFormattingConfig,
        this.keyTooltipInjectMethodConfig,
        this.keyClipboardCopyConfig,
        this.keyClipboardFormattingConfig,
        this.keyClipboardSelectorConfig
    };
}
