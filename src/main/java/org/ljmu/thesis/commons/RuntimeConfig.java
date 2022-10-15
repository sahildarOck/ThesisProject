package org.ljmu.thesis.commons;

import org.ljmu.thesis.helpers.JsonHelper;
import org.ljmu.thesis.model.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class RuntimeConfig {
    private static final String CONFIG_FILE_PATH = Paths.get("src", "main", "resources", "config.json").toAbsolutePath().toString();

    private static Config config;

    public static Config getConfig() throws IOException {
        if (config == null) {
            File configFile = new File(CONFIG_FILE_PATH);
            config = JsonHelper.getObject(configFile, Config.class);
        }
        return config;
    }
}
