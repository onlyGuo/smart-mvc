package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Base type for expected application failures that have explicit HTTP response semantics.
 *
 * <p>Each exception carries an HTTP status, stable error code, optional response details, and
 * optional cause. Writable stack traces and suppression are disabled to keep frequently used
 * control-flow exceptions inexpensive; centralized handlers are responsible for translation
 * to the public response envelope.</p>
 */
public abstract class SmartMvcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int status;
    private final String code;
    private final Object details;

    protected SmartMvcException(HttpStatusCode status, String code, String message) {
        this(status, code, message, null, null);
    }

    protected SmartMvcException(HttpStatusCode status, String code, String message, Object details) {
        this(status, code, message, details, null);
    }

    protected SmartMvcException(HttpStatusCode status, String code, String message,
                                Object details, Throwable cause) {
        super(message, cause, false, false);
        this.status = status.value();
        this.code = code;
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public Object getDetails() {
        return details;
    }
}
