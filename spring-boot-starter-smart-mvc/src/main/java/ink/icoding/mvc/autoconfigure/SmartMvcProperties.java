package ink.icoding.mvc.autoconfigure;

import ink.icoding.mvc.entitys.AuthConfig;
import ink.icoding.mvc.entitys.DateTimeConfig;
import ink.icoding.mvc.entitys.ExceptionConfig;
import ink.icoding.mvc.entitys.RequestLogConfig;
import ink.icoding.mvc.entitys.ResponseConfig;
import ink.icoding.mvc.entitys.SmartMvcConfig;
import ink.icoding.mvc.entitys.ValidationConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Binds the {@code spring.smart.mvc} configuration namespace to the SmartMVC runtime model.
 *
 * <p>The class reuses the framework-neutral configuration objects from the core module and
 * marks each group as nested so Spring Boot can generate metadata and provide IDE completion
 * without moving Spring dependencies into the core artifact.</p>
 */
@ConfigurationProperties(prefix = "spring.smart.mvc")
public class SmartMvcProperties extends SmartMvcConfig {

    @Override
    @NestedConfigurationProperty
    public ExceptionConfig getException() {
        return super.getException();
    }

    @Override
    @NestedConfigurationProperty
    public ResponseConfig getResponse() {
        return super.getResponse();
    }

    @Override
    @NestedConfigurationProperty
    public DateTimeConfig getDateTime() {
        return super.getDateTime();
    }

    @Override
    @NestedConfigurationProperty
    public ValidationConfig getValidation() {
        return super.getValidation();
    }

    @Override
    @NestedConfigurationProperty
    public RequestLogConfig getRequestLog() {
        return super.getRequestLog();
    }

    @Override
    @NestedConfigurationProperty
    public AuthConfig getAuth() {
        return super.getAuth();
    }
}
