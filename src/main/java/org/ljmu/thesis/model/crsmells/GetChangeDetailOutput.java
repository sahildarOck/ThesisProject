package org.ljmu.thesis.model.crsmells;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetChangeDetailOutput {
    public Developer owner;
    public Reviewers reviewers;
    public Integer insertions;
    public Integer deletions;
    public String created;
    public String submitted;
}
