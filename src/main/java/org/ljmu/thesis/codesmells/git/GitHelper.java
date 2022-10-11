package org.ljmu.thesis.codesmells.git;

import org.ljmu.thesis.codesmells.processbuilder.ProcessBuilderHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GitHelper {

    public static String checkout(String projectPath, String commitId) throws IOException {
        List<String> commands = Arrays.asList("git", "checkout", commitId);
        return ProcessBuilderHelper.startProcess(projectPath, commands);
    }
}
