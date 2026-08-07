package ink.icoding.mvc.entitys;

/**
 * Controls whether SmartMVC records a concise summary for each HTTP request.
 *
 * <p>When enabled, the Spring integration logs the request method and URI together with the
 * resulting status code and elapsed processing time. The configured level selects the SLF4J
 * method used for the summary, and the logger category is derived from the matched controller.</p>
 */
public class RequestLogConfig {

    private boolean enabled = true;
    private RequestLogLevel level = RequestLogLevel.INFO;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RequestLogLevel getLevel() {
        return level;
    }

    public void setLevel(RequestLogLevel level) {
        this.level = level == null ? RequestLogLevel.INFO : level;
    }
}
