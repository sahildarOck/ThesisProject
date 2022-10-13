package org.ljmu.thesis.codesmells.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PmdHelper {
    private static final String PMD_BIN_PATH = File.separator + "Users" + File.separator + "srivastavas"
            + File.separator + "Downloads" + File.separator + "M.Sc." + File.separator + "pmd-bin-6.50.0" + File.separator + "bin";
    private static final String CODE_SMELLS_RULES_FILE_PATH = Paths.get("src", "main", "resources", "CodeSmellRules.xml").toAbsolutePath().toString();

    public static String startPmdCodeSmellProcessAndGetOutput(String projectPath, String filePath) throws IOException {
        return ProcessBuilderHelper.startProcessAndGetOutput(PMD_BIN_PATH, getPmdCommands(projectPath, filePath));
    }

    private static List<String> getPmdCommands(String projectPath, String filePath) {
        return Arrays.asList("./run.sh", "pmd", "--no-cache", "-d", projectPath + filePath,
                "-f", "json", "-R", CODE_SMELLS_RULES_FILE_PATH);
    }
}
