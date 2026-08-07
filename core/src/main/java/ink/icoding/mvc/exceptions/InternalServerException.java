package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an uncategorized internal server failure and maps to HTTP 500.
 *
 * <p>The optional cause can preserve the originating technical error for centralized logging
 * while the public response exposes only the configured safe message and error code.</p>
 */
public class InternalServerException extends SmartMvcException {
    public InternalServerException(String message) {
        this(message, null);
    }

    public InternalServerException(String message, Throwable cause) {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", message, null, cause);
    }
}
