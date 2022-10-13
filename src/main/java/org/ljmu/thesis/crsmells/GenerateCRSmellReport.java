package org.ljmu.thesis.crsmells;

import org.ljmu.thesis.commons.JsonHelper;
import org.ljmu.thesis.crsmells.helpers.GerritApiHelper;
import org.ljmu.thesis.crsmells.model.GetChangeDetailOutput;
import org.ljmu.thesis.crsmells.model.GetChangeRevisionCommitOutput;

import java.io.IOException;
import java.util.logging.Logger;

public class GenerateCRSmellReport {
    private static final Logger LOGGER = Logger.getLogger(GenerateCRSmellReport.class.getName());

    private static String changeId = "Id3b90536d6f7afbcbfb5bc3a4cca8bff1df53627";

    public static void main(String[] args) throws IOException {
        GetChangeDetailOutput cdo = JsonHelper.getObject(GerritApiHelper.getChangeDetail(changeId), GetChangeDetailOutput.class);
        LOGGER.info(cdo.reviewers.REVIEWER[0].name);

        GetChangeRevisionCommitOutput crco = JsonHelper.getObject(GerritApiHelper.getChangeRevisionCommit(changeId), GetChangeRevisionCommitOutput.class);
        LOGGER.info(crco.message);

        LOGGER.info(JsonHelper.getFieldNames(GerritApiHelper.getChangeRevisionFiles(changeId, 7)).stream().reduce("", (str1, str2) -> str1 + str2));
    }
}
