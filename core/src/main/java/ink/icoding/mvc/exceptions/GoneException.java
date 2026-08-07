package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a resource that has been intentionally and permanently removed.
 *
 * <p>The exception maps to HTTP 410 and communicates that retrying the same resource location
 * is not expected to succeed, unlike a potentially temporary not-found condition.</p>
 */
public class GoneException extends SmartMvcException {
    public GoneException(String message) {
        super(HttpStatusCode.GONE, "GONE", message);
    }
}
