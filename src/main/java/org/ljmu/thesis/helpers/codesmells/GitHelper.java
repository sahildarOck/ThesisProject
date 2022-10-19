package org.ljmu.thesis.helpers.codesmells;

import java.io.IOException;
import java.util.ArrayList;
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

    public static List<String> removeWorkTree(String projectPath, String worktreeName) throws IOException {
        List<List<String>> commands = new ArrayList<>();
        commands.add(Arrays.asList("git", "worktree", "remove", "-f", "-f", worktreeName));
        commands.add(Arrays.asList("rm", "-r", "||", "true", worktreeName));
        return ProcessBuilderHelper.startProcessesAndGetOutput(projectPath, commands);
    }

    public static String pruneWorktree(String projectPath) throws IOException {
        List<String> command = Arrays.asList("git", "worktree", "prune");
        return ProcessBuilderHelper.startProcessAndGetOutput(projectPath, command);
    }
}
