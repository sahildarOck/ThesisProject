package org.ljmu.thesis.codesmells;

import org.ljmu.thesis.codesmells.git.GitHelper;
import org.ljmu.thesis.codesmells.json.JsonHelper;
import org.ljmu.thesis.codesmells.model.PmdReport;
import org.ljmu.thesis.codesmells.pmd.PmdHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class GenerateCodeSmellReport {
    private static final Logger LOGGER = Logger.getLogger(String.valueOf(GenerateCodeSmellReport.class));
    private static final String PROJECT_PATH = File.separator + "Users" + File.separator + "srivastavas" + File.separator + "Downloads" + File.separator + "M.Sc."
            + File.separator + "datasets" + File.separator + "CROP_dataset" + File.separator + "git_repos" + File.separator + "eclipse.platform.ui";
    private static String filePath = File.separator + "tests" + File.separator + "org.eclipse.ui.tests.harness" + File.separator + "src" + File.separator + "org"
            + File.separator + "eclipse" + File.separator + "ui" + File.separator + "tests" + File.separator + "harness" + File.separator + "util" ;

    private static String beforeCommitId = "55fcccbfd6bcbfade0a387fda2ecc490a61475eb";
    private static String afterCommitId = "5556b634389127039c0200c9657fad698133b1bf";

    public static void main(String[] args) throws IOException {
        int initialSmellsCount = getSmellsCount(beforeCommitId, filePath);
        int finalSmellsCount = getSmellsCount(afterCommitId, filePath);

        LOGGER.info(String.format("Initial Smells Count: %d", initialSmellsCount));
        LOGGER.info(String.format("Final Smells Count: %d", finalSmellsCount));
        LOGGER.info(String.format("Is Increased: %s", finalSmellsCount > initialSmellsCount));
    }

    private static int getSmellsCount(String commitId, String filePath) throws IOException {
        String gitCheckoutOutput = GitHelper.checkout(PROJECT_PATH, commitId);
        LOGGER.info(String.format("Git checkout Output: %s", gitCheckoutOutput));
        String outputJson = PmdHelper.startPmdCodeSmellProcess(PROJECT_PATH, filePath);
//        LOGGER.info(outputJson);
        PmdReport report = JsonHelper.getObject(outputJson, PmdReport.class);
        return Arrays.stream(report.files).map(file -> file.violations.length).reduce(0, (len1, len2) -> len1 + len2);
    }
}
