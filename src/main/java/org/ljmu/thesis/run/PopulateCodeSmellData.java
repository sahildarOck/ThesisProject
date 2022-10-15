package org.ljmu.thesis.run;

import org.ljmu.thesis.helpers.JsonHelper;
import org.ljmu.thesis.helpers.PathHelper;
import org.ljmu.thesis.helpers.codesmells.GitHelper;
import org.ljmu.thesis.helpers.codesmells.PmdHelper;
import org.ljmu.thesis.model.codesmells.PmdReport;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class PopulateCodeSmellData {
    private static final Logger LOGGER = Logger.getLogger(PopulateCodeSmellData.class.getName());

    // TODO: Read from csv
    private static String filePath = File.separator + "bundles" + File.separator + "org.eclipse.e4.ui.workbench.renderers.swt" + File.separator + "src" + File.separator + "org"
            + File.separator + "eclipse" + File.separator + "e4" + File.separator + "ui" + File.separator + "workbench" + File.separator + "renderers" + File.separator + "swt" + File.separator + "MenuManagerRenderer.java";

    private static String beforeCommitId = "6ee8a8ba9bb2e56baf6c808d533c67530cbc94c9";
    private static String afterCommitId = "29102800c7e57a7c6d29097ee54e3204f5de42e2";

    private static String projectPath;

    public static void main(String[] args) throws IOException {
        initProperties();

        int initialSmellsCount = getSmellsCount(beforeCommitId, filePath);
        LOGGER.info(String.format("Initial Smells Count: %d", initialSmellsCount));

        int finalSmellsCount = getSmellsCount(afterCommitId, filePath);
        LOGGER.info(String.format("Final Smells Count: %d", finalSmellsCount));

        LOGGER.info(String.format("Is Increased: %s", finalSmellsCount > initialSmellsCount));
    }

    public static void initProperties() throws IOException {
        projectPath = PathHelper.getGitReposProjectPath();
    }

    private static int getSmellsCount(String commitId, String filePath) throws IOException {
        String gitCheckoutOutput = GitHelper.checkout(projectPath, commitId);
        LOGGER.info(String.format("Git checkout Output: %s", gitCheckoutOutput));
        String outputJson = PmdHelper.startPmdCodeSmellProcessAndGetOutput(projectPath, filePath);
        // LOGGER.info(outputJson);
        if (outputJson.contains("No such file")) {
            return 0;
        }
        PmdReport report = JsonHelper.getObject(outputJson, PmdReport.class);
        return Arrays.stream(report.files).map(file -> file.violations.length).reduce(0, (len1, len2) -> len1 + len2);
    }
}
