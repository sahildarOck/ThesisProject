package org.ljmu.thesis.model;

import org.ljmu.thesis.helpers.ConfigHelper;

import java.io.IOException;
import java.util.List;

public class Results {

    private String projectName;
    private int totalPRs;

    private PresenceResults presenceResults;
    private AbsenceResults absenceResults;

    private IndividualCRSmellResult crSmellLackOfCR;
    private IndividualCRSmellResult crSmellPingPong;
    private IndividualCRSmellResult crSmellSleepingReviews;
    private IndividualCRSmellResult crSmellMissingContext;
    private IndividualCRSmellResult crSmellLargeChangesets;
    private IndividualCRSmellResult crSmellReviewBuddies;

    public static Results computeResults(List<ProcessedPRRecord> prRecords) throws IOException {
        Results results = new Results();
        results.projectName = ConfigHelper.getProjectToRun();
        for (ProcessedPRRecord pr : prRecords) {

        }
    }

    public static class PresenceResults {
        private int totalPRsWithAtLeastOneCRSmell;
        private int totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell;
        private float crSmellsPresenceEffect;
    }

    public static class AbsenceResults {
        private int totalPRsWithNoCRSmell;
        private int totalPRsWithNoCRSmellAndIncreasedCodeSmell;
        private float crSmellsAbsenceEffect;
    }
}
