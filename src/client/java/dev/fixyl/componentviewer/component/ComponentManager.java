/*
 * MIT License
 *
 * Copyright (c) 2025 fixyldev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.fixyl.componentviewer.component;

import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import dev.fixyl.componentviewer.option.TooltipComponents;
import dev.fixyl.componentviewer.util.ResultCache;

public final class ComponentManager {
    private final ResultCache<Components> componentsCache;

    public ComponentManager() {
        this.componentsCache = new ResultCache<>();
    }

    public Components getAllComponents(ItemStack itemStack) {
        return this.componentsCache.cache(() -> {
            Set<Component<?>> regularComponents = ComponentManager.createComponentSet(itemStack.getComponents());

            return new Components(TooltipComponents.ALL, regularComponents);
        }, itemStack, TooltipComponents.ALL);
    }

    public Components getDefaultComponents(ItemStack itemStack) {
        return this.componentsCache.cache(() -> {
            Set<Component<?>> defaultComponents = ComponentManager.createComponentSet(itemStack.getDefaultComponents());

            return new Components(TooltipComponents.DEFAULT, defaultComponents);
        }, itemStack, TooltipComponents.DEFAULT);
    }

    public Components getChangedComponents(ItemStack itemStack) {
        return this.componentsCache.cache(() -> {
            Set<Component<?>> regularComponents = ComponentManager.createComponentSet(itemStack.getComponents());
            Set<Component<?>> defaultComponents = ComponentManager.createComponentSet(itemStack.getDefaultComponents());

            Set<Component<?>> changedComponents = new HashSet<>(regularComponents);
            changedComponents.removeAll(defaultComponents);

            Set<Component<?>> removedComponents = new HashSet<>(defaultComponents);
            Set<ComponentType<?>> componentTypes = regularComponents.stream().map(Component::type).collect(Collectors.toSet());
            removedComponents.removeIf(defaultComponent -> componentTypes.contains(defaultComponent.type()));

            return new Components(TooltipComponents.CHANGES, changedComponents, removedComponents);
        }, itemStack, TooltipComponents.CHANGES);
    }

    private static Set<Component<?>> createComponentSet(ComponentMap componentMap) {
        Set<Component<?>> componentSet = new HashSet<>();

        for (Component<?> component : componentMap) {
            componentSet.add(component);
        }

        return componentSet;
    }
}
