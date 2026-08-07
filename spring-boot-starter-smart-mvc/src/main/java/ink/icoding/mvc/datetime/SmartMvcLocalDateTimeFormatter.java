package ink.icoding.mvc.datetime;

import ink.icoding.mvc.entitys.DateTimeConfig;
import org.springframework.format.Formatter;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Converts {@link LocalDateTime} query parameters and path variables for Spring MVC.
 *
 * <p>The formatter honors the configured request and response patterns together with the
 * incomplete-input policy used by Jackson deserialization.</p>
 */
public class SmartMvcLocalDateTimeFormatter implements Formatter<LocalDateTime> {

    private final DateTimeConfig config;

    public SmartMvcLocalDateTimeFormatter(DateTimeConfig config) {
        this.config = config;
    }

    @Override
    public LocalDateTime parse(String text, Locale locale) {
        return DateTimeSupport.parseLocalDateTime(text, config);
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return DateTimeSupport.format(object, config);
    }
}
