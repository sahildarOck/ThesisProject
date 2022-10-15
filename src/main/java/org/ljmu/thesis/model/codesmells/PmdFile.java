package org.ljmu.thesis.model.codesmells;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PmdFile {
    public PmdViolation[] violations;
}
