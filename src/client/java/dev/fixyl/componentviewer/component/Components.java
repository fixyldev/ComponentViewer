/*
 * MIT License
 *
 * Copyright (c) 2024 fixyldev
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

import dev.fixyl.componentviewer.config.Config;

public record Components(List<Component<?>> modifiedComponents, List<Component<?>> removedComponents) {
    private static final Comparator<Component<?>> comparator = Comparator.comparing(component -> component.type().toString());

    public static Components getComponents(ItemStack itemStack) {
        if (Config.COMPONENT_CHANGES.getValue())
            return Components.handleComponentChanges(itemStack);

        return Components.handleComponents(itemStack);
    }

    private static Components handleComponents(ItemStack itemStack) {
        ComponentMap componentMap = itemStack.getComponents();

        List<Component<?>> components = new ArrayList<>(componentMap.size());

        for (Component<?> component : componentMap)
            components.add(component);

        components.sort(Components.comparator);

        return new Components(components, new ArrayList<>(0));
    }

    private static Components handleComponentChanges(ItemStack itemStack) {
        Set<Component<?>> componentSet = Components.createComponentSet(itemStack.getComponents());
        Set<Component<?>> defaultComponentSet = Components.createComponentSet(itemStack.getDefaultComponents());

        Set<Component<?>> modifiedComponentSet = new HashSet<>(componentSet);
        modifiedComponentSet.removeAll(defaultComponentSet);

        Set<Component<?>> removedComponentSet = new HashSet<>(defaultComponentSet);
        Set<ComponentType<?>> componentTypes = componentSet.stream().map(Component::type).collect(Collectors.toSet());
        removedComponentSet.removeIf(defaultComponent -> componentTypes.contains(defaultComponent.type()));

        return new Components(Components.createSortedComponentList(modifiedComponentSet), Components.createSortedComponentList(removedComponentSet));
    }

    public int size() {
        return this.modifiedComponents.size() + this.removedComponents.size();
    }

    public boolean isEmpty() {
        return this.modifiedComponents.isEmpty() && this.removedComponents.isEmpty();
    }

    private static Set<Component<?>> createComponentSet(ComponentMap componentMap) {
        Set<Component<?>> componentSet = new HashSet<>(componentMap.size());

        for (Component<?> component : componentMap)
            componentSet.add(component);

        return componentSet;
    }

    private static List<Component<?>> createSortedComponentList(Set<Component<?>> componentSet) {
        List<Component<?>> sortedComponentList = new ArrayList<>(componentSet);

        sortedComponentList.sort(Components.comparator);

        return sortedComponentList;
    }
}
