package org.ljmu.thesis.codesmells.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PmdReport {
    public PmdFile[] files;
}
