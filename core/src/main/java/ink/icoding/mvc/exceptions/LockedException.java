package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an operation that cannot proceed because the target resource is locked.
 *
 * <p>The exception maps to HTTP 423 and can describe explicit application locks, editing
 * leases, or workflow locks that temporarily prevent modification.</p>
 */
public class LockedException extends SmartMvcException {
    public LockedException(String message) {
        super(HttpStatusCode.LOCKED, "LOCKED", message);
    }
}
