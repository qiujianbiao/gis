package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 1/26/2018.
 */
public class MsgResponse {
    private String error;
    private String error_description;
    private String id;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
