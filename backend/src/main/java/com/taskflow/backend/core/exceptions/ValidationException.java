package com.taskflow.backend.core.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ValidationException extends AppGenericException {

    private static final String DEFAULT_CODE = "ValidationError";
    private final BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        super(DEFAULT_CODE, "Validation Failed");
        this.bindingResult = bindingResult;
    }
}
