package ink.icoding.mvc.datetime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ink.icoding.mvc.entitys.DateTimeConfig;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Serializes common Java Time values with SmartMVC date, time, and time-zone configuration.
 *
 * <p>Local values use their dedicated output patterns, while instant-based values are first
 * normalized to the configured zone. The serializer is registered for each supported temporal
 * type by the Jackson auto-configuration.</p>
 *
 * @param <T> supported temporal type
 */
public class ConfiguredTemporalSerializer<T> extends StdSerializer<T> {

    private final DateTimeConfig config;

    public ConfiguredTemporalSerializer(Class<T> type, DateTimeConfig config) {
        super(type);
        this.config = config;
    }

    @Override
    public void serialize(T value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        if (value instanceof Instant) {
            generator.writeString(DateTimeSupport.format((Instant) value, config));
        } else if (value instanceof LocalDate) {
            generator.writeString(DateTimeSupport.format((LocalDate) value, config));
        } else if (value instanceof LocalTime) {
            generator.writeString(DateTimeSupport.format((LocalTime) value, config));
        } else if (value instanceof OffsetDateTime) {
            generator.writeString(DateTimeSupport.format((OffsetDateTime) value, config));
        } else if (value instanceof ZonedDateTime) {
            generator.writeString(DateTimeSupport.format((ZonedDateTime) value, config));
        } else {
            provider.defaultSerializeValue(value, generator);
        }
    }
}
