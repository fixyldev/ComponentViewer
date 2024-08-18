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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import dev.fixyl.componentviewer.ComponentViewer;

public final class ConfigManager {
    private static ConfigManager instance;

    private static final String CONFIG_FILENAME = "componentviewer-config.json";

    private final ConfigAdapter configAdapter;
    private final Gson gson;
    private final File configFile;

    private ConfigManager() {
        this.configAdapter = new ConfigAdapter();
        this.gson = new GsonBuilder().registerTypeAdapter(ConfigAdapter.class, this.configAdapter).setPrettyPrinting().create();
        this.configFile = ComponentViewer.fabricLoader.getConfigDir().resolve(ConfigManager.CONFIG_FILENAME).toFile();

        this.readConfigFile();
    }

    public static ConfigManager getInstance() {
        if (ConfigManager.instance == null)
            ConfigManager.instance = new ConfigManager();

        return ConfigManager.instance;
    }

    public void readConfigFile() {
        if (!this.configFile.exists()) {
            ComponentViewer.logger.info("No config file present! Creating new config file.");
            this.writeConfigFile();
            return;
        }

        try (FileReader configFileReader = new FileReader(this.configFile)) {
            ConfigAdapter parseResult = this.gson.fromJson(configFileReader, ConfigAdapter.class);

            if (parseResult == null)
                throw new JsonParseException("Config file presumably empty");

            if (parseResult.doConfigRewrite()) {
                ComponentViewer.logger.info("Re-writing config file.");
                this.writeConfigFile();
            }
        } catch (IOException | JsonParseException e) {
            ComponentViewer.logger.error("Error when reading config file! Re-writing config file.", e);
            this.writeConfigFile();
        }
    }

    public void writeConfigFile() {
        try (FileWriter configFileWriter = new FileWriter(this.configFile)) {
            this.gson.toJson(this.configAdapter, configFileWriter);
        } catch (IOException | JsonIOException e) {
            ComponentViewer.logger.error("Error when writing config file! Configs won't be saved across sessions!", e);
        }
    }
}
