package org.ljmu.thesis.model.codesmells;

import org.ljmu.thesis.enums.Status;

public class RawPRRecord {
    private String id;
    private int reviewNumber;
    private int revisionNumber;
    private String author;
    private Status status;
    private String changeId;
    private String url;
    private String beforeCommitId;
    private String afterCommitId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getReviewNumber() {
        return reviewNumber;
    }

    public void setReviewNumber(int reviewNumber) {
        this.reviewNumber = reviewNumber;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
}
