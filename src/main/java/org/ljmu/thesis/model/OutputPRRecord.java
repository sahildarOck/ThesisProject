package org.ljmu.thesis.model;


import java.util.Date;

public class OutputPRRecord {
    // Existing
    private int revisionNumber;
    private String changeId;
    private String url;
    private String beforeCommitId;
    private String afterCommitId;
    private int iterationCount;

    // Fetched
    private String updatedFilesList; // Let's use ':' as delimiter within the String to separate items
    private String owner;
    private String reviewersList; // Let's use ':' as delimiter within the String to separate items
    private Date createdDate;
    private Date mergedDate;
    private int locChanged;
    private String subject;
    private String message;

    // Derived during CRSmells execution
    private boolean lackOfCRCRSmell;
    private boolean pingPongCRSmell;
    private boolean sleepingReviewsCRSmell;
    private boolean missingContextCRSmell;
    private boolean largeChangesetsCRSmell;

    // Derived during Code Smell execution
    private Boolean increasedCodeSmells;

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getReviewersList() {
        return reviewersList;
    }

    public void setReviewersList(String reviewersList) {
        this.reviewersList = reviewersList;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getMergedDate() {
        return mergedDate;
    }

    public void setMergedDate(Date mergedDate) {
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

    public Boolean getIncreasedCodeSmells() {
        return increasedCodeSmells;
    }

    public void setIncreasedCodeSmells(Boolean increasedCodeSmells) {
        this.increasedCodeSmells = increasedCodeSmells;
    }
}
