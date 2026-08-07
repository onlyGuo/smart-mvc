package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a request that does not contain valid authentication credentials.
 *
 * <p>The exception maps to HTTP 401 and is raised when token extraction or identity validation
 * fails. Authorization failures for an authenticated identity use {@link ForbiddenException}.</p>
 */
public class UnauthorizedException extends SmartMvcException {
    public UnauthorizedException() {
        this("Authentication is required");
    }

    public UnauthorizedException(String message) {
        super(HttpStatusCode.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
