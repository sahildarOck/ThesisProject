package org.ljmu.thesis.run;

import org.ljmu.thesis.helpers.codesmells.GitHelper;
import org.ljmu.thesis.helpers.JsonHelper;
import org.ljmu.thesis.model.codesmells.PmdReport;
import org.ljmu.thesis.helpers.codesmells.PmdHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class GenerateCodeSmellReport {
    private static final Logger LOGGER = Logger.getLogger(GenerateCodeSmellReport.class.getName());
    private static final String PROJECT_PATH = File.separator + "Users" + File.separator + "srivastavas" + File.separator + "Downloads" + File.separator + "M.Sc."
            + File.separator + "datasets" + File.separator + "CROP_dataset" + File.separator + "git_repos" + File.separator + "eclipse.platform.ui";

    private static String filePath = File.separator + "bundles" + File.separator + "org.eclipse.e4.ui.workbench.renderers.swt" + File.separator + "src" + File.separator + "org"
            + File.separator + "eclipse" + File.separator + "e4" + File.separator + "ui" + File.separator + "workbench" + File.separator + "renderers" + File.separator + "swt" + File.separator + "MenuManagerRenderer.java";

    private static String beforeCommitId = "6ee8a8ba9bb2e56baf6c808d533c67530cbc94c9";
    private static String afterCommitId = "29102800c7e57a7c6d29097ee54e3204f5de42e2";

    public static void main(String[] args) throws IOException {
        int initialSmellsCount = getSmellsCount(beforeCommitId, filePath);
        LOGGER.info(String.format("Initial Smells Count: %d", initialSmellsCount));

        int finalSmellsCount = getSmellsCount(afterCommitId, filePath);
        LOGGER.info(String.format("Final Smells Count: %d", finalSmellsCount));

        LOGGER.info(String.format("Is Increased: %s", finalSmellsCount > initialSmellsCount));
    }

    private static int getSmellsCount(String commitId, String filePath) throws IOException {
        String gitCheckoutOutput = GitHelper.checkout(PROJECT_PATH, commitId);
        LOGGER.info(String.format("Git checkout Output: %s", gitCheckoutOutput));
        String outputJson = PmdHelper.startPmdCodeSmellProcessAndGetOutput(PROJECT_PATH, filePath);
//        LOGGER.info(outputJson);
        if(outputJson.contains("No such file")) {
            return 0;
        }
        PmdReport report = JsonHelper.getObject(outputJson, PmdReport.class);
        return Arrays.stream(report.files).map(file -> file.violations.length).reduce(0, (len1, len2) -> len1 + len2);
    }
}