package ink.icoding.mvc.autoconfigure;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ink.icoding.mvc.datetime.FlexibleDateDeserializer;
import ink.icoding.mvc.datetime.FlexibleLocalDateTimeDeserializer;
import ink.icoding.mvc.datetime.FlexibleTemporalDeserializer;
import ink.icoding.mvc.datetime.ConfiguredTemporalSerializer;
import ink.icoding.mvc.datetime.LegacyDateSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Applies SmartMVC temporal and long-integer serialization rules to Jackson.
 *
 * <p>The customizer installs configured serializers and deserializers for legacy dates and
 * common Java Time types, including time-zone conversion for instant-based values. It can also
 * serialize long integers as strings to avoid precision loss in JavaScript clients.</p>
 */
public class SmartMvcJacksonCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    private final SmartMvcProperties properties;

    public SmartMvcJacksonCustomizer(SmartMvcProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        SimpleModule module = new SimpleModule("smart-mvc-json");
        DateTimeFormatter responseFormatter = DateTimeFormatter.ofPattern(
                properties.getDateTime().getResponseFormat());
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(responseFormatter));
        module.addDeserializer(LocalDateTime.class,
                new FlexibleLocalDateTimeDeserializer(properties.getDateTime()));
        module.addSerializer(Instant.class,
                new ConfiguredTemporalSerializer<Instant>(Instant.class, properties.getDateTime()));
        module.addDeserializer(Instant.class,
                new FlexibleTemporalDeserializer<Instant>(Instant.class, properties.getDateTime()));
        module.addSerializer(LocalDate.class,
                new ConfiguredTemporalSerializer<LocalDate>(LocalDate.class,
                        properties.getDateTime()));
        module.addDeserializer(LocalDate.class,
                new FlexibleTemporalDeserializer<LocalDate>(LocalDate.class,
                        properties.getDateTime()));
        module.addSerializer(LocalTime.class,
                new ConfiguredTemporalSerializer<LocalTime>(LocalTime.class,
                        properties.getDateTime()));
        module.addDeserializer(LocalTime.class,
                new FlexibleTemporalDeserializer<LocalTime>(LocalTime.class,
                        properties.getDateTime()));
        module.addSerializer(OffsetDateTime.class,
                new ConfiguredTemporalSerializer<OffsetDateTime>(OffsetDateTime.class,
                        properties.getDateTime()));
        module.addDeserializer(OffsetDateTime.class,
                new FlexibleTemporalDeserializer<OffsetDateTime>(OffsetDateTime.class,
                        properties.getDateTime()));
        module.addSerializer(ZonedDateTime.class,
                new ConfiguredTemporalSerializer<ZonedDateTime>(ZonedDateTime.class,
                        properties.getDateTime()));
        module.addDeserializer(ZonedDateTime.class,
                new FlexibleTemporalDeserializer<ZonedDateTime>(ZonedDateTime.class,
                        properties.getDateTime()));
        module.addSerializer(Date.class, new LegacyDateSerializer(properties.getDateTime()));
        module.addDeserializer(Date.class, new FlexibleDateDeserializer(properties.getDateTime()));
        if (properties.getResponse().isLongAsString()) {
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        }
        builder.modulesToInstall(module);
    }
}
