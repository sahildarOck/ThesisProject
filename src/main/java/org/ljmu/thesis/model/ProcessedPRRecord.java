package org.ljmu.thesis.model;

import java.time.LocalDate;
import java.util.List;

public class ProcessedPRRecord implements WritableCsv {
    // Existing
    private int reviewNumber;
    private String changeId;
    private String url;
    private int iterationCount;
    private String beforeCommitId;
    private String afterCommitId;

    // Fetched
    private String updatedFilesList; // Let's use ':' as delimiter within the String to separate items
    private boolean atLeastOneUpdatedJavaFile;
    private String owner;
    private List<String> reviewersList; // Let's use ':' as delimiter within the String to separate items
    private LocalDate createdDate;
    private LocalDate mergedDate;
    private int locChanged;
    private String subject;
    private String message;

    // Derived during CRSmells execution
    private boolean lackOfCRCRSmell;
    private boolean pingPongCRSmell;
    private boolean sleepingReviewsCRSmell;
    private boolean missingContextCRSmell;
    private boolean largeChangesetsCRSmell;
    private boolean reviewBuddiesCRSmell;

    // Derived during Code Smell execution
    private Boolean increasedCodeSmells;

    public int getReviewNumber() {
        return reviewNumber;
    }

    public void setReviewNumber(int reviewNumber) {
        this.reviewNumber = reviewNumber;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBeforeCommitId() {
        return beforeCommitId;
    }

    public void setBeforeCommitId(String beforeCommitId) {
        this.beforeCommitId = beforeCommitId;
    }

    public String getAfterCommitId() {
        return afterCommitId;
    }

    public void setAfterCommitId(String afterCommitId) {
        this.afterCommitId = afterCommitId;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public String getUpdatedFilesList() {
        return updatedFilesList;
    }

    public void setUpdatedFilesList(String updatedFilesList) {
        this.updatedFilesList = updatedFilesList;
    }

    public boolean isAtLeastOneUpdatedJavaFile() {
        return atLeastOneUpdatedJavaFile;
    }

    public void setAtLeastOneUpdatedJavaFile(boolean atLeastOneUpdatedJavaFile) {
        this.atLeastOneUpdatedJavaFile = atLeastOneUpdatedJavaFile;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getReviewersList() {
        return reviewersList;
    }

    public void setReviewersList(List<String> reviewersList) {
        this.reviewersList = reviewersList;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getMergedDate() {
        return mergedDate;
    }

    public void setMergedDate(LocalDate mergedDate) {
        this.mergedDate = mergedDate;
    }

    public int getLocChanged() {
        return locChanged;
    }

    public void setLocChanged(int locChanged) {
        this.locChanged = locChanged;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLackOfCRCRSmell() {
        return lackOfCRCRSmell;
    }

    public void setLackOfCRCRSmell(boolean lackOfCRCRSmell) {
        this.lackOfCRCRSmell = lackOfCRCRSmell;
    }

    public boolean isPingPongCRSmell() {
        return pingPongCRSmell;
    }

    public void setPingPongCRSmell(boolean pingPongCRSmell) {
        this.pingPongCRSmell = pingPongCRSmell;
    }

    public boolean isSleepingReviewsCRSmell() {
        return sleepingReviewsCRSmell;
    }

    public void setSleepingReviewsCRSmell(boolean sleepingReviewsCRSmell) {
        this.sleepingReviewsCRSmell = sleepingReviewsCRSmell;
    }

    public boolean isMissingContextCRSmell() {
        return missingContextCRSmell;
    }

    public void setMissingContextCRSmell(boolean missingContextCRSmell) {
        this.missingContextCRSmell = missingContextCRSmell;
    }

    public boolean isLargeChangesetsCRSmell() {
        return largeChangesetsCRSmell;
    }

    public void setLargeChangesetsCRSmell(boolean largeChangesetsCRSmell) {
        this.largeChangesetsCRSmell = largeChangesetsCRSmell;
    }

    public boolean isReviewBuddiesCRSmell() {
        return reviewBuddiesCRSmell;
    }

    public void setReviewBuddiesCRSmell(boolean reviewBuddiesCRSmell) {
        this.reviewBuddiesCRSmell = reviewBuddiesCRSmell;
    }

    public Boolean getIncreasedCodeSmells() {
        return increasedCodeSmells;
    }

    public void setIncreasedCodeSmells(Boolean increasedCodeSmells) {
        this.increasedCodeSmells = increasedCodeSmells;
    }

    @Override
    public String[] getRecords() {
        String[] records = {String.valueOf(reviewNumber), changeId, url, String.valueOf(iterationCount), beforeCommitId, afterCommitId, updatedFilesList,
                String.valueOf(atLeastOneUpdatedJavaFile), createdDate.toString(), mergedDate.toString(), String.valueOf(locChanged),
                subject, message, String.valueOf(lackOfCRCRSmell), String.valueOf(pingPongCRSmell), String.valueOf(sleepingReviewsCRSmell), String.valueOf(missingContextCRSmell),
                String.valueOf(largeChangesetsCRSmell), String.valueOf(reviewBuddiesCRSmell)};
        return records;
    }
}
