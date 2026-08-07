package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a timeout while the server is waiting for the client request to complete.
 *
 * <p>The exception maps to HTTP 408 and is distinct from {@link GatewayTimeoutException},
 * which describes a timeout while waiting for an upstream service.</p>
 */
public class RequestTimeoutException extends SmartMvcException {
    public RequestTimeoutException(String message) {
        super(HttpStatusCode.REQUEST_TIMEOUT, "REQUEST_TIMEOUT", message);
    }
}
