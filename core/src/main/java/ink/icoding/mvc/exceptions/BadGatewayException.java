package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an invalid response received from an upstream service and maps to HTTP 502.
 *
 * <p>Use this exception when SmartMVC is acting as a gateway or proxy and the dependency
 * responds with malformed, incomplete, or otherwise unusable data.</p>
 */
public class BadGatewayException extends SmartMvcException {
    public BadGatewayException(String message) {
        super(HttpStatusCode.BAD_GATEWAY, "BAD_GATEWAY", message);
    }
}
