package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an HTTP method that is not supported by the addressed resource.
 *
 * <p>The exception maps to HTTP 405 and is intended for application-level method restrictions
 * that need to use the same standardized error envelope as framework routing failures.</p>
 */
public class MethodNotAllowedException extends SmartMvcException {
    public MethodNotAllowedException(String message) {
        super(HttpStatusCode.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", message);
    }
}
