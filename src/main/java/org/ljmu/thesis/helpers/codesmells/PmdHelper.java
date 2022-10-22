package org.ljmu.thesis.helpers.codesmells;

import org.ljmu.thesis.helpers.ConfigHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PmdHelper {
    public static String startPmdCodeSmellProcessAndGetOutput(String projectPath, String filePath) throws IOException {
        return ProcessBuilderHelper.startProcessAndGetOutput(ConfigHelper.getPmdBinPath(), getPmdCommands(projectPath, filePath));
    }

    private static List<String> getPmdCommands(String projectPath, String filePath) throws IOException {
        return Arrays.asList("./run.sh", "pmd", "--no-cache", "-d", projectPath + File.separator + filePath,
                "-f", "json", "-R", ConfigHelper.getCodeSmellRulesFilePath());
    }
}
