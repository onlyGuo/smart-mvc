package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a request payload that exceeds the size accepted by the server.
 *
 * <p>The exception maps to HTTP 413 and can be raised by upload, import, or application-level
 * body-size checks that run beyond the servlet container's own limits.</p>
 */
public class PayloadTooLargeException extends SmartMvcException {
    public PayloadTooLargeException(String message) {
        super(HttpStatusCode.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", message);
    }
}
