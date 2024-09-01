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
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.type.AbstractConfig;

public class ConfigAdapter implements JsonSerializer<ConfigAdapter>, JsonDeserializer<ConfigAdapter> {
    private static final Set<AbstractConfig<?>> configSet = new TreeSet<>(Comparator.comparing(AbstractConfig::id));

    private boolean doConfigRewrite = false;

    public boolean doConfigRewrite() {
        return this.doConfigRewrite;
    }

    @Override
    public JsonElement serialize(ConfigAdapter src, Type type, JsonSerializationContext context) {
        JsonObject root = new JsonObject();

        for (AbstractConfig<?> config : ConfigAdapter.getConfigSet()) {
            String[] path = config.id().split("\\.");

            JsonObject node = root;

            try {
                for (int index = 0; index < path.length - 1; index++) {
                    JsonElement nextNode = node.get(path[index]);

                    if (nextNode == null) {
                        nextNode = new JsonObject();
                        node.add(path[index], nextNode);
                    } else if (!nextNode.isJsonObject())
                        throw new JsonSyntaxException(String.format("Equally named key '%s' as non-JSON object already present", path[index]));

                    node = nextNode.getAsJsonObject();
                }
            } catch (JsonSyntaxException e) {
                ComponentViewer.logger.error(String.format("Can't serialize config '%s'! Config won't be saved across sessions!", config.id()), e);
                continue;
            }

            node.add(path[path.length - 1], context.serialize(config.value()));
        }

        return root;
    }

    @Override
    public ConfigAdapter deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject())
            throw new JsonParseException("JSON root is not an object");

        JsonObject root = json.getAsJsonObject();

        ConfigAdapter parseResult = new ConfigAdapter();

        for (AbstractConfig<?> config : ConfigAdapter.getConfigSet()) {
            String[] path = config.id().split("\\.");

            JsonObject node = root;

            try {
                for (int index = 0; index < path.length - 1; index++) {
                    JsonElement nextNode = node.get(path[index]);

                    if (nextNode == null || !nextNode.isJsonObject())
                        throw new JsonParseException("Config identifier path not sufficient");

                    node = nextNode.getAsJsonObject();
                }

                JsonElement keyNode = node.get(path[path.length - 1]);

                if (keyNode == null)
                    throw new JsonParseException("Config key not present");

                config.setValue(context.deserialize(keyNode, config.type()));
            } catch (JsonParseException e) {
                ComponentViewer.logger.error(String.format("Can't parse config '%s'! Using in-memory reference instead.", config.id()), e);
                parseResult.doConfigRewrite = true;
            }
        }

        return parseResult;
    }

    private static Set<AbstractConfig<?>> getConfigSet() {
        if (!ConfigAdapter.configSet.isEmpty())
            return ConfigAdapter.configSet;

        Set<String> ids = new HashSet<>();

        Field[] fields = Configs.class.getDeclaredFields();

        for (Field field : fields) {
            if (!AbstractConfig.class.isAssignableFrom(field.getType()) || !Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers()))
                continue;

            try {
                AbstractConfig<?> config = (AbstractConfig<?>) field.get(null);

                if (ids.add(config.id()))
                    ConfigAdapter.configSet.add(config);
                else
                    ComponentViewer.logger.warn("Duplicate config id '{}' present! Config field {} will never be saved across sessions! All config ids should be unique!", config.id(), field.getName());
            } catch (IllegalAccessException e) {
                ComponentViewer.logger.error(String.format("Can't access config field %s!", field.getName()), e);
            }
        }

        return ConfigAdapter.configSet;
    }
}
