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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import dev.fixyl.componentviewer.ComponentViewer;

public class ConfigManager {
    private static final String CONFIG_FILENAME = "componentviewer-config.json";

    private final Gson gson;
    private final File configFile;

    public ConfigManager() {
        this.gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
        this.configFile = ComponentViewer.fabricLoader.getConfigDir().resolve(ConfigManager.CONFIG_FILENAME).toFile();

        this.readConfigFile();
    }

    public void readConfigFile() {
        if (!this.configFile.exists()) {
            ComponentViewer.logger.info("No \"Component Viewer\" config file present! Creating new config file.");
            this.writeConfigFile();
            return;
        }

        try (FileReader configFileReader = new FileReader(this.configFile)) {
            ConfigJson configJson = this.gson.fromJson(configFileReader, ConfigJson.class);

            if (configJson == null) {
                throw new JsonParseException("File is presumably empty!");
            }

            configJson.setConfigValues();
        } catch (IOException | JsonParseException e) {
            ComponentViewer.logger.error("Error when reading/parsing \"Component Viewer\" config file! Re-creating config file.", e);
            this.writeConfigFile();
        }
    }

    public void writeConfigFile() {
        try (FileWriter configFileWriter = new FileWriter(this.configFile)) {
            this.gson.toJson(ConfigJson.getConfigValues(), configFileWriter);
        } catch (IOException | JsonParseException e) {
            ComponentViewer.logger.error("Error when writing \"Component Viewer\" config file! Config will no be saved across sessions!", e);
        }
    }
}
