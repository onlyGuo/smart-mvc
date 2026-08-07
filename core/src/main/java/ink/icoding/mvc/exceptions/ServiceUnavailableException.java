package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents a service that is temporarily unable to process requests and maps to HTTP 503.
 *
 * <p>Typical uses include maintenance windows, exhausted capacity, or a required subsystem
 * that is unavailable but expected to recover.</p>
 */
public class ServiceUnavailableException extends SmartMvcException {
    public ServiceUnavailableException(String message) {
        super(HttpStatusCode.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", message);
    }
}
