package com.ericsson.fms.exception.http;

import com.ericsson.fms.constants.HttpErrorCode;
import org.springframework.http.HttpStatus;

import javax.json.Json;
import javax.json.JsonObject;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class HttpException extends Exception {
	private static final long serialVersionUID = -3067652089618109463L;
	private static final String ERROR_CODE = "error";
    private static final String ERROR_DESCRIPTION = "error_description";

    private String message;
    private Integer status;
    private String error;
    private String exception;
    private String path;
    private String timestamp;

    private HttpStatus httpStatus;
    private HttpErrorCode httpErrorCode;
    private String httpErrorDescription;

    public HttpException(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpException(HttpStatus httpStatus, HttpErrorCode httpErrorCode, String httpErrorDescription) {
        this.httpStatus = httpStatus;
        this.httpErrorCode = httpErrorCode;
        this.httpErrorDescription = httpErrorDescription;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        JsonObject json = Json.createObjectBuilder().add(ERROR_CODE, httpErrorCode.getCodeString())
                .add(ERROR_DESCRIPTION, httpErrorDescription).build();
        return json.toString();
    }

//    @Override
//    public String toString() {
//        JsonObject json = Json.createObjectBuilder().add("status", status)
//                .add("message", message).add("timestamp", timestamp).build();
//        return json.toString();
//    }
    public HttpErrorCode getHttpErrorCode() {
        return httpErrorCode;
    }

    public String getHttpErrorDescription() {
        return httpErrorDescription;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTimestamp() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        f.setTimeZone(utc);
        GregorianCalendar cal = new GregorianCalendar(utc);
        timestamp = f.format(cal.getTime());
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
