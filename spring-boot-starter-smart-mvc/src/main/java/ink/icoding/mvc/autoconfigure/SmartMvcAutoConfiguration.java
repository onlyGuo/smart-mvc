package ink.icoding.mvc.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import ink.icoding.mvc.advice.ApiResponseBodyAdvice;
import ink.icoding.mvc.advice.SmartMvcExceptionHandler;
import ink.icoding.mvc.auth.AuthInterceptor;
import ink.icoding.mvc.auth.CurrentAuth;
import ink.icoding.mvc.auth.PermitAllAuthInterceptor;
import ink.icoding.mvc.auth.SmartMvcAuthInterceptor;
import ink.icoding.mvc.logging.RequestLoggingFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configures SmartMVC components for a Servlet-based Spring Boot web application.
 *
 * <p>The configuration registers authentication and current-context infrastructure, response
 * and exception advice, Jackson customization, MVC formatters, request logging, and validation
 * behavior. Conditional beans allow applications to replace security extensions or disable
 * individual features through the {@code spring.smart.mvc} namespace.</p>
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SmartMvcProperties.class)
public class SmartMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuthInterceptor.class)
    public AuthInterceptor<Object> smartMvcPermitAllAuthInterceptor() {
        return new PermitAllAuthInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public CurrentAuth currentAuth() {
        return new CurrentAuth();
    }

    @Bean
    @ConditionalOnMissingBean
    public SmartMvcAuthInterceptor smartMvcAuthInterceptor(AuthInterceptor<?> authInterceptor,
                                                           CurrentAuth currentAuth,
                                                           SmartMvcProperties properties) {
        return new SmartMvcAuthInterceptor(authInterceptor, currentAuth, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.smart.mvc.response", name = "wrap-enabled",
            havingValue = "true", matchIfMissing = true)
    public ApiResponseBodyAdvice smartMvcResponseBodyAdvice(SmartMvcProperties properties,
                                                            ObjectProvider<ObjectMapper> objectMapper) {
        return new ApiResponseBodyAdvice(properties,
                objectMapper.getIfAvailable(ObjectMapper::new));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.smart.mvc.exception", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public SmartMvcExceptionHandler smartMvcExceptionHandler(SmartMvcProperties properties) {
        return new SmartMvcExceptionHandler(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SmartMvcJacksonCustomizer smartMvcJacksonCustomizer(SmartMvcProperties properties) {
        return new SmartMvcJacksonCustomizer(properties);
    }

    @Bean
    public SmartMvcWebMvcConfigurer smartMvcWebMvcConfigurer(SmartMvcAuthInterceptor interceptor,
                                                             SmartMvcProperties properties) {
        return new SmartMvcWebMvcConfigurer(interceptor, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.smart.mvc.request-log", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<RequestLoggingFilter> smartMvcRequestLoggingFilter(
            SmartMvcProperties properties) {
        FilterRegistrationBean<RequestLoggingFilter> registration =
                new FilterRegistrationBean<RequestLoggingFilter>();
        registration.setFilter(new RequestLoggingFilter(properties.getRequestLog()));
        registration.setName("smartMvcRequestLoggingFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        return registration;
    }
}
