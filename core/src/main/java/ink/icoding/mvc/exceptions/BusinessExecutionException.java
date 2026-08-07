package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an unexpected technical failure encountered while executing domain logic.
 *
 * <p>Unlike {@link BusinessException}, this exception maps to HTTP 500 and may retain a cause
 * for diagnostic logging while still suppressing creation of its own stack trace.</p>
 */
public class BusinessExecutionException extends SmartMvcException {
    public BusinessExecutionException(String message) {
        this(message, null);
    }

    public BusinessExecutionException(String message, Throwable cause) {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, "BUSINESS_EXECUTION_FAILED", message, null, cause);
    }
}
