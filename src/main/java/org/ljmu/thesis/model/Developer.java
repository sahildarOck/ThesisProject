package org.ljmu.thesis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Developer {
    private long _account_id;
    private String name;

    public long get_account_id() {
        return _account_id;
    }

    public void set_account_id(long _account_id) {
        this._account_id = _account_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
