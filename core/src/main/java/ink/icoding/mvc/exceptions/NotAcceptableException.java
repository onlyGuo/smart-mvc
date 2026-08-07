package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a request for which the server cannot produce an acceptable representation.
 *
 * <p>The exception maps to HTTP 406 and is appropriate when content negotiation or an explicit
 * response-format requirement cannot be satisfied.</p>
 */
public class NotAcceptableException extends SmartMvcException {
    public NotAcceptableException(String message) {
        super(HttpStatusCode.NOT_ACCEPTABLE, "NOT_ACCEPTABLE", message);
    }
}
