package org.ljmu.thesis.helpers.codesmells;

import org.ljmu.thesis.commons.Utils;
import org.ljmu.thesis.helpers.ConfigHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PmdHelper {
    private static String PMD_RUN_MAC_FILE = "run.sh";
    private static String PMD_RUN_WIN_FILE = "pmd.bat";

    public static String startPmdCodeSmellProcessAndGetOutput(String projectPath, String filePath) throws IOException {
        return ProcessBuilderHelper.startProcessAndGetOutput(ConfigHelper.getPmdBinPath(), getPmdCommands(projectPath, filePath));
    }

    private static List<String> getPmdCommands(String projectPath, String filePath) throws IOException {
        if (Utils.isMacOS()) {
            return Arrays.asList("." + File.separator + PMD_RUN_MAC_FILE, "pmd", "--no-cache", "-d", projectPath + File.separator + filePath,
                    "-f", "json", "-R", ConfigHelper.getCodeSmellRulesFilePath());
        } else {
            return Arrays.asList(PMD_RUN_WIN_FILE, "--no-cache", "-d", projectPath + File.separator + filePath,
                    "-f", "json", "-R", ConfigHelper.getCodeSmellRulesFilePath());
        }
    }
}
