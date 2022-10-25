package org.ljmu.thesis.helpers;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProcessBuilderHelper {
    public static String startProcessAndGetOutput(String workingDirectory, List<String> command) throws IOException {
        File file = new File(workingDirectory);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(file);
        pb.redirectErrorStream(true);

        return IOUtils.toString(pb.start().getInputStream(), StandardCharsets.UTF_8).trim();
    }

    public static List<String> startProcessesAndGetOutput(String workingDirectory, List<List<String>> commands) throws IOException {
        File file = new File(workingDirectory);
        ProcessBuilder pb = new ProcessBuilder();
        List<String> output = new ArrayList<>();
        for (List<String> command : commands) {
            pb.command(command);
            pb.directory(file);
            pb.redirectErrorStream(true);
            output.add(IOUtils.toString(pb.start().getInputStream(), StandardCharsets.UTF_8).trim());
        }
        return output;
    }
}
