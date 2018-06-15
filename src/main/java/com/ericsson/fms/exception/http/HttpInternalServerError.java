package com.ericsson.fms.exception.http;

import org.springframework.http.HttpStatus;

import com.ericsson.fms.constants.HttpErrorCode;

public class HttpInternalServerError extends HttpException {
    private static final long serialVersionUID = 2208912863274285266L;

    public HttpInternalServerError() {
        this("Internal Server Error");
    }

    public HttpInternalServerError(String httpErrorDescription) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorCode.SERVER_ERROR, httpErrorDescription);
    }
}
