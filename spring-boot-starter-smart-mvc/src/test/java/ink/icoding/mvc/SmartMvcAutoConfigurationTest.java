package ink.icoding.mvc;

import ink.icoding.mvc.advice.ApiResponseBodyAdvice;
import ink.icoding.mvc.advice.SmartMvcExceptionHandler;
import ink.icoding.mvc.auth.AuthInterceptor;
import ink.icoding.mvc.auth.AuthPrincipal;
import ink.icoding.mvc.auth.AuthenticationMode;
import ink.icoding.mvc.auth.CurrentAuth;
import ink.icoding.mvc.auth.PermitAllAuthInterceptor;
import ink.icoding.mvc.auth.SmartMvcAuthInterceptor;
import ink.icoding.mvc.autoconfigure.SmartMvcAutoConfiguration;
import ink.icoding.mvc.autoconfigure.SmartMvcJacksonCustomizer;
import ink.icoding.mvc.autoconfigure.SmartMvcProperties;
import ink.icoding.mvc.autoconfigure.SmartMvcWebMvcConfigurer;
import ink.icoding.mvc.entitys.ExceptionStatusMode;
import ink.icoding.mvc.entitys.IncompleteDateTimePolicy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the beans contributed by SmartMVC servlet auto-configuration.
 */
class SmartMvcAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SmartMvcAutoConfiguration.class));

    @Test
    void contributesMvcEnhancementsForServletApplications() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SmartMvcProperties.class);
            assertThat(context).hasSingleBean(AuthInterceptor.class);
            assertThat(context).hasSingleBean(PermitAllAuthInterceptor.class);
            assertThat(context).hasSingleBean(SmartMvcAuthInterceptor.class);
            assertThat(context).hasSingleBean(CurrentAuth.class);
            assertThat(context).hasSingleBean(ApiResponseBodyAdvice.class);
            assertThat(context).hasSingleBean(SmartMvcExceptionHandler.class);
            assertThat(context).hasSingleBean(SmartMvcJacksonCustomizer.class);
            assertThat(context).hasSingleBean(SmartMvcWebMvcConfigurer.class);
            assertThat(context).hasBean("smartMvcRequestLoggingFilter");
        });
    }

    @Test
    void bindsEveryConfigurationGroup() {
        contextRunner.withPropertyValues(
                "spring.smart.mvc.exception.enabled=false",
                "spring.smart.mvc.exception.status-mode=always-ok",
                "spring.smart.mvc.response.wrap-enabled=false",
                "spring.smart.mvc.response.wrap-void=false",
                "spring.smart.mvc.response.success-message=done",
                "spring.smart.mvc.response.long-as-string=false",
                "spring.smart.mvc.date-time.request-format=yyyy/MM/dd HH:mm",
                "spring.smart.mvc.date-time.response-format=yyyyMMddHHmmss",
                "spring.smart.mvc.date-time.date-request-format=yyyy/MM/dd",
                "spring.smart.mvc.date-time.date-response-format=yyyyMMdd",
                "spring.smart.mvc.date-time.time-request-format=HH-mm-ss",
                "spring.smart.mvc.date-time.time-response-format=HHmmss",
                "spring.smart.mvc.date-time.zone-id=Asia/Shanghai",
                "spring.smart.mvc.date-time.incomplete-input-policy=reject",
                "spring.smart.mvc.validation.enabled=false",
                "spring.smart.mvc.request-log.enabled=false",
                "spring.smart.mvc.request-log.level=warn",
                "spring.smart.mvc.auth.enabled=false",
                "spring.smart.mvc.auth.mode=global",
                "spring.smart.mvc.auth.check-request-permission=true",
                "spring.smart.mvc.auth.authorization-header=X-Token",
                "spring.smart.mvc.auth.token-prefix=Token",
                "spring.smart.mvc.auth.exclude-paths[0]=/health"
        ).run(context -> {
            SmartMvcProperties properties = context.getBean(SmartMvcProperties.class);
            assertThat(properties.getException().isEnabled()).isFalse();
            assertThat(properties.getException().getStatusMode())
                    .isEqualTo(ExceptionStatusMode.ALWAYS_OK);
            assertThat(properties.getResponse().isWrapEnabled()).isFalse();
            assertThat(properties.getResponse().isWrapVoid()).isFalse();
            assertThat(properties.getResponse().getSuccessMessage()).isEqualTo("done");
            assertThat(properties.getResponse().isLongAsString()).isFalse();
            assertThat(properties.getDateTime().getRequestFormat()).isEqualTo("yyyy/MM/dd HH:mm");
            assertThat(properties.getDateTime().getResponseFormat()).isEqualTo("yyyyMMddHHmmss");
            assertThat(properties.getDateTime().getDateRequestFormat()).isEqualTo("yyyy/MM/dd");
            assertThat(properties.getDateTime().getDateResponseFormat()).isEqualTo("yyyyMMdd");
            assertThat(properties.getDateTime().getTimeRequestFormat()).isEqualTo("HH-mm-ss");
            assertThat(properties.getDateTime().getTimeResponseFormat()).isEqualTo("HHmmss");
            assertThat(properties.getDateTime().getZoneId()).isEqualTo("Asia/Shanghai");
            assertThat(properties.getDateTime().getIncompleteInputPolicy())
                    .isEqualTo(IncompleteDateTimePolicy.REJECT);
            assertThat(properties.getValidation().isEnabled()).isFalse();
            assertThat(properties.getRequestLog().isEnabled()).isFalse();
            assertThat(properties.getRequestLog().getLevel())
                    .isEqualTo(ink.icoding.mvc.entitys.RequestLogLevel.WARN);
            assertThat(properties.getAuth().isEnabled()).isFalse();
            assertThat(properties.getAuth().getMode()).isEqualTo(AuthenticationMode.GLOBAL);
            assertThat(properties.getAuth().isCheckRequestPermission()).isTrue();
            assertThat(properties.getAuth().getAuthorizationHeader()).isEqualTo("X-Token");
            assertThat(properties.getAuth().getTokenPrefix()).isEqualTo("Token");
            assertThat(properties.getAuth().getExcludePaths()).containsExactly("/health");
            assertThat(context).doesNotHaveBean(ApiResponseBodyAdvice.class);
            assertThat(context).doesNotHaveBean(SmartMvcExceptionHandler.class);
            assertThat(context).doesNotHaveBean("smartMvcRequestLoggingFilter");
        });
    }

    @Test
    void applicationAuthInterceptorReplacesPermitAllDefault() {
        AuthInterceptor<Object> custom = (token, request) ->
                new AuthPrincipal<Object>("custom-user");

        contextRunner.withBean("customAuthInterceptor", AuthInterceptor.class, () -> custom)
                .run(context -> {
                    assertThat(context).hasSingleBean(AuthInterceptor.class);
                    assertThat(context).doesNotHaveBean(PermitAllAuthInterceptor.class);
                    assertThat(context.getBean(AuthInterceptor.class)).isSameAs(custom);
                    assertThat(context).hasSingleBean(SmartMvcAuthInterceptor.class);
                });
    }
}
