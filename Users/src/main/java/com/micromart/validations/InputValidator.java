package com.micromart.validations;

import com.micromart.exceptions.BadRequestException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class InputValidator {

    public static void validate(BindingResult bindingResult) throws BadRequestException {
        if (!bindingResult.hasErrors()) {
            return;
        }
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder messageBuilder = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            messageBuilder.append(fieldError.getField());
            messageBuilder.append(" ");
            messageBuilder.append(fieldError.getDefaultMessage());
            messageBuilder.append("; ");
        }
        throw new BadRequestException(messageBuilder.toString().trim());
    }
}