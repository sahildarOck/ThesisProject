package org.ljmu.thesis.codesmells.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"formatVersion", "pmdVersion", "timestamp", "suppressedViolations", "processingErrors", "configurationErrors"})
public class PmdReport {
    public PmdFile[] files;
}
