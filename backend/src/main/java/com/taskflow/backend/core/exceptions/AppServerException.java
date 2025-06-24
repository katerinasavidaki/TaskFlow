package com.taskflow.backend.core.exceptions;

public class AppServerException extends AppGenericException {

    private static final String DEFAULT_CODE = "ServerError";

    public AppServerException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
