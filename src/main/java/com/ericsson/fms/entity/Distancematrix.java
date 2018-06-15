package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 11/10/2017.
 */
public class Distancematrix {
    private MatrixResponse[] matrix;
    private String source;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public MatrixResponse[] getMatrix() {
        return matrix;
    }

    public void setMatrix(MatrixResponse[] matrix) {
        this.matrix = matrix;
    }
}
