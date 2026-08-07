package ink.icoding.mvc.entitys;

/**
 * Controls registration and HTTP status behavior of the SmartMVC global exception handler.
 *
 * <p>The handler can be disabled completely, or configured to preserve each exception's real
 * HTTP status versus returning a uniform successful transport status with error details in
 * the response body.</p>
 */
public class ExceptionConfig {

    private boolean enabled = true;
    private ExceptionStatusMode statusMode = ExceptionStatusMode.HTTP_STATUS;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ExceptionStatusMode getStatusMode() {
        return statusMode;
    }

    public void setStatusMode(ExceptionStatusMode statusMode) {
        this.statusMode = statusMode;
    }
}
