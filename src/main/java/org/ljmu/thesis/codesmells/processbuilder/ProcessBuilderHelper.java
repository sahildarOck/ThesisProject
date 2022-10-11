package org.ljmu.thesis.codesmells.processbuilder;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ProcessBuilderHelper {
    private static final String PMD_BIN_PATH = File.separator + "Users" + File.separator + "srivastavas"
            + File.separator + "Downloads" + File.separator + "M.Sc." + File.separator + "pmd-bin-6.50.0" + File.separator + "bin";
    private static final String CODE_SMELLS_RULES_FILE_PATH = Paths.get("src", "main", "resources", "CodeSmellRules.xml").toAbsolutePath().toString();

    public static String startPmdCodeSmellProcess(String projectPath, String filePath) throws IOException {
        return startProcess(PMD_BIN_PATH, getPmdCommands(projectPath, filePath));
    }

//    public static String startGitProcess(String... command) {
//
//    }

    public static String startProcess(String workingDirectory, List<String> command) throws IOException {
        File file = new File(workingDirectory);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(file);
        pb.redirectErrorStream(true);

        return IOUtils.toString(pb.start().getInputStream(), StandardCharsets.UTF_8);

    }

    private static List<String> getPmdCommands(String projectPath, String filePath) {
        return Arrays.asList("./run.sh", "pmd", "--no-cache", "-d", projectPath + filePath,
                "-f", "json", "-R", CODE_SMELLS_RULES_FILE_PATH);
    }
}
