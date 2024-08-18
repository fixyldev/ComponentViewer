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

package dev.fixyl.componentviewer.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.type.AbstractConfig;

public class ConfigHelper {
    private static final Comparator<AbstractConfig<?>> COMPARATOR = Comparator.comparing(AbstractConfig::id);

    private static Set<AbstractConfig<?>> configs;

    private ConfigHelper() {}

    public static Set<AbstractConfig<?>> configSet() {
        if (ConfigHelper.configs != null)
            return ConfigHelper.configs;

        ConfigHelper.configs = new TreeSet<>(ConfigHelper.COMPARATOR);
        Set<String> ids = new HashSet<>();

        Field[] fields = Configs.class.getDeclaredFields();

        for (Field field : fields) {
            if (!AbstractConfig.class.isAssignableFrom(field.getType()) || !Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers()))
                continue;

            try {
                AbstractConfig<?> config = (AbstractConfig<?>) field.get(null);

                if (ids.add(config.id()))
                    ConfigHelper.configs.add(config);
                else
                    ComponentViewer.logger.warn("Duplicate config id '{}' present! Config field {} will never be saved across sessions! All config ids should be unique!", config.id(), field.getName());
            } catch (IllegalAccessException e) {
                ComponentViewer.logger.error(String.format("Can't access config field %s!", field.getName()), e);
            }
        }

        return ConfigHelper.configs;
    }
}
