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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

import dev.fixyl.componentviewer.option.TooltipComponents;
import dev.fixyl.componentviewer.util.ResultCache;

public class Components {
    private static final Comparator<Component<?>> COMPARATOR = Comparator.comparing(component -> component.type().toString());
    private static final ResultCache<Components> COMPONENTS_CACHE = new ResultCache<>();

    private final TooltipComponents componentsType;
    private final List<Component<?>> componentsList;
    private final int startOfRemovedComponents;

    private Components(TooltipComponents componentsType, Set<Component<?>> regularComponents, Set<Component<?>> removedComponents) {
        this.componentsType = componentsType;

        this.componentsList = Stream.concat(regularComponents.stream().sorted(Components.COMPARATOR), removedComponents.stream().sorted(Components.COMPARATOR)).toList();

        this.startOfRemovedComponents = regularComponents.size();
    }

    private Components(TooltipComponents componentsType, Set<Component<?>> regularComponents) {
        this(componentsType, regularComponents, new HashSet<>());
    }

    public TooltipComponents componentsType() {
        return this.componentsType;
    }

    public int size() {
        return this.componentsList.size();
    }

    public boolean isEmpty() {
        return this.componentsList.isEmpty();
    }

    // Suppresses the generic wildcard warning for SonarQube
    @SuppressWarnings("java:S1452")
    public Component<?> get(int index) {
        return this.componentsList.get(index);
    }

    public boolean isRemovedComponent(int index) {
        return index >= this.startOfRemovedComponents;
    }

    public static Components getAllComponents(ItemStack itemStack) {
        return Components.COMPONENTS_CACHE.cache(() -> {
            Set<Component<?>> regularComponents = Components.createComponentSet(itemStack.getComponents());

            return new Components(TooltipComponents.ALL, regularComponents);
        }, itemStack, TooltipComponents.ALL);
    }

    public static Components getDefaultComponents(ItemStack itemStack) {
        return Components.COMPONENTS_CACHE.cache(() -> {
            Set<Component<?>> defaultComponents = Components.createComponentSet(itemStack.getDefaultComponents());

            return new Components(TooltipComponents.DEFAULT, defaultComponents);
        }, itemStack, TooltipComponents.DEFAULT);
    }

    public static Components getChangedComponents(ItemStack itemStack) {
        return Components.COMPONENTS_CACHE.cache(() -> {
            Set<Component<?>> regularComponents = Components.createComponentSet(itemStack.getComponents());
            Set<Component<?>> defaultComponents = Components.createComponentSet(itemStack.getDefaultComponents());

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
