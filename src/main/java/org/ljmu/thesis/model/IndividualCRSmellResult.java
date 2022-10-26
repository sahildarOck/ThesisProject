package org.ljmu.thesis.model;

public class IndividualCRSmellResult {

    private PresenceResults presenceResults;
    private AbsenceResults absenceResults;

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
