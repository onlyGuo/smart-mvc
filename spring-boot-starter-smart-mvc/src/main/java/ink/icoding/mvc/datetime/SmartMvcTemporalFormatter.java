package ink.icoding.mvc.datetime;

import ink.icoding.mvc.entitys.DateTimeConfig;
import org.springframework.format.Formatter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * Converts common Java Time query parameters and path variables for Spring MVC.
 *
 * <p>A concrete target type is supplied when the formatter is registered, allowing one
 * implementation to support local date and time, instant, offset date-time, and zoned
 * date-time values while sharing all rules with Jackson conversion.</p>
 *
 * @param <T> supported temporal type
 */
public class SmartMvcTemporalFormatter<T> implements Formatter<T> {

    private final Class<T> type;
    private final DateTimeConfig config;

    public SmartMvcTemporalFormatter(Class<T> type, DateTimeConfig config) {
        this.type = type;
        this.config = config;
    }

    @Override
    public T parse(String text, Locale locale) {
        if (type == Instant.class) {
            return type.cast(DateTimeSupport.parseInstant(text, config));
        }
        if (type == LocalDate.class) {
            return type.cast(DateTimeSupport.parseLocalDate(text, config));
        }
        if (type == LocalTime.class) {
            return type.cast(DateTimeSupport.parseLocalTime(text, config));
        }
        if (type == OffsetDateTime.class) {
            return type.cast(DateTimeSupport.parseOffsetDateTime(text, config));
        }
        if (type == ZonedDateTime.class) {
            return type.cast(DateTimeSupport.parseZonedDateTime(text, config));
        }
        throw new IllegalArgumentException("Unsupported SmartMVC temporal type: " + type.getName());
    }

    @Override
    public String print(T object, Locale locale) {
        if (object instanceof Instant) {
            return DateTimeSupport.format((Instant) object, config);
        }
        if (object instanceof LocalDate) {
            return DateTimeSupport.format((LocalDate) object, config);
        }
        if (object instanceof LocalTime) {
            return DateTimeSupport.format((LocalTime) object, config);
        }
        if (object instanceof OffsetDateTime) {
            return DateTimeSupport.format((OffsetDateTime) object, config);
        }
        if (object instanceof ZonedDateTime) {
            return DateTimeSupport.format((ZonedDateTime) object, config);
        }
        throw new IllegalArgumentException("Unsupported SmartMVC temporal value: " + object);
    }
}
