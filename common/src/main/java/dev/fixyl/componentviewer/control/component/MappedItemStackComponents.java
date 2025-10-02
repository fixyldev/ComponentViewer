package dev.fixyl.componentviewer.control.component;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.enums.TooltipComponents;

final class MappedItemStackComponents extends ItemStackComponents {

    private final Supplier<DataComponentMap> dataComponentMap;

    MappedItemStackComponents(ItemStack itemStack, Supplier<DataComponentMap> dataComponentMap, TooltipComponents componentContext) {
        super(itemStack, componentContext);

        this.dataComponentMap = dataComponentMap;
    }

    @Override
    public int size() {
        return this.dataComponentMap.get().size();
    }

    @Override
    public boolean isEmpty() {
        return this.dataComponentMap.get().isEmpty();
    }

    @Override
    public List<DataComponentType<?>> getComponentTypes() {
        return this.dataComponentMap.get().keySet().stream().sorted(REGISTRY_ID_COMPARATOR).toList();
    }

    @Override
    public <T> @NullPermitted T getValue(DataComponentType<T> dataComponentType) {
        return this.dataComponentMap.get().get(dataComponentType);
    }

    @Override
    public <T> boolean wasRemoved(DataComponentType<T> dataComponentType) {
        return false;
    }
}
