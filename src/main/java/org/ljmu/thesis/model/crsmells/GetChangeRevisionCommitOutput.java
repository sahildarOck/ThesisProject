package org.ljmu.thesis.model.crsmells;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetChangeRevisionCommitOutput {
    public String subject;
    public String message;
}
