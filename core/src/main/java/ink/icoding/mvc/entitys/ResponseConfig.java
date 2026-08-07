package ink.icoding.mvc.entitys;

/**
 * Configures standardized controller responses and JSON number serialization.
 *
 * <p>The options determine whether ordinary and void controller results are wrapped, define
 * the default success message, and control string serialization of long integer values to
 * protect JavaScript clients from precision loss.</p>
 */
public class ResponseConfig {

    private boolean wrapEnabled = true;
    private boolean wrapVoid = true;
    private String successMessage = "success";
    private boolean longAsString = true;

    public boolean isWrapEnabled() {
        return wrapEnabled;
    }

    public void setWrapEnabled(boolean wrapEnabled) {
        this.wrapEnabled = wrapEnabled;
    }

    public boolean isWrapVoid() {
        return wrapVoid;
    }

    public void setWrapVoid(boolean wrapVoid) {
        this.wrapVoid = wrapVoid;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public boolean isLongAsString() {
        return longAsString;
    }

    public void setLongAsString(boolean longAsString) {
        this.longAsString = longAsString;
    }
}
