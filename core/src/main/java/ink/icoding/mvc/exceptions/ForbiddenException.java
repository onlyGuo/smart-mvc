package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an authenticated identity that is not authorized to perform the requested action.
 *
 * <p>The exception maps to HTTP 403 and distinguishes insufficient roles or permissions from
 * missing or invalid credentials, which are represented by {@link UnauthorizedException}.</p>
 */
public class ForbiddenException extends SmartMvcException {
    public ForbiddenException() {
        this("Access is forbidden");
    }

    public ForbiddenException(String message) {
        super(HttpStatusCode.FORBIDDEN, "FORBIDDEN", message);
    }
}
