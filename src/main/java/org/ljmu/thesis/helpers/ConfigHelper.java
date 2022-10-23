package org.ljmu.thesis.helpers;

import org.ljmu.thesis.model.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.ljmu.thesis.commons.RuntimeConfig.getConfig;

public class ConfigHelper {
    public static String getGitReposProjectPath() throws IOException {
        Config config = getConfig();
        return config.path.cropDataset + File.separator + config.path.gitRepos + File.separator + config.projectToRun;
    }

    public static String getMetaDataCsvPath() throws IOException {
        Config config = getConfig();
        return config.path.cropDataset + File.separator + config.path.metadata + File.separator + config.projectToRun + ".csv";
    }

    public static String getCodeSmellRulesFilePath() throws IOException {
        Config config = getConfig();
        return Paths.get("src", "main", "resources", config.path.codeSmellRules).toAbsolutePath().toString();
    }

    public static String getPmdBinPath() throws IOException {
        Config config = getConfig();
        return config.path.pmdBin;
    }

    public static String getOutputDirectoryPath() throws IOException {
        Config config = getConfig();
        return config.path.outputDirectory + File.separator;
    }

    public static String getProjectToRun() throws IOException {
        Config config = getConfig();
        return config.projectToRun;
    }
}
