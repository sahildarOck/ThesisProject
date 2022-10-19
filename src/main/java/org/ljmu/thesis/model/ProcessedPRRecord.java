package org.ljmu.thesis.model;

import java.time.LocalDate;
import java.util.List;

public class ProcessedPRRecord implements WritableCsv, Cloneable {
    // Existing
    private int reviewNumber;
    private String changeId;
    private String url;
    private int iterationCount;
    private String beforeCommitId;
    private String afterCommitId;

    // Fetched
    private List<String> updatedFilesList; // Let's use ':' as delimiter within the String to separate items
    private boolean atLeastOneUpdatedJavaFile;
    private String owner;
    private List<String> reviewersList; // Let's use ':' as delimiter within the String to separate items
    private LocalDate createdDate;
    private LocalDate mergedDate;
    private int locChanged;
    private String subject;
    private String message;

    // Computed during CRSmells execution
    private boolean crSmellLackOfCR;
    private boolean crSmellPingPong;
    private boolean crSmellSleepingReviews;
    private boolean crSmellMissingContext;
    private boolean crSmellLargeChangesets;
    private boolean crSmellReviewBuddies;

    // Computed during Code Smell execution
    private Integer codeSmellsDifferenceCount;
    private Boolean codeSmellsIncreased;

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

    public List<String> getUpdatedFilesList() {
        return updatedFilesList;
    }

    public void setUpdatedFilesList(List<String> updatedFilesList) {
        this.updatedFilesList = updatedFilesList;
    }

    public boolean hasAtLeastOneUpdatedJavaFile() {
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

    public boolean isCrSmellLackOfCR() {
        return crSmellLackOfCR;
    }

    public void setCrSmellLackOfCR(boolean crSmellLackOfCR) {
        this.crSmellLackOfCR = crSmellLackOfCR;
    }

    public boolean isCrSmellPingPong() {
        return crSmellPingPong;
    }

    public void setCrSmellPingPong(boolean crSmellPingPong) {
        this.crSmellPingPong = crSmellPingPong;
    }

    public boolean isCrSmellSleepingReviews() {
        return crSmellSleepingReviews;
    }

    public void setCrSmellSleepingReviews(boolean crSmellSleepingReviews) {
        this.crSmellSleepingReviews = crSmellSleepingReviews;
    }

    public boolean isCrSmellMissingContext() {
        return crSmellMissingContext;
    }

    public void setCrSmellMissingContext(boolean crSmellMissingContext) {
        this.crSmellMissingContext = crSmellMissingContext;
    }

    public boolean isCrSmellLargeChangesets() {
        return crSmellLargeChangesets;
    }

    public void setCrSmellLargeChangesets(boolean crSmellLargeChangesets) {
        this.crSmellLargeChangesets = crSmellLargeChangesets;
    }

    public boolean isCrSmellReviewBuddies() {
        return crSmellReviewBuddies;
    }

    public void setCrSmellReviewBuddies(boolean crSmellReviewBuddies) {
        this.crSmellReviewBuddies = crSmellReviewBuddies;
    }

    public int getCodeSmellsDifferenceCount() {
        return codeSmellsDifferenceCount;
    }

    public void setCodeSmellsDifferenceCount(Integer codeSmellsDifferenceCount) {
        this.codeSmellsDifferenceCount = codeSmellsDifferenceCount;
    }

    public Boolean getCodeSmellsIncreased() {
        return codeSmellsIncreased;
    }

    public void setCodeSmellsIncreased(Boolean codeSmellsIncreased) {
        this.codeSmellsIncreased = codeSmellsIncreased;
    }

    private String getStringOutput(Object obj) {
        return obj == null ? "NA" : String.valueOf(obj);
    }

    @Override
    public String[] getRecords() {
        String[] records = {String.valueOf(reviewNumber), changeId, url, String.valueOf(iterationCount), beforeCommitId, afterCommitId,
                String.valueOf(atLeastOneUpdatedJavaFile), createdDate.toString(), mergedDate.toString(), String.valueOf(locChanged),
                subject, message, String.valueOf(crSmellLackOfCR), String.valueOf(crSmellPingPong), String.valueOf(crSmellSleepingReviews), String.valueOf(crSmellMissingContext),
                String.valueOf(crSmellLargeChangesets), String.valueOf(crSmellReviewBuddies),
                getStringOutput(codeSmellsDifferenceCount), getStringOutput(codeSmellsIncreased)};
        return records;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
