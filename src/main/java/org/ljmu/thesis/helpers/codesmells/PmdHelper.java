package org.ljmu.thesis.helpers.codesmells;

import org.ljmu.thesis.helpers.PathHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PmdHelper {
    public static String startPmdCodeSmellProcessAndGetOutput(String projectPath, String filePath) throws IOException {
        return ProcessBuilderHelper.startProcessAndGetOutput(PathHelper.getPmdBinPath(), getPmdCommands(projectPath, filePath));
    }

    private static List<String> getPmdCommands(String projectPath, String filePath) throws IOException {
        return Arrays.asList("./run.sh", "pmd", "--no-cache", "-d", projectPath + filePath,
                "-f", "json", "-R", PathHelper.getCodeSmellRulesFilePath());
    }
}
