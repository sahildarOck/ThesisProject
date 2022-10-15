package org.ljmu.thesis.model.crsmells;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Developer {
    public String name;
    public String email;
    public String username;
}
