package com.taskflow.backend.core.exceptions;

public class AppObjectInvalidArgumentException extends AppGenericException {

    private static final String DEFAULT_CODE = "InvalidArgument";

    public AppObjectInvalidArgumentException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
