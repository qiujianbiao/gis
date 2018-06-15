package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 1/26/2018.
 */
public class BatchcreateResult {
    private MsgResponse[] failedItems;
    private String successfulItems;

    public MsgResponse[] getFailedItems() {
        return failedItems;
    }

    public void setFailedItems(MsgResponse[] failedItems) {
        this.failedItems = failedItems;
    }

    public String getSuccessfulItems() {
        return successfulItems;
    }

    public void setSuccessfulItems(String successfulItems) {
        this.successfulItems = successfulItems;
    }
}
