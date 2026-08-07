package ink.icoding.mvc.entitys;

/**
 * Defines how SmartMVC maps application failures to the HTTP response status line.
 *
 * <p>{@link #HTTP_STATUS} preserves the failure-specific status, whereas {@link #ALWAYS_OK}
 * returns HTTP 200 and communicates the failure through the standardized response envelope.</p>
 */
public enum ExceptionStatusMode {
    HTTP_STATUS,
    ALWAYS_OK
}
