package ink.icoding.mvc.datetime;

import ink.icoding.mvc.entitys.DateTimeConfig;
import org.springframework.format.Formatter;

import java.util.Date;
import java.util.Locale;

/**
 * Converts legacy {@link Date} query parameters and path variables for Spring MVC.
 *
 * <p>Parsing and printing use the same SmartMVC pattern and time-zone rules as JSON conversion,
 * preventing controller parameter behavior from diverging from request-body behavior.</p>
 */
public class SmartMvcDateFormatter implements Formatter<Date> {

    private final DateTimeConfig config;

    public SmartMvcDateFormatter(DateTimeConfig config) {
        this.config = config;
    }

    @Override
    public Date parse(String text, Locale locale) {
        return DateTimeSupport.parseDate(text, config);
    }

    @Override
    public String print(Date object, Locale locale) {
        return DateTimeSupport.format(object, config);
    }
}
