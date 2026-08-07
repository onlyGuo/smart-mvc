package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents one or more request parameters that failed application validation.
 *
 * <p>The exception maps to HTTP 400 and can carry structured violation details, allowing the
 * global handler to return field-level feedback in the standard response envelope.</p>
 */
public class ParameterValidationException extends SmartMvcException {
    public ParameterValidationException(String message) {
        this(message, null);
    }

    public ParameterValidationException(String message, Object details) {
        super(HttpStatusCode.BAD_REQUEST, "PARAMETER_VALIDATION_FAILED", message, details);
    }
}
