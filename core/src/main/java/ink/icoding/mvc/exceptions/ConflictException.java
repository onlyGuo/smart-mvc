package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a request that conflicts with the current state of a target resource.
 *
 * <p>The exception maps to HTTP 409 and is suitable for duplicate creation, optimistic-lock
 * conflicts, or state transitions that cannot be applied concurrently.</p>
 */
public class ConflictException extends SmartMvcException {
    public ConflictException(String message) {
        super(HttpStatusCode.CONFLICT, "CONFLICT", message);
    }

    public ConflictException(String code, String message, Object details) {
        super(HttpStatusCode.CONFLICT, code, message, details);
    }
}
