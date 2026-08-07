package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a timeout while waiting for an upstream service and maps to HTTP 504.
 *
 * <p>Use this exception when the application is operating as a gateway or service client and
 * a dependency fails to respond within the allotted time.</p>
 */
public class GatewayTimeoutException extends SmartMvcException {
    public GatewayTimeoutException(String message) {
        super(HttpStatusCode.GATEWAY_TIMEOUT, "GATEWAY_TIMEOUT", message);
    }
}
