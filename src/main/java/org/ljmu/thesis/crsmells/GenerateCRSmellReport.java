package org.ljmu.thesis.crsmells;

import org.ljmu.thesis.commons.JsonHelper;
import org.ljmu.thesis.crsmells.helpers.GerritHelper;
import org.ljmu.thesis.crsmells.model.GetChangeDetailOutput;
import org.ljmu.thesis.crsmells.model.GetChangeRevisionCommitOutput;

import java.io.IOException;
import java.util.logging.Logger;

public class GenerateCRSmellReport {
    private static final Logger LOGGER = Logger.getLogger(GenerateCRSmellReport.class.getName());

    private static String changeId = "I787c070e3bd2733259709545111e3d010bda86bd";

    public static void main(String[] args) throws IOException {
        GetChangeDetailOutput cdo = JsonHelper.getObject(GerritHelper.getChangeDetail(changeId), GetChangeDetailOutput.class);
        LOGGER.info(cdo.owner.name);

        GetChangeRevisionCommitOutput crco = JsonHelper.getObject(GerritHelper.getChangeRevisionCommit(changeId), GetChangeRevisionCommitOutput.class);
        LOGGER.info(crco.message);
    }
}
