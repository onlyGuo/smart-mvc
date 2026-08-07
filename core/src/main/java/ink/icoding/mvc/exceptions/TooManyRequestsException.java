package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a client that has exceeded an application request-rate limit.
 *
 * <p>The exception maps to HTTP 429 and can be used by throttling components or domain-specific
 * quotas that need the standard SmartMVC error representation.</p>
 */
public class TooManyRequestsException extends SmartMvcException {
    public TooManyRequestsException(String message) {
        super(HttpStatusCode.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", message);
    }
}
