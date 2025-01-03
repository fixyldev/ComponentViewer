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

package dev.fixyl.componentviewer.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import dev.fixyl.componentviewer.ComponentViewer;
import dev.fixyl.componentviewer.config.type.AbstractConfig;

public final class ConfigManager {
    private final Class<?> configClass;
    private final Set<AbstractConfig<?>> configSet;

    private final ConfigAdapter configAdapter;
    private final Gson gson;
    private final File configFile;

    public ConfigManager(Class<?> configClass, String configFilename) {
        this.configClass = configClass;
        this.configSet = ConfigManager.createConfigSet(this.configClass);

        this.configAdapter = new ConfigAdapter();
        this.gson = new GsonBuilder().registerTypeAdapter(ConfigHelper.class, this.configAdapter).setPrettyPrinting().create();
        this.configFile = ComponentViewer.fabricLoader.getConfigDir().resolve(configFilename).toFile();

        this.readConfigFile();
    }

    public void readConfigFile() {
        if (!this.configFile.exists()) {
            ComponentViewer.logger.info("Config file '{}' not found! Creating new config file.", this.configFile.getAbsolutePath());
            this.writeConfigFile();
            return;
        }

        try (FileReader configFileReader = new FileReader(this.configFile)) {
            ConfigHelper configHelper = this.gson.fromJson(configFileReader, ConfigHelper.class);

            if (configHelper == null)
                throw new JsonParseException("Config file presumably empty");

            if (configHelper.doConfigRewrite()) {
                ComponentViewer.logger.info("Re-writing config file '{}'.", this.configFile.getAbsolutePath());
                this.writeConfigFile();
            }
        } catch (IOException | JsonParseException e) {
            ComponentViewer.logger.error(String.format("Error when reading config file '%s'! Re-writing config file.", this.configFile.getAbsoluteFile()), e);
            this.writeConfigFile();
        }
    }

    public void writeConfigFile() {
        try (FileWriter configFileWriter = new FileWriter(this.configFile)) {
            this.gson.toJson(new ConfigHelper(), configFileWriter);
        } catch (IOException | JsonIOException e) {
            ComponentViewer.logger.error(String.format("Error when writing config file '%s'! Some configs won't be saved across sessions!", this.configFile.getAbsolutePath()), e);
        }
    }

    private static Set<AbstractConfig<?>> createConfigSet(Class<?> configClass) {
        Set<AbstractConfig<?>> newConfigSet = new TreeSet<>(Comparator.comparing(AbstractConfig::id));
        Set<String> ids = new HashSet<>();

        Field[] fields = configClass.getDeclaredFields();

        for (Field field : fields) {
            if (!AbstractConfig.class.isAssignableFrom(field.getType()) || !Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers()))
                continue;

            try {
                AbstractConfig<?> config = (AbstractConfig<?>) field.get(null);

                if (ids.add(config.id()))
                    newConfigSet.add(config);
                else
                    ComponentViewer.logger.warn("Duplicate config id '{}' present! Config field {} will never be saved across sessions! All config ids within a ConfigManager's context frame should be unique!", config.id(), field.getName());
            } catch (IllegalAccessException e) {
                ComponentViewer.logger.error(String.format("Can't access config field %s!", field.getName()), e);
            }
        }

        return newConfigSet;
    }

    private class ConfigAdapter implements JsonSerializer<ConfigHelper>, JsonDeserializer<ConfigHelper> {
        @Override
        public JsonElement serialize(ConfigHelper src, Type type, JsonSerializationContext context) {
            JsonObject root = new JsonObject();

            for (AbstractConfig<?> config : ConfigManager.this.configSet) {
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
        public ConfigHelper deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                throw new JsonParseException("JSON root is not an object");

            JsonObject root = json.getAsJsonObject();

            boolean doConfigRewrite = false;

            for (AbstractConfig<?> config : ConfigManager.this.configSet) {
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
                    doConfigRewrite = true;
                }
            }

            return new ConfigHelper(doConfigRewrite);
        }
    }

    private record ConfigHelper(boolean doConfigRewrite) {
        private ConfigHelper() {
            this(false);
        }
    }
}
