package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents malformed request syntax, invalid parameters, or unsupported request semantics.
 *
 * <p>The exception maps to HTTP 400 and optionally carries a custom error code and structured
 * details that the global exception handler includes in the standardized error response.</p>
 */
public class BadRequestException extends SmartMvcException {
    public BadRequestException(String message) {
        super(HttpStatusCode.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public BadRequestException(String code, String message, Object details) {
        super(HttpStatusCode.BAD_REQUEST, code, message, details);
    }
}
