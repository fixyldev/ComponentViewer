package dev.fixyl.componentviewer.control;

import java.util.Optional;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;
import dev.fixyl.componentviewer.control.component.ItemStackComponents;

public class HoveredItemStack {

    private final ItemStack itemStack;
    private final Configs configs;

    private ItemStackComponents components;
    private @NullPermitted Selection componentSelection;

    public HoveredItemStack(ItemStack itemStack, Configs configs) {
        this.itemStack = itemStack;
        this.configs = configs;
    }

    /**
     * Get the {@link ItemStack} instance associated with this {@link HoveredItemStack}.
     *
     * @return the item stack of this instance
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * Get a {@link ItemStackComponents} instance which holds all currently relevant
     * components of the item stack based on the player's {@link TooltipComponents}
     * config.
     * <p>
     * It is not guaranteed that this is always the same instance.
     *
     * @implNote
     * This method and {@link HoveredItemStack#getComponentSelection() getComponentSelection()}
     * follow a hierarchical order, where calling {@code getComponentSelection()} from
     * within this method is strictly forbidden since it might result in an unintended infinite
     * recursion. This is because both associated fields are "lazy", meaning they will only
     * be updated when one of either method is called. Therefore, {@code getComponentSelection()}
     * depends on this method to update the components.
     *
     * @return all currently relevant components
     */
    public ItemStackComponents getComponents() {
        TooltipComponents componentContextFromConfig = this.configs.tooltipComponents.getValue();

        if (this.components == null || this.components.getComponentContext() != componentContextFromConfig) {
            this.components = ItemStackComponents.getComponentsBasedOnContext(this.itemStack, componentContextFromConfig);
        }

        return this.components;
    }

    /**
     * Get a {@link Selection} instance wrapped in an {@link Optional} representing
     * the currently selected component. To update the selection based on user input, use
     * {@link Selection#updateByCycling(Selection.CycleType) updateByCycling(CycleType)} and
     * {@link Selection#updateByScrolling(double) updateByScrolling(double)}
     * on the {@link Selection} instance.
     * <p>
     * It is not guaranteed that this is always the same instance.
     * <p>
     * An empty {@link Optional} is returned when no {@link Selection} instance is associated
     * with this {@link HoveredItemStack}. This is the case when the associated
     * {@link ItemStackComponents} instance has no components.
     *
     * @return the component selection, if present
     */
    public Optional<Selection> getComponentSelection() {
        int amountOfComponents = this.getComponents().size();

        if (amountOfComponents <= 0) {
            this.componentSelection = null;
        } else if (this.componentSelection == null) {
            this.componentSelection = new Selection(amountOfComponents);
        } else if (amountOfComponents != this.componentSelection.getAmount()) {
            this.componentSelection.setAmount(amountOfComponents);
        }

        return Optional.ofNullable(this.componentSelection);
    }

    /**
     * Get the {@link TypedDataComponent} instance wrapped in an {@link Optional}
     * which is currently selected by the player.
     * <p>
     * The {@link Optional} will be empty if no component selection exists, meaning
     * {@link HoveredItemStack#getComponentSelection() getComponentSelection()}
     * returns an empty {@link Optional} as well.
     *
     * @implNote
     * SonarQube warning for returning a wildcard generic in non-private methods is suppressed.
     * The component type is mixed and therefore not known, even for the method itself.
     *
     * @return the currently selected component, if a component selection exists
     * @see HoveredItemStack#getComponents()
     * @see HoveredItemStack#getComponentSelection()
     */
    @SuppressWarnings("java:S1452")
    public Optional<TypedDataComponent<?>> getSelectedComponent() {
        Optional<Selection> optionalComponentSelection = this.getComponentSelection();

        if (optionalComponentSelection.isEmpty()) {
            return Optional.empty();
        }

        ItemStackComponents currentComponents = this.getComponents();

        int selectedIndex = optionalComponentSelection.orElseThrow().getSelectedIndex();
        DataComponentType<?> selectedType = currentComponents.getComponentTypes().get(selectedIndex);

        return Optional.of(currentComponents.getTypedComponent(selectedType));
    }
}
