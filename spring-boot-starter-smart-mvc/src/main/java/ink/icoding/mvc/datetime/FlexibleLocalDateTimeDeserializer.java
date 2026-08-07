package ink.icoding.mvc.datetime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ink.icoding.mvc.entitys.DateTimeConfig;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Deserializes {@link LocalDateTime} values with the configured SmartMVC input pattern.
 *
 * <p>When incomplete-input filling is enabled, shorter values such as a date, year-month, or
 * year are completed deterministically. Invalid text is reported through Jackson's standard
 * weird-string handling.</p>
 */
public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final DateTimeConfig config;

    public FlexibleLocalDateTimeDeserializer(DateTimeConfig config) {
        this.config = config;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String value = parser.getValueAsString();
        try {
            return DateTimeSupport.parseLocalDateTime(value, config);
        } catch (DateTimeParseException exception) {
            throw context.weirdStringException(value, LocalDateTime.class,
                    "Expected date-time format " + config.getRequestFormat());
        }
    }
}
