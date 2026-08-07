package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a syntactically valid request whose semantic content cannot be processed.
 *
 * <p>The exception maps to HTTP 422 and supports a custom code and structured details for
 * application-specific semantic validation failures.</p>
 */
public class UnprocessableEntityException extends SmartMvcException {
    public UnprocessableEntityException(String message) {
        super(HttpStatusCode.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", message);
    }

    public UnprocessableEntityException(String code, String message, Object details) {
        super(HttpStatusCode.UNPROCESSABLE_ENTITY, code, message, details);
    }
}
