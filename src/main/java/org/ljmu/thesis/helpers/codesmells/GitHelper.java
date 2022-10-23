package org.ljmu.thesis.helpers.codesmells;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GitHelper {
    public static String checkout(String projectPath, String commitId) throws IOException {
        List<String> command = Arrays.asList("git", "checkout", commitId);
        return ProcessBuilderHelper.startProcessAndGetOutput(projectPath, command);
    }

    public static String createNewWorkTree(String projectPath, String worktreeName, String commitId) throws IOException {
        List<String> command = Arrays.asList("git", "worktree", "add", worktreeName, commitId);
        return ProcessBuilderHelper.startProcessAndGetOutput(projectPath, command);
    }

    public static String removeWorkTree(String projectPath, String worktreeName) throws IOException {
        List<String> command = Arrays.asList("git", "worktree", "remove", "-f", "-f", worktreeName);
        String workTreeRemoveOutput = ProcessBuilderHelper.startProcessAndGetOutput(projectPath, command);
        FileUtils.deleteQuietly(new File(projectPath + File.separator + worktreeName));
        return workTreeRemoveOutput;
    }

    public static String pruneWorktree(String projectPath) throws IOException {
        List<String> command = Arrays.asList("git", "worktree", "prune");
        return ProcessBuilderHelper.startProcessAndGetOutput(projectPath, command);
    }
}
