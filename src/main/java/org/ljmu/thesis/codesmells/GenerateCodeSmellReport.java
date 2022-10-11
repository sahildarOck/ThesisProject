package org.ljmu.thesis.codesmells;

import org.ljmu.thesis.codesmells.json.JsonHelper;
import org.ljmu.thesis.codesmells.model.PmdReport;
import org.ljmu.thesis.codesmells.processbuilder.ProcessBuilderHelper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class GenerateCodeSmellReport {

    private static final String PROJECT_PATH = File.separator + "Users" + File.separator + "srivastavas" + File.separator + "Downloads" + File.separator + "M.Sc."
            + File.separator + "datasets" + File.separator + "CROP_dataset" + File.separator + "git_repos" + File.separator + "eclipse.platform.ui";
    private static final String FILE_PATH = File.separator + "tests" + File.separator + "org.eclipse.ui.tests.harness" + File.separator + "src" + File.separator + "org"
            + File.separator + "eclipse" + File.separator + "ui" + File.separator + "tests" + File.separator + "harness" + File.separator + "util" + File.separator + "UITestCase.java";
    private static final Logger LOGGER = Logger.getLogger(String.valueOf(GenerateCodeSmellReport.class));

    public static void main(String[] args) throws IOException {
        String outputJson = ProcessBuilderHelper.startPmdCodeSmellProcess(PROJECT_PATH, FILE_PATH);
        LOGGER.info(outputJson);
        PmdReport report = JsonHelper.getObject(outputJson, PmdReport.class);
        System.out.println(report.files.length);
    }
}
