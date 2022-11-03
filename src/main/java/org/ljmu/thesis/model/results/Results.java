package org.ljmu.thesis.model.results;

import org.ljmu.thesis.helpers.ConfigHelper;
import org.ljmu.thesis.model.ProcessedPRRecord;

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

    public Results() throws IOException {
        this.projectName = ConfigHelper.getProjectToRun();
        this.totalPRs = 0;
        this.presenceResults = new PresenceResults();
        this.absenceResults = new AbsenceResults();
        crSmellLackOfCR = new IndividualCRSmellResult();
        crSmellPingPong = new IndividualCRSmellResult();
        crSmellSleepingReviews = new IndividualCRSmellResult();
        crSmellMissingContext = new IndividualCRSmellResult();
        crSmellLargeChangesets = new IndividualCRSmellResult();
        crSmellReviewBuddies = new IndividualCRSmellResult();
    }

    public static Results computeResults(List<ProcessedPRRecord> prRecords) throws IOException {
        Results results = new Results();

        for (ProcessedPRRecord pr : prRecords) {
            if (!pr.hasAtLeastOneUpdatedJavaFile()) {
                continue;
            }
            results.totalPRs++;
            if (pr.hasAtLeastOneCRSmell()) { // At least 1 CR Smell
                results.presenceResults.totalPRsWithAtLeastOneCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) { // At least 1 CR Smell > Has Code Smells increased
                    results.presenceResults.totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell++;
                }
            } else { // No CR Smell
                results.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) { // No CR Smell > Has Code Smells increased
                    results.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellLackOfCR()) {
                results.crSmellLackOfCR.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellLackOfCR.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellLackOfCR.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellLackOfCR.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellPingPong()) {
                results.crSmellPingPong.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellPingPong.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellPingPong.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellPingPong.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellSleepingReviews()) {
                results.crSmellSleepingReviews.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellSleepingReviews.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellSleepingReviews.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellSleepingReviews.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellMissingContext()) {
                results.crSmellMissingContext.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellMissingContext.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellMissingContext.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellMissingContext.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellLargeChangesets()) {
                results.crSmellLargeChangesets.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellLargeChangesets.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellLargeChangesets.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellLargeChangesets.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellReviewBuddies()) {
                results.crSmellReviewBuddies.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellReviewBuddies.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellReviewBuddies.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                    results.crSmellReviewBuddies.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }
        }
        // TODO: Compute crSmellsPresenceEffect and crSmellsAbsenceEffect for whole and individual

        return results;
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

    public static class IndividualCRSmellResult {

        private PresenceResults presenceResults;
        private AbsenceResults absenceResults;

        public IndividualCRSmellResult() {
            this.presenceResults = new PresenceResults();
            this.absenceResults = new AbsenceResults();
        }

        public static class PresenceResults {
            private int totalPRsWithCRSmell;
            private int totalPRsWithCRSmellAndIncreasedCodeSmell;
            private float crSmellPresenceEffect;
        }

        public static class AbsenceResults {
            private int totalPRsWithNoCRSmell;
            private int totalPRsWithNoCRSmellAndIncreasedCodeSmell;
            private float crSmellAbsenceEffect;
        }
    }
}
