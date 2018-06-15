package com.ericsson.fms.exception.http;

import org.springframework.http.HttpStatus;

import com.ericsson.fms.constants.HttpErrorCode;

public class HttpClientException extends HttpException {
    private static final long serialVersionUID = -2152073370604040787L;

    public HttpClientException(HttpStatus httpStatus, HttpErrorCode httpErrorCode, String httpErrorDescription) {
        super(httpStatus, httpErrorCode, httpErrorDescription);
    }
}
