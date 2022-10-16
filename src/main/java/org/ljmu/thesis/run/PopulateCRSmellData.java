package org.ljmu.thesis.run;

import org.ljmu.thesis.helpers.JsonHelper;
import org.ljmu.thesis.helpers.crsmells.GerritApiHelper;
import org.ljmu.thesis.model.crsmells.GetChangeDetailOutput;
import org.ljmu.thesis.model.crsmells.GetChangeRevisionCommitOutput;

import java.io.IOException;
import java.util.logging.Logger;

public class PopulateCRSmellData {
    private static final Logger LOGGER = Logger.getLogger(PopulateCRSmellData.class.getName());

    private static String changeId = "Id3b90536d6f7afbcbfb5bc3a4cca8bff1df53627";

    public static void main(String[] args) throws IOException {
        //  1. Get all RawPRRecords
        //  2. For each RawPRRecord
        //      i. Create 1 OutputPRRecord
        //      ii. Populate the existing fields for OutputPRRecord from RawPRRecord
        //      iii. Hit Gerrit endpoints and fetch GetChangeDetailOutput and GetChangeRevisionCommitOutput
        //      iv. Populate the fetched fields for OutputPRRecord from GetChangeDetailOutput and GetChangeRevisionCommitOutput
        //      v. Derive the CRSmells derived fields and populate them for OutputPRRecord


        GetChangeDetailOutput cdo = JsonHelper.getObject(GerritApiHelper.getChangeDetail(changeId), GetChangeDetailOutput.class);
        LOGGER.info(cdo.reviewers.REVIEWER[0].name);

        GetChangeRevisionCommitOutput crco = JsonHelper.getObject(GerritApiHelper.getChangeRevisionCommit(changeId), GetChangeRevisionCommitOutput.class);
        LOGGER.info(crco.message);

        LOGGER.info(JsonHelper.getFieldNames(GerritApiHelper.getChangeRevisionFiles(changeId, 7)).stream().reduce("", (str1, str2) -> str1 + str2));
    }
}
