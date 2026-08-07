package ink.icoding.mvc.datetime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ink.icoding.mvc.entitys.DateTimeConfig;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Deserializes common Java Time values according to SmartMVC temporal configuration.
 *
 * <p>The implementation supports local date and time values as well as instant, offset, and
 * zoned date-time values. Instant-based targets accept both standard ISO input and configured
 * local text interpreted in the configured time zone.</p>
 *
 * @param <T> supported temporal type
 */
public class FlexibleTemporalDeserializer<T> extends JsonDeserializer<T> {

    private final Class<T> type;
    private final DateTimeConfig config;

    public FlexibleTemporalDeserializer(Class<T> type, DateTimeConfig config) {
        this.type = type;
        this.config = config;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        try {
            if (type == Instant.class) {
                return type.cast(DateTimeSupport.parseInstant(value, config));
            }
            if (type == LocalDate.class) {
                return type.cast(DateTimeSupport.parseLocalDate(value, config));
            }
            if (type == LocalTime.class) {
                return type.cast(DateTimeSupport.parseLocalTime(value, config));
            }
            if (type == OffsetDateTime.class) {
                return type.cast(DateTimeSupport.parseOffsetDateTime(value, config));
            }
            if (type == ZonedDateTime.class) {
                return type.cast(DateTimeSupport.parseZonedDateTime(value, config));
            }
        } catch (DateTimeException exception) {
            throw context.weirdStringException(value, type,
                    "value does not match the configured SmartMVC temporal format");
        }
        throw context.weirdStringException(value, type, "unsupported SmartMVC temporal type");
    }
}
