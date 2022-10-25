package org.ljmu.thesis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Reviewers {
    public Developer[] REVIEWER;
}
