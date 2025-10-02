package dev.fixyl.componentviewer.control;

import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.control.component.ItemStackComponents;
import dev.fixyl.componentviewer.formatting.Formatter;
import dev.fixyl.componentviewer.formatting.FormattingException;

/**
 * A {@link Tooltip} is a wrapper around a {@link List} holding {@link Component} instances.
 * The main purpose is to provide easy methods for altering a tooltip's content
 * without having raw access to the entire {@link List} instance.
 * <p>
 * So a {@link Toolip} doesn't represent a "real" tooltip. All operations, no matter
 * how complex, operate on the {@link List} instance directly.
 * Therefore, any {@link List} implementation, not supporting operations like
 * {@code add}, {@code addAll}, {@code clear} and so on, will throw an
 * {@link UnsupportedOperationException} or similar, depending on the actual implementation.
 */
public class Tooltip {

    private static final Style HEADER_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);

    private static final Style COMPONENT_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
    private static final Style SELECTED_COMPONENT_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GREEN);
    private static final Style REMOVED_COMPONENT_STYLE = Style.EMPTY.withStrikethrough(true);
    private static final Style NOT_REGISTERED_COMPONENT_STYLE = Style.EMPTY.withItalic(true);

    private static final Style ERROR_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);

    private static final String CONTENT_INDENTATION = " ";

    private static final String NOT_REGISTERED_TRANSLATION_KEY = "componentviewer.tooltip.not_registered";

    private static final Map<TooltipComponents, String> COMPONENT_SELECTION_TRANSLATION_KEYS = Map.of(
        TooltipComponents.ALL, "componentviewer.tooltip.purpose.components.selection.all",
        TooltipComponents.DEFAULT, "componentviewer.tooltip.purpose.components.selection.default",
        TooltipComponents.CHANGES, "componentviewer.tooltip.purpose.components.selection.changes"
    );

    private static final Map<TooltipComponents, String> EMPTY_COMPONENT_SELECTION_TRANSLATION_KEYS = Map.of(
        TooltipComponents.ALL, "componentviewer.tooltip.purpose.components.selection.all.empty",
        TooltipComponents.DEFAULT, "componentviewer.tooltip.purpose.components.selection.default.empty",
        TooltipComponents.CHANGES, "componentviewer.tooltip.purpose.components.selection.changes.empty"
    );

    private static final Map<TooltipComponents, String> COMPONENT_SELECTION_WITH_AMOUNT_TRANSLATION_KEYS = Map.of(
        TooltipComponents.ALL, "componentviewer.tooltip.purpose.components.selection.all.with_amount",
        TooltipComponents.DEFAULT, "componentviewer.tooltip.purpose.components.selection.default.with_amount",
        TooltipComponents.CHANGES, "componentviewer.tooltip.purpose.components.selection.changes.with_amount"
    );

    private final List<Component> lines;

    /**
     * Construct a new {@link Tooltip} instance using the provided {@link List}
     * of {@link Component} elements.
     *
     * @param lines the lines of the tooltip as a list of text components
     */
    public Tooltip(List<Component> lines) {
        this.lines = lines;
    }

    /**
     * Get the amount of lines this tooltip currently holds.
     *
     * @return the amount of lines
     */
    public int size() {
        return this.lines.size();
    }

    /**
     * Check whether the tooltip holds any text components.
     *
     * @return {@code true} if empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.lines.isEmpty();
    }

    /**
     * Clear the tooltip's content. This effectively removes any
     * text component, currently held by the tooltip.
     *
     * @return the same tooltip instance
     */
    public Tooltip clear() {
        this.lines.clear();

        return this;
    }

    /**
     * Add an empty line to the tooltip.
     *
     * @return the same tooltip instance
     */
    public Tooltip addSpacer() {
        this.lines.add(Component.empty());

        return this;
    }

    /**
     * Add a header, provided by the translation key, to the tooltip.
     *
     * @param translationKey the key to grab translated text from
     * @param args the arguments for potential placeholders
     * @return the same tooltip instance
     */
    public Tooltip addHeader(String translationKey, Object... args) {
        this.lines.add(Component.translatable(translationKey, args).withStyle(HEADER_STYLE));

        return this;
    }

    /**
     * Add a section which displays all components of the given {@link HoveredItemStack}
     * and the currently selected component.
     * If the hovered item stack doesn't have any components associated with it,
     * a single-line notice stating that is displayed instead.
     *
     * @param hoveredItemStack the hovered item stack to grab the selection and components from
     * @param hideSelectedComponent should the currently selected component be hidden
     * @param showAmount whether the component amount is displayed in the header
     * @return the same tooltip instance
     */
    public Tooltip addComponentSelection(HoveredItemStack hoveredItemStack, boolean hideSelectedComponent, boolean showAmount) {
        ItemStackComponents components = hoveredItemStack.getComponents();
        TooltipComponents componentContext = components.getComponentContext();

        if (components.isEmpty()) {
            this.addHeader(EMPTY_COMPONENT_SELECTION_TRANSLATION_KEYS.get(componentContext));
            return this;
        }

        if (showAmount) {
            this.addHeader(COMPONENT_SELECTION_WITH_AMOUNT_TRANSLATION_KEYS.get(componentContext), components.size());
        } else {
            this.addHeader(COMPONENT_SELECTION_TRANSLATION_KEYS.get(componentContext));
        }

        // Double the indentation if more than one component needs to be displayed
        String indentationOfSelected = CONTENT_INDENTATION.repeat(Math.min(components.size(), 2));

        List<DataComponentType<?>> dataComponentTypes = components.getComponentTypes();
        @NullPermitted DataComponentType<?> selectedDataComponentType = (
            (hideSelectedComponent)
                ? null
                : dataComponentTypes.get(hoveredItemStack.getComponentSelection().orElseThrow().getSelectedIndex())
        );

        // Add all component types
        for (DataComponentType<?> dataComponentType : dataComponentTypes) {
            ResourceLocation resourceLocation = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(dataComponentType);
            MutableComponent componentTypeText = (
                (resourceLocation == null)
                    ? Component.translatable(NOT_REGISTERED_TRANSLATION_KEY)
                        .withStyle(COMPONENT_STYLE)
                        .withStyle(NOT_REGISTERED_COMPONENT_STYLE)
                    : Component.literal(resourceLocation.toString())
                        .withStyle(COMPONENT_STYLE)
            );

            if (dataComponentType == selectedDataComponentType) {
                componentTypeText.withStyle(SELECTED_COMPONENT_STYLE);
            }

            if (components.wasRemoved(dataComponentType)) {
                componentTypeText.withStyle(REMOVED_COMPONENT_STYLE);
            }

            this.lines.add(Component.literal(
                (dataComponentType == selectedDataComponentType)
                    ? indentationOfSelected
                    : CONTENT_INDENTATION
            ).append(componentTypeText));
        }

        return this;
    }

    /**
     * Add a {@link TypedDataComponent}'s value, formatted using the specified {@link Formatter},
     * to the tooltip.
     * <p>
     * If formatting fails, meaning the formatter throws a {@link FormattingException},
     * a warning stating this is displayed instead of the value.
     *
     * @param <T> the type of the component
     * @param component the component with the value to add
     * @param formatter a formatter implementation to use for formatting the value
     * @param formattingIndentation the size of indentation used for multi-line formatting
     * @param coloredFormatting whether the formatter is instructed to format using colors
     * @return the same tooltip instance
     */
    public <T> Tooltip addComponentValue(TypedDataComponent<T> component, Formatter formatter, int formattingIndentation, boolean coloredFormatting) {
        this.addHeader("componentviewer.tooltip.purpose.components.value");

        try {
            this.lines.addAll(formatter.componentToText(component, formattingIndentation, coloredFormatting, CONTENT_INDENTATION));
        } catch (FormattingException e) {
            this.addFormattingException();
        }

        return this;
    }

    /**
     * Add an {@link ItemStack}'s data, formatted using the specified {@link Formatter},
     * to the toolip.
     * <p>
     * This is usually the same kind of data a player gets when using the {@code /data get} in-game
     * command. Although the exact behaviour depends on the {@link Formatter}'s implementation.
     * <p>
     * If formatting fails, meaning the formatter throws a {@link FormattingException},
     * a warning stating this is displayed instead of the item stack.
     *
     * @param itemStack the item stack to add
     * @param formatter a formatter implementation to use for formatting the item stack
     * @param formattingIndentation the size of indentation used for multi-line formatting
     * @param coloredFormatting whether the formatter is instructed to format using colors
     * @return the same tooltip instance
     */
    public Tooltip addItemStack(ItemStack itemStack, Formatter formatter, int formattingIndentation, boolean coloredFormatting) {
        this.addHeader("componentviewer.tooltip.purpose.item_stack");

        try {
            this.lines.addAll(formatter.itemStackToText(itemStack, formattingIndentation, coloredFormatting, CONTENT_INDENTATION));
        } catch (FormattingException e) {
            this.addFormattingException();
        }

        return this;
    }

    private void addFormattingException() {
        this.lines.add(Component.literal(CONTENT_INDENTATION).append(Component.translatable("componentviewer.tooltip.formatting_exception").withStyle(ERROR_STYLE)));
    }
}
