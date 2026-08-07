package ink.icoding.mvc.datetime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ink.icoding.mvc.entitys.DateTimeConfig;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Deserializes legacy {@link Date} values according to SmartMVC temporal configuration.
 *
 * <p>Parsing delegates to {@link DateTimeSupport}, allowing configured local date-time input
 * and ISO instant input to be interpreted consistently in the configured time zone.</p>
 */
public class FlexibleDateDeserializer extends JsonDeserializer<Date> {

    private final DateTimeConfig config;

    public FlexibleDateDeserializer(DateTimeConfig config) {
        this.config = config;
    }

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        try {
            return DateTimeSupport.parseDate(value, config);
        } catch (DateTimeParseException exception) {
            throw context.weirdStringException(value, Date.class,
                    "Expected date-time format " + config.getRequestFormat());
        }
    }
}
