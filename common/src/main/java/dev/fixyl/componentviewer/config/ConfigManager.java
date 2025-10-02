package dev.fixyl.componentviewer.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

import org.slf4j.Logger;

import dev.fixyl.componentviewer.config.option.AdvancedOption;
import dev.fixyl.componentviewer.config.option.Options;

public final class ConfigManager {

    private final Set<String> optionIds;
    private final Set<AdvancedOption<?>> options;

    private final File configFile;
    private final ConfigAdapter configAdapter;
    private final Gson gson;

    private final Logger logger;

    public ConfigManager(File configFile, Logger logger) {
        this.optionIds = new HashSet<>();
        this.options = new TreeSet<>(Comparator.comparing(AdvancedOption::getId));

        this.configFile = configFile;
        this.configAdapter = new ConfigAdapter();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(ConfigHelper.class, this.configAdapter)
            .setPrettyPrinting()
            .create();

        this.logger = logger;
    }

    public void addOptions(Options options) {
        for (AdvancedOption<?> option : options.getOptions()) {
            if (this.optionIds.add(option.getId())) {
                this.options.add(option);
                continue;
            }

            this.logger.warn(
                "Duplicate config identifier '{}' present! All config identifiers within a ConfigManager's context frame should be unique!",
                option.getId()
            );
        }
    }

    public void readFromFile() {
        if (!this.configFile.exists()) {
            this.logger.info(
                "Config file '{}' not found! Creating new config file.",
                this.configFile.getAbsolutePath()
            );
            this.writeToFile();
            return;
        }

        try (FileReader configFileReader = new FileReader(this.configFile)) {
            ConfigHelper configHelper = this.gson.fromJson(configFileReader, ConfigHelper.class);

            if (configHelper == null) {
                throw new JsonParseException("Config file presumably empty");
            }

            if (configHelper.doConfigRewrite()) {
                this.logger.info(
                    "Re-writing config file '{}'.",
                    this.configFile.getAbsolutePath()
                );
                this.writeToFile();
            }
        } catch (IOException | JsonParseException e) {
            this.logger.error(String.format(
                "Error when reading config file '%s'! Re-writing config file.",
                this.configFile.getAbsoluteFile()
            ), e);
            this.writeToFile();
        }
    }

    public void writeToFile() {
        try (FileWriter configFileWriter = new FileWriter(this.configFile)) {
            this.gson.toJson(new ConfigHelper(), configFileWriter);
        } catch (IOException | JsonIOException e) {
            this.logger.error(String.format(
                "Error when writing config file '%s'! Some configs won't be saved across sessions!",
                this.configFile.getAbsolutePath()
            ), e);
        }
    }

    private class ConfigAdapter implements JsonSerializer<ConfigHelper>, JsonDeserializer<ConfigHelper> {

        @Override
        public JsonElement serialize(ConfigHelper src, Type type, JsonSerializationContext context) {
            JsonObject root = new JsonObject();

            for (AdvancedOption<?> option : ConfigManager.this.options) {
                String[] path = option.getId().split("\\.");

                JsonObject node = root;

                try {
                    for (int index = 0; index < path.length - 1; index++) {
                        JsonElement nextNode = node.get(path[index]);

                        if (nextNode == null) {
                            nextNode = new JsonObject();
                            node.add(path[index], nextNode);
                        } else if (!nextNode.isJsonObject()) {
                            throw new JsonSyntaxException(String.format(
                                "Equally named key '%s' as non-JSON object already present",
                                path[index]
                            ));
                        }

                        node = nextNode.getAsJsonObject();
                    }
                } catch (JsonSyntaxException e) {
                    ConfigManager.this.logger.error(String.format(
                        "Can't serialize config '%s'! Config won't be saved across sessions!",
                        option.getId()
                    ), e);
                    continue;
                }

                node.add(path[path.length - 1], context.serialize(option.getValue()));
            }

            return root;
        }

        @Override
        public ConfigHelper deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                throw new JsonParseException("JSON root is not an object");
            }

            JsonObject root = json.getAsJsonObject();

            boolean doConfigRewrite = false;

            for (AdvancedOption<?> config : ConfigManager.this.options) {
                String[] path = config.getId().split("\\.");

                JsonObject node = root;

                try {
                    for (int index = 0; index < path.length - 1; index++) {
                        JsonElement nextNode = node.get(path[index]);

                        if (nextNode == null || !nextNode.isJsonObject()) {
                            throw new JsonParseException("Config identifier path not sufficient");
                        }

                        node = nextNode.getAsJsonObject();
                    }

                    JsonElement keyNode = node.get(path[path.length - 1]);

                    if (keyNode == null) {
                        throw new JsonParseException("Config key not present");
                    }

                    config.setValue(context.deserialize(keyNode, config.getType()));
                } catch (JsonParseException e) {
                    ConfigManager.this.logger.error(String.format(
                        "Can't parse config '%s'! Using in-memory reference instead.",
                        config.getId()
                    ), e);
                    doConfigRewrite = true;
                }
            }

            return new ConfigHelper(doConfigRewrite);
        }
    }

    private static record ConfigHelper(boolean doConfigRewrite) {

        private ConfigHelper() {
            this(false);
        }
    }
}
