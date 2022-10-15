package org.ljmu.thesis.helpers;

import org.ljmu.thesis.model.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.ljmu.thesis.commons.RuntimeConfig.getConfig;

public class PathHelper {
    public static String getGitReposProjectPath() throws IOException {
        Config config = getConfig();
        return config.path.cropDataset + config.path.gitRepos + File.separator + config.projectToRun;
    }

    public static String getMetaDataProjectPath() throws IOException {
        Config config = getConfig();
        return config.path.cropDataset + config.path.metadata + File.separator + config.projectToRun;
    }

    public static String getCodeSmellRulesFilePath() {
        return Paths.get("src", "main", "resources", "CodeSmellRules.xml").toAbsolutePath().toString();
    }

    public static String getPmdBinPath() throws IOException {
        Config config = getConfig();
        return config.path.pmdBin;
    }
}
