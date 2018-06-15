package com.ericsson.fms.exception.http;

import com.ericsson.fms.constants.HttpErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Created by ejioqiu on 3/5/2018.
 */
public class HttpGeofenceInvalidException extends HttpClientException {
    private static final long serialVersionUID = 1192043723909906690L;

    public HttpGeofenceInvalidException(String httpErrorDescription) {
        super(HttpStatus.BAD_REQUEST, HttpErrorCode.GEOFENCE_INVALID_ERROR, httpErrorDescription);
    }
}