package org.ljmu.thesis.crsmells.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Reviewers {
    public Developer[] REVIEWER;
}
