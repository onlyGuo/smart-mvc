package ink.icoding.mvc.entitys;

/**
 * Aggregates every framework-neutral SmartMVC configuration group in one object graph.
 *
 * <p>The core module owns the configuration model and defaults without depending on Spring.
 * The starter module subclasses this model and binds it to the {@code spring.smart.mvc}
 * configuration namespace.</p>
 */
public class SmartMvcConfig {

    private final ExceptionConfig exception = new ExceptionConfig();
    private final ResponseConfig response = new ResponseConfig();
    private final DateTimeConfig dateTime = new DateTimeConfig();
    private final ValidationConfig validation = new ValidationConfig();
    private final RequestLogConfig requestLog = new RequestLogConfig();
    private final AuthConfig auth = new AuthConfig();

    public ExceptionConfig getException() {
        return exception;
    }

    public ResponseConfig getResponse() {
        return response;
    }

    public DateTimeConfig getDateTime() {
        return dateTime;
    }

    public ValidationConfig getValidation() {
        return validation;
    }

    public RequestLogConfig getRequestLog() {
        return requestLog;
    }

    public AuthConfig getAuth() {
        return auth;
    }
}
