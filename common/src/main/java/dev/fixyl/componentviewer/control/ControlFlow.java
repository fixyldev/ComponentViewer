package dev.fixyl.componentviewer.control;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.DisablableMod;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.enums.ClipboardCopy;
import dev.fixyl.componentviewer.config.enums.TooltipDisplay;
import dev.fixyl.componentviewer.config.enums.TooltipInjectMethod;
import dev.fixyl.componentviewer.config.enums.TooltipPurpose;
import dev.fixyl.componentviewer.control.keyboard.Clipboard;
import dev.fixyl.componentviewer.config.enums.TooltipKeepSelection;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.JsonFormatter;
import dev.fixyl.componentviewer.formatting.ObjectFormatter;
import dev.fixyl.componentviewer.formatting.SnbtFormatter;

/**
 * Represents basically the entire main control flow
 * of this mod. Nearly all method calls and object creations
 * originate from this class in some way or another.
 * <p>
 * Therefore, this class is more or less the brain of this mod,
 * defining most decisions, especially ones regarding configs.
 */
public final class ControlFlow {

    private final Minecraft minecraftClient;
    private final DisablableMod disablableMod;
    private final Configs configs;
    private final Clipboard clipboard;

    private final Formatter snbtFormatter;
    private final Formatter jsonFormatter;
    private final Formatter objectFormatter;

    private long renderTick;

    private HoveredItemStack hoveredItemStack;
    private ItemStack previousItemStack;

    private long lastTimeItemStackHovered;
    private boolean isTooltipShown;
    private long lastTimeTooltipShown;

    public ControlFlow(Minecraft minecraftClient, DisablableMod disablableMod, Configs configs) {
        this.minecraftClient = minecraftClient;
        this.disablableMod = disablableMod;
        this.configs = configs;
        this.clipboard = new Clipboard();

        this.snbtFormatter = new SnbtFormatter();
        this.jsonFormatter = new JsonFormatter();
        this.objectFormatter = new ObjectFormatter();

        this.renderTick = 0L;

        this.lastTimeItemStackHovered = -1L;
        this.isTooltipShown = false;
        this.lastTimeTooltipShown = -1L;
    }

    /**
     * Increment the internal render tick counter.
     * <p>
     * Call this once at the beginning of each render tick!
     */
    public void onStartRender() {
        this.renderTick++;
    }

    public void onTooltip(ItemStack itemStack, Tooltip tooltip) {
        if (itemStack == null || this.disablableMod.isModDisabled()) {
            return;
        }

        if (this.hoveredItemStack == null || itemStack != this.previousItemStack) {
            HoveredItemStack newHoveredItemStack = new HoveredItemStack(itemStack, this.configs);

            if (
                this.configs.tooltipKeepSelection.getValue() != TooltipKeepSelection.NEVER
                && this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS
                && this.hoveredItemStack != null
            ) {
                this.keepSelection(newHoveredItemStack);
            }

            this.hoveredItemStack = newHoveredItemStack;
            this.previousItemStack = itemStack;
        }

        this.lastTimeItemStackHovered = this.renderTick;

        this.isTooltipShown = this.shouldDisplayToolip();
        if (this.isTooltipShown) {
            this.lastTimeTooltipShown = this.renderTick;
        } else {
            return;
        }

        if (this.configs.tooltipInjectMethod.getValue() == TooltipInjectMethod.REPLACE) {
            tooltip.clear();
        } else if (!tooltip.isEmpty()) {
            tooltip.addSpacer();
        }

        if (this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS) {
            this.handleComponentPurpose(tooltip);
        } else {
            this.handleItemStackPurpose(tooltip);
        }
    }

    public InteractionResult onBundleTooltipImage() {
        return this.isTooltipShown()
            && this.configs.tooltipInjectMethod.getValue() == TooltipInjectMethod.REPLACE
            ? InteractionResult.SUCCESS
            : InteractionResult.PASS;
    }

    public InteractionResult onMouseScroll(double scrollDistance) {
        if (
            this.isComponentSelectionShown()
            && this.configs.controlsAllowScrolling.getBooleanValue()
        ) {
            Optional<Selection> optionalSelection = this.hoveredItemStack.getComponentSelection();

            if (optionalSelection.isPresent()) {
                optionalSelection.orElseThrow().updateByScrolling(scrollDistance);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    public void onCycleComponent(Selection.CycleType cycleType) {
        if (this.isComponentSelectionShown()) {
            this.hoveredItemStack.getComponentSelection().ifPresent(selection ->
                selection.updateByCycling(cycleType)
            );
        }
    }

    public void onCopyAction() {
        if (!this.isItemStackHovered()) {
            return;
        }

        switch (this.configs.clipboardCopy.getValue()) {
            case ITEM_STACK -> this.copyItemStack(this.hoveredItemStack.getItemStack());
            case GIVE_COMMAND -> this.copyGiveCommand(this.hoveredItemStack.getItemStack());
            case ClipboardCopy copyType when (
                copyType == ClipboardCopy.COMPONENT_VALUE
                && this.isComponentSelectionShown()
            ) -> this.hoveredItemStack.getSelectedComponent().ifPresent(this::copyComponentValue);
            default -> { /* Default not needed, copying disabled */ }
        }
    }

    public boolean isItemStackHovered() {
        return this.lastTimeItemStackHovered == this.renderTick;
    }

    public boolean isTooltipShown() {
        return this.isTooltipShown && this.lastTimeTooltipShown == this.renderTick;
    }

    private boolean isComponentSelectionShown() {
        return this.isTooltipShown()
            && this.configs.tooltipPurpose.getValue() == TooltipPurpose.COMPONENTS
            && this.configs.tooltipComponentValues.getBooleanValue();
    }

    private boolean shouldDisplayToolip() {
        TooltipDisplay tooltipDisplay = this.configs.tooltipDisplay.getValue();
        if (
            tooltipDisplay == TooltipDisplay.NEVER
            || tooltipDisplay == TooltipDisplay.HOLD && !this.configs.keyShowTooltip.isDownAnywhere()
        ) {
            return false;
        }

        return this.minecraftClient.options.advancedItemTooltips || !this.configs.tooltipAdvancedTooltips.getBooleanValue();
    }

    private void keepSelection(HoveredItemStack newHoveredItemStack) {
        switch (this.configs.tooltipKeepSelection.getValue()) {
            case INDEX -> this.keepSelectionByIndex(newHoveredItemStack);
            case TYPE -> this.keepSelectionByType(newHoveredItemStack);
            case NEVER -> { /* Don't keep the selection */ }
        }
    }

    private void keepSelectionByIndex(HoveredItemStack newHoveredItemStack) {
        this.hoveredItemStack.getComponentSelection().ifPresent(currentSelection ->
            newHoveredItemStack.getComponentSelection().ifPresent(newSelection ->
                newSelection.updateByValue(currentSelection.getSelectedIndex())
            )
        );
    }

    private void keepSelectionByType(HoveredItemStack newHoveredItemStack) {
        this.hoveredItemStack.getSelectedComponent().ifPresent(component -> {
            int indexOfComponent = newHoveredItemStack.getComponents().indexOf(component.type());

            if (indexOfComponent < 0) {
                this.keepSelectionByIndex(newHoveredItemStack);
                return;
            }

            newHoveredItemStack.getComponentSelection().ifPresent(newSelection ->
                newSelection.updateByValue(indexOfComponent)
            );
        });
    }

    private void handleComponentPurpose(Tooltip tooltip) {
        boolean showComponentValues = this.configs.tooltipComponentValues.getBooleanValue();

        tooltip.addComponentSelection(
            this.hoveredItemStack,
            !showComponentValues,
            this.configs.tooltipShowAmount.getBooleanValue()
        );

        if (this.hoveredItemStack.getComponents().isEmpty() || !showComponentValues) {
            return;
        }

        TypedDataComponent<?> selectedComponent = this.hoveredItemStack.getSelectedComponent().orElseThrow();

        tooltip.addSpacer().addComponentValue(
            selectedComponent,
            this.getTooltipFormatter(),
            this.getTooltipIndentation(),
            this.configs.tooltipColoredFormatting.getBooleanValue()
        );
    }

    private void handleItemStackPurpose(Tooltip tooltip) {
        tooltip.addItemStack(
            this.hoveredItemStack.getItemStack(),
            this.getTooltipFormatter(),
            this.configs.tooltipIndentation.getIntValue(),
            this.configs.tooltipColoredFormatting.getBooleanValue()
        );
    }

    private <T> void copyComponentValue(TypedDataComponent<T> component) {
        this.clipboard.copyComponentValue(
            component,
            this.getClipboardFormatter(),
            this.getClipboardIndentation(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private void copyItemStack(ItemStack itemStack) {
        this.clipboard.copyItemStack(
            itemStack,
            this.getClipboardFormatter(),
            this.getClipboardIndentation(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private void copyGiveCommand(ItemStack itemStack) {
        this.clipboard.copyGiveCommand(
            itemStack,
            this.getGiveCommandSelector(),
            this.configs.clipboardPrependSlash.getBooleanValue(),
            this.configs.clipboardIncludeCount.getBooleanValue(),
            this.configs.clipboardSuccessNotification.getBooleanValue()
        );
    }

    private Formatter getTooltipFormatter() {
        return switch (this.configs.tooltipFormatting.getValue()) {
            case SNBT -> this.snbtFormatter;
            case JSON -> this.jsonFormatter;
            case OBJECT -> this.objectFormatter;
        };
    }

    private Formatter getClipboardFormatter() {
        return switch (this.configs.clipboardFormatting.getValue()) {
            case SYNC -> this.getTooltipFormatter();
            case SNBT -> this.snbtFormatter;
            case JSON -> this.jsonFormatter;
            case OBJECT -> this.objectFormatter;
        };
    }

    private int getTooltipIndentation() {
        return this.configs.tooltipIndentation.getIntValue();
    }

    private int getClipboardIndentation() {
        int clipboardIndentation = this.configs.clipboardIndentation.getIntValue();

        return (clipboardIndentation == -1) ? this.getTooltipIndentation() : clipboardIndentation;
    }

    private String getGiveCommandSelector() {
        return switch (this.configs.clipboardSelector.getValue()) {
            case EVERYONE -> "@a";
            case NEAREST -> "@p";
            case SELF -> "@s";
            case PLAYER -> this.minecraftClient.getGameProfile().name();
        };
    }
}
