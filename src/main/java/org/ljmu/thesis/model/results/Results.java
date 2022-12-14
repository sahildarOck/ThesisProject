package org.ljmu.thesis.model.results;

import org.ljmu.thesis.helpers.ConfigHelper;
import org.ljmu.thesis.model.ProcessedPRRecord;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class Results {

    private String projectName;
    private int totalPRs;
    private int totalPRsExcludedWithCorruptData;
    private String percentPRsExcludedWithCorruptData;
    private int totalPRsIncluded;

    private PresenceResults presenceResults;
    private AbsenceResults absenceResults;

    private IndividualCRSmellResult crSmellLackOfCR;
    private IndividualCRSmellResult crSmellPingPong;
    private IndividualCRSmellResult crSmellSleepingReviews;
    private IndividualCRSmellResult crSmellMissingContext;
    private IndividualCRSmellResult crSmellLargeChangesets;
    private IndividualCRSmellResult crSmellReviewBuddies;

    private static DecimalFormat df = new DecimalFormat("0.00");

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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getTotalPRs() {
        return totalPRs;
    }

    public void setTotalPRs(int totalPRs) {
        this.totalPRs = totalPRs;
    }

    public int getTotalPRsExcludedWithCorruptData() {
        return totalPRsExcludedWithCorruptData;
    }

    public void setTotalPRsExcludedWithCorruptData(int totalPRsExcludedWithCorruptData) {
        this.totalPRsExcludedWithCorruptData = totalPRsExcludedWithCorruptData;
    }

    public String getPercentPRsExcludedWithCorruptData() {
        this.percentPRsExcludedWithCorruptData = df.format(this.totalPRs == 0 ? 0.0f : (((float) this.totalPRsExcludedWithCorruptData / this.totalPRs) * 100)) + " %";
        return this.percentPRsExcludedWithCorruptData;
    }

    public int getTotalPRsIncluded() {
        return totalPRsIncluded;
    }

    public static DecimalFormat getDf() {
        return df;
    }

    public PresenceResults getPresenceResults() {
        return presenceResults;
    }


    public AbsenceResults getAbsenceResults() {
        return absenceResults;
    }


    public IndividualCRSmellResult getCrSmellLackOfCR() {
        return crSmellLackOfCR;
    }


    public IndividualCRSmellResult getCrSmellPingPong() {
        return crSmellPingPong;
    }


    public IndividualCRSmellResult getCrSmellSleepingReviews() {
        return crSmellSleepingReviews;
    }

    public IndividualCRSmellResult getCrSmellMissingContext() {
        return crSmellMissingContext;
    }


    public IndividualCRSmellResult getCrSmellLargeChangesets() {
        return crSmellLargeChangesets;
    }


    public IndividualCRSmellResult getCrSmellReviewBuddies() {
        return crSmellReviewBuddies;
    }


    public static Results computeResults(List<ProcessedPRRecord> prRecords) throws IOException {
        Results results = new Results();

        for (ProcessedPRRecord pr : prRecords) {
            if (!pr.hasAtLeastOneUpdatedJavaFile()) {
                continue;
            }
            results.totalPRs++;

            if (null == pr.getCodeSmellsDifferenceCount()) {
                results.totalPRsExcludedWithCorruptData++;
                continue;
            }

            results.totalPRsIncluded++;

            if (pr.hasAtLeastOneCRSmell()) { // At least 1 CR Smell
                results.presenceResults.totalPRsWithAtLeastOneCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) { // At least 1 CR Smell > Has Code Smells increased
                    results.presenceResults.totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell++;
                }
            } else { // No CR Smell
                results.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) { // No CR Smell > Has Code Smells increased
                    results.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellLackOfCR()) {
                results.crSmellLackOfCR.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellLackOfCR.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellLackOfCR.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellLackOfCR.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellPingPong()) {
                results.crSmellPingPong.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellPingPong.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellPingPong.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellPingPong.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellSleepingReviews()) {
                results.crSmellSleepingReviews.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellSleepingReviews.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellSleepingReviews.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellSleepingReviews.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellMissingContext()) {
                results.crSmellMissingContext.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellMissingContext.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellMissingContext.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellMissingContext.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellLargeChangesets()) {
                results.crSmellLargeChangesets.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellLargeChangesets.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellLargeChangesets.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellLargeChangesets.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }

            if (pr.hasCrSmellReviewBuddies()) {
                results.crSmellReviewBuddies.presenceResults.totalPRsWithCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellReviewBuddies.presenceResults.totalPRsWithCRSmellAndIncreasedCodeSmell++;
                }
            } else {
                results.crSmellReviewBuddies.absenceResults.totalPRsWithNoCRSmell++;
                if (Boolean.TRUE.equals(pr.getIsIncreased())) {
                    results.crSmellReviewBuddies.absenceResults.totalPRsWithNoCRSmellAndIncreasedCodeSmell++;
                }
            }
        }

        return results;
    }

    private static class PresenceResults {
        private int totalPRsWithAtLeastOneCRSmell;
        private int totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell;
        private String crSmellsPresenceEffect;

        public int getTotalPRsWithAtLeastOneCRSmell() {
            return totalPRsWithAtLeastOneCRSmell;
        }

        public void setTotalPRsWithAtLeastOneCRSmell(int totalPRsWithAtLeastOneCRSmell) {
            this.totalPRsWithAtLeastOneCRSmell = totalPRsWithAtLeastOneCRSmell;
        }

        public int getTotalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell() {
            return totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell;
        }

        public void setTotalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell(int totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell) {
            this.totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell = totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell;
        }

        public String getCrSmellsPresenceEffect() {
            float crSmellsPresenceEffectValue = this.totalPRsWithAtLeastOneCRSmell == 0 ? 0.0f : (((float) this.totalPRsWithAtLeastOneCRSmellAndIncreasedCodeSmell / this.totalPRsWithAtLeastOneCRSmell) * 100);
            crSmellsPresenceEffect = df.format(crSmellsPresenceEffectValue) + " %";
            return crSmellsPresenceEffect;
        }

        public void setCrSmellsPresenceEffect(String crSmellsPresenceEffect) {
            this.crSmellsPresenceEffect = crSmellsPresenceEffect;
        }
    }

    private static class AbsenceResults {
        private int totalPRsWithNoCRSmell;
        private int totalPRsWithNoCRSmellAndIncreasedCodeSmell;
        private String crSmellsAbsenceEffect;

        public int getTotalPRsWithNoCRSmell() {
            return totalPRsWithNoCRSmell;
        }

        public void setTotalPRsWithNoCRSmell(int totalPRsWithNoCRSmell) {
            this.totalPRsWithNoCRSmell = totalPRsWithNoCRSmell;
        }

        public int getTotalPRsWithNoCRSmellAndIncreasedCodeSmell() {
            return totalPRsWithNoCRSmellAndIncreasedCodeSmell;
        }

        public void setTotalPRsWithNoCRSmellAndIncreasedCodeSmell(int totalPRsWithNoCRSmellAndIncreasedCodeSmell) {
            this.totalPRsWithNoCRSmellAndIncreasedCodeSmell = totalPRsWithNoCRSmellAndIncreasedCodeSmell;
        }

        public String getCrSmellsAbsenceEffect() {
            float crSmellsAbsenceEffectValue = this.totalPRsWithNoCRSmell == 0 ? 0.0f : (((float) this.totalPRsWithNoCRSmellAndIncreasedCodeSmell / this.totalPRsWithNoCRSmell) * 100);
            crSmellsAbsenceEffect = df.format(crSmellsAbsenceEffectValue) + " %";
            return crSmellsAbsenceEffect;
        }

        public void setCrSmellsAbsenceEffect(String crSmellsAbsenceEffect) {
            this.crSmellsAbsenceEffect = crSmellsAbsenceEffect;
        }
    }

    private static class IndividualCRSmellResult {

        private PresenceResults presenceResults;
        private AbsenceResults absenceResults;

        public IndividualCRSmellResult() {
            this.presenceResults = new PresenceResults();
            this.absenceResults = new AbsenceResults();
        }

        public PresenceResults getPresenceResults() {
            return presenceResults;
        }

        public void setPresenceResults(PresenceResults presenceResults) {
            this.presenceResults = presenceResults;
        }

        public AbsenceResults getAbsenceResults() {
            return absenceResults;
        }

        public void setAbsenceResults(AbsenceResults absenceResults) {
            this.absenceResults = absenceResults;
        }

        public static class PresenceResults {
            private int totalPRsWithCRSmell;
            private int totalPRsWithCRSmellAndIncreasedCodeSmell;
            private String crSmellPresenceEffect;

            public int getTotalPRsWithCRSmell() {
                return totalPRsWithCRSmell;
            }

            public void setTotalPRsWithCRSmell(int totalPRsWithCRSmell) {
                this.totalPRsWithCRSmell = totalPRsWithCRSmell;
            }

            public int getTotalPRsWithCRSmellAndIncreasedCodeSmell() {
                return totalPRsWithCRSmellAndIncreasedCodeSmell;
            }

            public void setTotalPRsWithCRSmellAndIncreasedCodeSmell(int totalPRsWithCRSmellAndIncreasedCodeSmell) {
                this.totalPRsWithCRSmellAndIncreasedCodeSmell = totalPRsWithCRSmellAndIncreasedCodeSmell;
            }

            public String getCrSmellPresenceEffect() {
                float crSmellPresenceEffectValue = this.totalPRsWithCRSmell == 0 ? 0.0f : (((float) this.totalPRsWithCRSmellAndIncreasedCodeSmell / this.totalPRsWithCRSmell) * 100);
                crSmellPresenceEffect = df.format(crSmellPresenceEffectValue) + " %";
                return crSmellPresenceEffect;
            }

            public void setCrSmellPresenceEffect(String crSmellPresenceEffect) {
                this.crSmellPresenceEffect = crSmellPresenceEffect;
            }
        }

        public static class AbsenceResults {
            private int totalPRsWithNoCRSmell;
            private int totalPRsWithNoCRSmellAndIncreasedCodeSmell;
            private String crSmellAbsenceEffect;

            public int getTotalPRsWithNoCRSmell() {
                return totalPRsWithNoCRSmell;
            }

            public void setTotalPRsWithNoCRSmell(int totalPRsWithNoCRSmell) {
                this.totalPRsWithNoCRSmell = totalPRsWithNoCRSmell;
            }

            public int getTotalPRsWithNoCRSmellAndIncreasedCodeSmell() {
                return totalPRsWithNoCRSmellAndIncreasedCodeSmell;
            }

            public void setTotalPRsWithNoCRSmellAndIncreasedCodeSmell(int totalPRsWithNoCRSmellAndIncreasedCodeSmell) {
                this.totalPRsWithNoCRSmellAndIncreasedCodeSmell = totalPRsWithNoCRSmellAndIncreasedCodeSmell;
            }

            public String getCrSmellAbsenceEffect() {
                float crSmellAbsenceEffectValue = this.totalPRsWithNoCRSmell == 0 ? 0.0f : (((float) this.totalPRsWithNoCRSmellAndIncreasedCodeSmell / this.totalPRsWithNoCRSmell) * 100);
                crSmellAbsenceEffect = df.format(crSmellAbsenceEffectValue) + " %";
                return crSmellAbsenceEffect;
            }

            public void setCrSmellAbsenceEffect(String crSmellAbsenceEffect) {
                this.crSmellAbsenceEffect = crSmellAbsenceEffect;
            }
        }
    }
}
