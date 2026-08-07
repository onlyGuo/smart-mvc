package ink.icoding.mvc.entitys;

/**
 * Framework-neutral configuration for temporal parsing, formatting, and time-zone conversion.
 *
 * <p>Separate patterns are provided for date-time, date-only, and time-only values. The zone
 * identifier determines how instant-based values are converted, while the incomplete-input
 * policy controls whether missing date or time components are filled or rejected.</p>
 */
public class DateTimeConfig {

    private String requestFormat = "yyyy-MM-dd HH:mm:ss";
    private String responseFormat = "yyyy-MM-dd HH:mm:ss";
    private String dateRequestFormat = "yyyy-MM-dd";
    private String dateResponseFormat = "yyyy-MM-dd";
    private String timeRequestFormat = "HH:mm:ss";
    private String timeResponseFormat = "HH:mm:ss";
    private String zoneId = "system-default";
    private IncompleteDateTimePolicy incompleteInputPolicy = IncompleteDateTimePolicy.FILL_MISSING;

    public String getRequestFormat() {
        return requestFormat;
    }

    public void setRequestFormat(String requestFormat) {
        this.requestFormat = requestFormat;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public String getDateRequestFormat() {
        return dateRequestFormat;
    }

    public void setDateRequestFormat(String dateRequestFormat) {
        this.dateRequestFormat = dateRequestFormat;
    }

    public String getDateResponseFormat() {
        return dateResponseFormat;
    }

    public void setDateResponseFormat(String dateResponseFormat) {
        this.dateResponseFormat = dateResponseFormat;
    }

    public String getTimeRequestFormat() {
        return timeRequestFormat;
    }

    public void setTimeRequestFormat(String timeRequestFormat) {
        this.timeRequestFormat = timeRequestFormat;
    }

    public String getTimeResponseFormat() {
        return timeResponseFormat;
    }

    public void setTimeResponseFormat(String timeResponseFormat) {
        this.timeResponseFormat = timeResponseFormat;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public IncompleteDateTimePolicy getIncompleteInputPolicy() {
        return incompleteInputPolicy;
    }

    public void setIncompleteInputPolicy(IncompleteDateTimePolicy incompleteInputPolicy) {
        this.incompleteInputPolicy = incompleteInputPolicy;
    }
}
