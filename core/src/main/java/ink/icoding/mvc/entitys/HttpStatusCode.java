package ink.icoding.mvc.entitys;

/**
 * Spring-independent subset of HTTP status codes used by SmartMVC exceptions.
 *
 * <p>Keeping these numeric values in the core module allows domain and validation exceptions
 * to describe their HTTP semantics without importing Spring's HTTP abstractions.</p>
 */
public enum HttpStatusCode {
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    NOT_ACCEPTABLE(406),
    REQUEST_TIMEOUT(408),
    CONFLICT(409),
    GONE(410),
    PAYLOAD_TOO_LARGE(413),
    UNSUPPORTED_MEDIA_TYPE(415),
    UNPROCESSABLE_ENTITY(422),
    LOCKED(423),
    TOO_MANY_REQUESTS(429),
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504);

    private final int value;

    HttpStatusCode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
