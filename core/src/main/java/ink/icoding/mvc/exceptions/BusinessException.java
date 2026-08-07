package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an expected rejection caused by an application domain rule.
 *
 * <p>The request may be structurally valid but cannot be completed in the current business
 * state. It maps to HTTP 422 and supports a domain-specific code and response details.</p>
 */
public class BusinessException extends SmartMvcException {
    public BusinessException(String message) {
        this("BUSINESS_ERROR", message, null);
    }

    public BusinessException(String code, String message) {
        this(code, message, null);
    }

    public BusinessException(String code, String message, Object details) {
        super(HttpStatusCode.UNPROCESSABLE_ENTITY, code, message, details);
    }
}
