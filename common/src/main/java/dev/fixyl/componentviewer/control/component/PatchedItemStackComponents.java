package dev.fixyl.componentviewer.control.component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;

final class PatchedItemStackComponents extends ItemStackComponents {

    private final Supplier<DataComponentPatch> dataComponentPatch;

    PatchedItemStackComponents(ItemStack itemStack, Supplier<DataComponentPatch> dataComponentPatch, TooltipComponents componentContext) {
        super(itemStack, componentContext);

        this.dataComponentPatch = dataComponentPatch;
    }

    @Override
    public int size() {
        return this.dataComponentPatch.get().size();
    }

    @Override
    public boolean isEmpty() {
        return this.dataComponentPatch.get().isEmpty();
    }

    @Override
    public List<DataComponentType<?>> getComponentTypes() {
        DataComponentPatch currentPatch = this.dataComponentPatch.get();

        return currentPatch.entrySet()
            .stream()
            .<DataComponentType<?>>map(Entry::getKey)
            .sorted(REGISTRY_ID_COMPARATOR)
            .sorted(Comparator.comparing(dataComponentType ->
                PatchedItemStackComponents.wasRemovedWithPatch(dataComponentType, currentPatch)
            ))
            .toList();
    }

    // Suppress SonarQube warning for not-handling Optionals the way
    // you should (null check can be made useless).
    // This doesn't work here because `DataComponentPatch.get`
    // can return `null` nonetheless. We don't have control over this.
    @SuppressWarnings("java:S2789")
    @Override
    public <T> @NullPermitted T getValue(DataComponentType<T> dataComponentType) {
        Optional<? extends T> optionalValue = this.dataComponentPatch.get().get(dataComponentType);

        if (optionalValue == null) {
            return null;
        } else if (optionalValue.isEmpty()) {
            return itemStack.getPrototype().get(dataComponentType);
        }

        return optionalValue.orElseThrow();
    }

    @Override
    public <T> boolean wasRemoved(DataComponentType<T> dataComponentType) {
        return PatchedItemStackComponents.wasRemovedWithPatch(
            dataComponentType,
            this.dataComponentPatch.get()
        );
    }

    @SuppressWarnings("java:S2789")
    private static <T> boolean wasRemovedWithPatch(DataComponentType<T> dataComponentType, DataComponentPatch dataComponentPatch) {
        Optional<? extends T> optionalValue = dataComponentPatch.get(dataComponentType);

        if (optionalValue == null) {
            return false;
        }

        return optionalValue.isEmpty();
    }
}
