package org.ljmu.thesis.model.crsmells;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetChangeDetailOutput {
    private Developer owner;
    public Reviewers reviewers;
    private Integer insertions;
    private Integer deletions;
    private String created;
    private String updated;
    private String submitted;

    public Developer getOwner() {
        return owner;
    }

    public void setOwner(Developer owner) {
        this.owner = owner;
    }

    public Integer getInsertions() {
        return insertions;
    }

    public void setInsertions(Integer insertions) {
        this.insertions = insertions;
    }

    public Integer getDeletions() {
        return deletions;
    }

    public void setDeletions(Integer deletions) {
        this.deletions = deletions;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getSubmitted() {
        if (submitted == null) {
            if (updated == null) {
                return created;
            }
            return updated;
        }
        return submitted;
    }

    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
}
