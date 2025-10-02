package dev.fixyl.componentviewer.control.component;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;

/**
 * This class is a wrapper and accessor around an item stack's components.
 * It can wrap around various contexts, eg. prototype components, patched components, etc.
 * <p>
 * Therefore, any change to the item stacks components will also change what this
 * instance will return and represent.
 *
 * @see ItemStack
 * @see DataComponentMap
 * @see DataComponentPatch
 */
public abstract sealed class ItemStackComponents permits MappedItemStackComponents, PatchedItemStackComponents {

    protected static final Comparator<DataComponentType<?>> REGISTRY_ID_COMPARATOR = Comparator.comparing(
        dataComponentType -> {
            ResourceLocation resourceLocation = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(dataComponentType);
            return (resourceLocation == null) ? "" : resourceLocation.toString();
        }
    );

    protected final ItemStack itemStack;
    protected final TooltipComponents componentContext;

    protected ItemStackComponents(ItemStack itemStack, TooltipComponents componentContext) {
        this.itemStack = itemStack;
        this.componentContext = componentContext;
    }

    /**
     * Get the {@link ItemStack} whos components this instance represents.
     *
     * @return the item stack instance
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * Get the context describing which kind of components this instance wraps around
     * and makes accessible.
     *
     * @return the component context as a {@link TooltipComponents} enum
     */
    public TooltipComponents getComponentContext() {
        return this.componentContext;
    }

    /**
     * Get the amount of components for the given context.
     *
     * @return the amount of components
     */
    public abstract int size();

    /**
     * Check whether no component exists in the given context.
     *
     * @return {@code true} if none exists, {@code false} otherwise
     * @see ItemStackComponents#size()
     */
    public abstract boolean isEmpty();

    /**
     * Get an immutable {@link List} holding all {@link DataComponentType} instances
     * in alphabetical order, sorted by their {@link ResourceLocation} in the registry.
     * <p>
     * Data component types, that aren't registered, come first.
     * <p>
     * Data component types, flagged as removed, come last.
     *
     * @implNote
     * SonarQube warning for returning a wildcard generic in non-private methods is suppressed.
     * The component type is mixed and therefore not known, even for the method itself.
     *
     * @return a list holding all data component types for the given context
     */
    @SuppressWarnings("java:S1452")
    public abstract List<DataComponentType<?>> getComponentTypes();

    /**
     * Get the value associated with a given {@link DataComponentType} on that item stack.
     * <p>
     * If the given data component type doesn't exist for the context used, {@code null} is returned.
     *
     * @param <T> the type of the value returned
     * @param dataComponentType the data component type to get the value for
     * @return the value associated with the given data component type, {@code null} if none is associated
     * @see ItemStackComponents#getTypedComponent(DataComponentType)
     */
    public abstract <T> @NullPermitted T getValue(DataComponentType<T> dataComponentType);

    /**
     * Get a {@link TypedDataComponent} which holds both the given {@link DataComponentType} and its
     * value based on the context used. This is, besides {@code null} checks, equivalent to getting
     * the associated value with {@link ItemStackComponents#getValue(DataComponentType)} manually
     * and constructing a {@link TypedDataComponent}.
     * <p>
     * If the given data component type doesn't exist for the context used, {@code null} is returned.
     *
     * @param <T> the type of the value held by the typed data component
     * @param dataComponentType the data component type to construct the typed data component from
     * @return the typed data component holding both type and value
     * @see ItemStackComponents#getValue(DataComponentType)
     */
    public <T> @NullPermitted TypedDataComponent<T> getTypedComponent(DataComponentType<T> dataComponentType) {
        @NullPermitted T value = this.getValue(dataComponentType);

        return (value == null) ? null : new TypedDataComponent<>(dataComponentType, value);
    }

    /**
     * Check whether the given {@link DataComponentType} was flagged as removed
     * for the given context on that item stack.
     *
     * @param <T> the type of possible values for the given data component type
     * @param dataComponentType the data component type to check for
     * @return {@code true} if flagged as removed, {@code false} otherwise
     */
    public abstract <T> boolean wasRemoved(DataComponentType<T> dataComponentType);

    /**
     * Get the index of a {@link DataComponentType} in the component type list.
     * If the component type doesn't exist in the list, {@code -1} is returned.
     * <p>
     * This is mostly equivalent to
     * {@code itemStackComponents.getComponentTypes().indexOf(dataComponentType)},
     * but permits {@code null} elements and treats them as non-existent.
     *
     * @param <T> the type of possible values for the given data component type
     * @param dataComponentType the data component type instance to look for
     * @return the first index the data component type was found, {@code -1} if not found
     * @see ItemStackComponents#getComponentTypes()
     */
    public <T> int indexOf(@NullPermitted DataComponentType<T> dataComponentType) {
        if (dataComponentType == null) {
            return -1;
        }

        return this.getComponentTypes().indexOf(dataComponentType);
    }

    /**
     * Get an {@link ItemStackComponents} instance with a context representing
     * all components present on the given {@link ItemStack}.
     *
     * @param itemStack the item stack
     * @return the item stack components instance
     */
    public static ItemStackComponents getComponents(ItemStack itemStack) {
        return new MappedItemStackComponents(itemStack, itemStack::getComponents, TooltipComponents.ALL);
    }

    /**
     * Get an {@link ItemStackComponents} instance with a context representing
     * all components present on the {@link ItemStack} by default.
     *
     * @param itemStack the item stack
     * @return the item stack components instance
     */
    public static ItemStackComponents getPrototypeComponents(ItemStack itemStack) {
        return new MappedItemStackComponents(itemStack, itemStack::getPrototype, TooltipComponents.DEFAULT);
    }

    /**
     * Get an {@link ItemStackComponents} instance with a context representing
     * all component patches found on the given {@link ItemStack}, where
     * component patches are all patches applied to the item stack's prototype
     * components.
     *
     * @param itemStack the item stack
     * @return the item stack components instance
     */
    public static ItemStackComponents getPatchedComponents(ItemStack itemStack) {
        return new PatchedItemStackComponents(itemStack, itemStack::getComponentsPatch, TooltipComponents.CHANGES);
    }

    /**
     * Get an {@link ItemStackComponents} instance with the given {@link ItemStack}
     * and context.
     *
     * @param itemStack the item stack
     * @param componentContext the component context as a {@link TooltipComponents} enum
     * @return the item stack components instance
     * @see ItemStackComponents#getComponents(ItemStack)
     * @see ItemStackComponents#getPrototypeComponents(ItemStack)
     * @see ItemStackComponents#getPatchedComponents(ItemStack)
     */
    public static ItemStackComponents getComponentsBasedOnContext(ItemStack itemStack, TooltipComponents componentContext) {
        return switch (componentContext) {
            case ALL -> ItemStackComponents.getComponents(itemStack);
            case DEFAULT -> ItemStackComponents.getPrototypeComponents(itemStack);
            case CHANGES -> ItemStackComponents.getPatchedComponents(itemStack);
        };
    }
}
