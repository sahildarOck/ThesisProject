package org.ljmu.thesis.helpers;

import org.ljmu.thesis.model.Config;
import org.ljmu.thesis.model.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.ljmu.thesis.commons.RuntimeConfig.getConfig;

public class ConfigHelper {
    public static String getGitReposProjectPath() throws IOException {
        Config config = getConfig();
        return config.getPath().getCropDataset() + File.separator + config.getPath().getGitRepos() + File.separator + config.getProjectToRun();
    }

    public static String getMetaDataCsvPath() throws IOException {
        Config config = getConfig();
        return config.getPath().getCropDataset() + File.separator + config.getPath().getMetadata() + File.separator + config.getProjectToRun() + ".csv";
    }

    public static String getCodeSmellRulesFilePath() throws IOException {
        Config config = getConfig();
        return Paths.get("src", "main", "resources", config.getPath().getCodeSmellRules()).toAbsolutePath().toString();
    }

    public static String getPmdBinPath() throws IOException {
        Config config = getConfig();
        return config.getPath().getPmdBin();
    }

    public static String getOutputDirectoryPath() throws IOException {
        Config config = getConfig();
        return config.getPath().getOutputDirectory() + File.separator;
    }

    public static String getProjectToRun() throws IOException {
        Config config = getConfig();
        return config.getProjectToRun();
    }

    public static String getBaseUrlForProjectToRun() throws IOException {
        String runningProjectName = getProjectToRun();
        Config config = getConfig();
        for (Project project : config.getProjects()) {
            if (project.getName().equals(runningProjectName)) {
                return project.getBaseUrl();
            }
        }
        throw new IllegalArgumentException("projectToRun not present in the projects[] in config.json...!!!");
    }
}
