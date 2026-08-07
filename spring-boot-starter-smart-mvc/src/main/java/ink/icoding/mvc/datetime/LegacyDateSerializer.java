package ink.icoding.mvc.datetime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ink.icoding.mvc.entitys.DateTimeConfig;

import java.io.IOException;
import java.util.Date;

/**
 * Serializes legacy {@link Date} values with the configured SmartMVC response format and zone.
 *
 * <p>The conversion delegates to {@link DateTimeSupport}, ensuring legacy dates produce the
 * same textual representation as equivalent {@code Instant} values.</p>
 */
public class LegacyDateSerializer extends JsonSerializer<Date> {

    private final DateTimeConfig config;

    public LegacyDateSerializer(DateTimeConfig config) {
        this.config = config;
    }

    @Override
    public void serialize(Date value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeString(DateTimeSupport.format(value, config));
    }
}
