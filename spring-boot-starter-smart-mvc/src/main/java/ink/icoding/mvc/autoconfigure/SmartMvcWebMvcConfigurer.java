package ink.icoding.mvc.autoconfigure;

import ink.icoding.mvc.auth.SmartMvcAuthInterceptor;
import ink.icoding.mvc.datetime.SmartMvcDateFormatter;
import ink.icoding.mvc.datetime.SmartMvcLocalDateTimeFormatter;
import ink.icoding.mvc.datetime.SmartMvcTemporalFormatter;
import ink.icoding.mvc.validation.NoOpValidator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Integrates SmartMVC authentication, temporal conversion, and validation with Spring MVC.
 *
 * <p>The configurer registers the authentication dispatcher with configured path exclusions,
 * installs formatters for legacy and Java Time request parameters, and substitutes a no-op
 * validator when request validation has been disabled.</p>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SmartMvcWebMvcConfigurer implements WebMvcConfigurer {

    private final SmartMvcAuthInterceptor authInterceptor;
    private final SmartMvcProperties properties;

    public SmartMvcWebMvcConfigurer(SmartMvcAuthInterceptor authInterceptor,
                                    SmartMvcProperties properties) {
        this.authInterceptor = authInterceptor;
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns(properties.getAuth().getExcludePaths());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new SmartMvcLocalDateTimeFormatter(properties.getDateTime()));
        registry.addFormatterForFieldType(Instant.class,
                new SmartMvcTemporalFormatter<Instant>(Instant.class, properties.getDateTime()));
        registry.addFormatterForFieldType(LocalDate.class,
                new SmartMvcTemporalFormatter<LocalDate>(LocalDate.class,
                        properties.getDateTime()));
        registry.addFormatterForFieldType(LocalTime.class,
                new SmartMvcTemporalFormatter<LocalTime>(LocalTime.class,
                        properties.getDateTime()));
        registry.addFormatterForFieldType(OffsetDateTime.class,
                new SmartMvcTemporalFormatter<OffsetDateTime>(OffsetDateTime.class,
                        properties.getDateTime()));
        registry.addFormatterForFieldType(ZonedDateTime.class,
                new SmartMvcTemporalFormatter<ZonedDateTime>(ZonedDateTime.class,
                        properties.getDateTime()));
        registry.addFormatter(new SmartMvcDateFormatter(properties.getDateTime()));
    }

    @Override
    public Validator getValidator() {
        return properties.getValidation().isEnabled() ? null : new NoOpValidator();
    }
}
