package org.ljmu.thesis.commons;

import org.ljmu.thesis.helpers.JsonHelper;
import org.ljmu.thesis.model.Config;

import java.io.File;
import java.io.IOException;

public class RuntimeConfig {
    private static final String CONFIG_FILE_PATH = "/src/main/resources/config.json";

    private File configFile;
    private Config config;

    public RuntimeConfig() throws IOException {
        configFile = new File(CONFIG_FILE_PATH);
        config = JsonHelper.getObject(configFile, Config.class);
    }

    public Config getConfig() {
        return config;
    }
}
