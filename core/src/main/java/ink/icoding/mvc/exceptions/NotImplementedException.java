package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents server functionality required by a request but not yet implemented.
 *
 * <p>The exception maps to HTTP 501 and should be used for unsupported server capabilities,
 * rather than for an unavailable dependency or a temporarily disabled feature.</p>
 */
public class NotImplementedException extends SmartMvcException {
    public NotImplementedException(String message) {
        super(HttpStatusCode.NOT_IMPLEMENTED, "NOT_IMPLEMENTED", message);
    }
}
