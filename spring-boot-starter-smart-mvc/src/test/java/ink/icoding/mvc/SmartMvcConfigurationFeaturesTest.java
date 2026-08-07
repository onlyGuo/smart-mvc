package ink.icoding.mvc;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.icoding.mvc.advice.ApiResponseBodyAdvice;
import ink.icoding.mvc.advice.SmartMvcExceptionHandler;
import ink.icoding.mvc.auth.CurrentAuth;
import ink.icoding.mvc.auth.PermitAllAuthInterceptor;
import ink.icoding.mvc.auth.SmartMvcAuthInterceptor;
import ink.icoding.mvc.autoconfigure.SmartMvcJacksonCustomizer;
import ink.icoding.mvc.autoconfigure.SmartMvcProperties;
import ink.icoding.mvc.autoconfigure.SmartMvcWebMvcConfigurer;
import ink.icoding.mvc.entitys.ApiResponse;
import ink.icoding.mvc.entitys.ExceptionStatusMode;
import ink.icoding.mvc.entitys.IncompleteDateTimePolicy;
import ink.icoding.mvc.exceptions.ResourceNotFoundException;
import ink.icoding.mvc.validation.NoOpValidator;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests behavior controlled by SmartMVC response, exception, date and validation settings.
 */
class SmartMvcConfigurationFeaturesTest {

    @Test
    void serializesLongAndDateTimeUsingConfiguredFormats() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        properties.getDateTime().setResponseFormat("yyyy/MM/dd HH:mm");
        ObjectMapper mapper = objectMapper(properties);
        JsonPayload payload = new JsonPayload();
        payload.setId(9007199254740993L);
        payload.setTime(LocalDateTime.of(2026, 8, 6, 14, 30));

        String json = mapper.writeValueAsString(payload);

        assertTrue(json.contains("\"id\":\"9007199254740993\""));
        assertTrue(json.contains("\"time\":\"2026/08/06 14:30\""));
    }

    @Test
    void fillsOrRejectsIncompleteDateTimeAccordingToPolicy() throws Exception {
        SmartMvcProperties fillProperties = new SmartMvcProperties();
        JsonPayload filled = objectMapper(fillProperties).readValue(
                "{\"time\":\"2026-08-06\"}", JsonPayload.class);
        assertEquals(LocalDateTime.of(2026, 8, 6, 0, 0), filled.getTime());

        SmartMvcProperties rejectProperties = new SmartMvcProperties();
        rejectProperties.getDateTime().setIncompleteInputPolicy(IncompleteDateTimePolicy.REJECT);
        assertThrows(JsonMappingException.class, () -> objectMapper(rejectProperties).readValue(
                "{\"time\":\"2026-08-06\"}", JsonPayload.class));
    }

    @Test
    void handlesInstantAndOtherJavaTimeTypesWithConfiguredZone() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        properties.getDateTime().setZoneId("Asia/Shanghai");
        ObjectMapper mapper = objectMapper(properties);
        Instant instant = Instant.parse("2026-08-06T06:30:00Z");
        TemporalPayload payload = new TemporalPayload();
        payload.setInstant(instant);
        payload.setDate(LocalDate.of(2026, 8, 6));
        payload.setTime(LocalTime.of(14, 30, 5));
        payload.setOffsetDateTime(instant.atOffset(ZoneOffset.UTC));
        payload.setZonedDateTime(instant.atZone(ZoneId.of("UTC")));

        String json = mapper.writeValueAsString(payload);

        assertTrue(json.contains("\"instant\":\"2026-08-06 14:30:00\""));
        assertTrue(json.contains("\"date\":\"2026-08-06\""));
        assertTrue(json.contains("\"time\":\"14:30:05\""));
        assertTrue(json.contains("\"offsetDateTime\":\"2026-08-06 14:30:00\""));
        assertTrue(json.contains("\"zonedDateTime\":\"2026-08-06 14:30:00\""));

        TemporalPayload parsed = mapper.readValue("{"
                + "\"instant\":\"2026-08-06 14:30:00\","
                + "\"date\":\"2026-08\","
                + "\"time\":\"14:30\","
                + "\"offsetDateTime\":\"2026-08-06 14:30:00\","
                + "\"zonedDateTime\":\"2026-08-06 14:30:00\"}",
                TemporalPayload.class);

        assertEquals(instant, parsed.getInstant());
        assertEquals(LocalDate.of(2026, 8, 1), parsed.getDate());
        assertEquals(LocalTime.of(14, 30), parsed.getTime());
        assertEquals(ZoneOffset.ofHours(8), parsed.getOffsetDateTime().getOffset());
        assertEquals(ZoneId.of("Asia/Shanghai"), parsed.getZonedDateTime().getZone());

        TemporalPayload isoParsed = mapper.readValue(
                "{\"instant\":\"2026-08-06T06:30:00Z\"}", TemporalPayload.class);
        assertEquals(instant, isoParsed.getInstant());

        SmartMvcProperties offsetProperties = new SmartMvcProperties();
        offsetProperties.getDateTime().setRequestFormat("yyyy-MM-dd HH:mm:ssXXX");
        TemporalPayload offsetParsed = objectMapper(offsetProperties).readValue(
                "{\"instant\":\"2026-08-06 06:30:00Z\","
                        + "\"offsetDateTime\":\"2026-08-06 06:30:00Z\"}",
                TemporalPayload.class);
        assertEquals(instant, offsetParsed.getInstant());
        assertEquals(ZoneOffset.UTC, offsetParsed.getOffsetDateTime().getOffset());
    }

    @Test
    void leavesLongNumericWhenStringConversionIsDisabled() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        properties.getResponse().setLongAsString(false);
        JsonPayload payload = new JsonPayload();
        payload.setId(42L);

        assertTrue(objectMapper(properties).writeValueAsString(payload).contains("\"id\":42"));
    }

    @Test
    void appliesSuccessMessageAndVoidWrappingSettings() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        properties.getResponse().setSuccessMessage("completed");
        ApiResponseBodyAdvice advice = new ApiResponseBodyAdvice(properties, objectMapper(properties));
        MethodParameter objectResult = returnType("objectResult");
        MethodParameter voidResult = returnType("voidResult");

        assertTrue(advice.supports(objectResult, MappingJackson2HttpMessageConverter.class));
        ApiResponse<?> response = (ApiResponse<?>) advice.beforeBodyWrite(
                "value", objectResult, null, MappingJackson2HttpMessageConverter.class, null, null);
        assertEquals("completed", response.getMessage());
        assertEquals("value", response.getData());
        assertTrue(advice.supports(voidResult, MappingJackson2HttpMessageConverter.class));

        properties.getResponse().setWrapVoid(false);
        assertFalse(advice.supports(voidResult, MappingJackson2HttpMessageConverter.class));
    }

    @Test
    void serializesWrappedStringForStringMessageConverter() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        ApiResponseBodyAdvice advice = new ApiResponseBodyAdvice(properties, objectMapper(properties));
        MethodParameter returnType = returnType("stringResult");
        TestServerHttpResponse response = new TestServerHttpResponse();

        Object result = advice.beforeBodyWrite("text", returnType, null,
                StringHttpMessageConverter.class, null, response);

        assertTrue(result instanceof String);
        assertTrue(((String) result).contains("\"data\":\"text\""));
        assertEquals("application/json", response.getHeaders().getContentType().toString());
    }

    @Test
    void switchesBetweenRealAndAlwaysOkExceptionStatuses() {
        SmartMvcProperties properties = new SmartMvcProperties();
        SmartMvcExceptionHandler handler = new SmartMvcExceptionHandler(properties);

        ResponseEntity<ApiResponse<Object>> realStatus = handler.handleSmartMvcException(
                new ResourceNotFoundException("missing"));
        assertEquals(404, realStatus.getStatusCode().value());

        properties.getException().setStatusMode(ExceptionStatusMode.ALWAYS_OK);
        ResponseEntity<ApiResponse<Object>> alwaysOk = handler.handleSmartMvcException(
                new ResourceNotFoundException("missing"));
        assertEquals(200, alwaysOk.getStatusCode().value());
        assertEquals("RESOURCE_NOT_FOUND", alwaysOk.getBody().getCode());
    }

    @Test
    void replacesMvcValidatorOnlyWhenValidationIsDisabled() {
        SmartMvcProperties properties = new SmartMvcProperties();
        SmartMvcWebMvcConfigurer configurer = new SmartMvcWebMvcConfigurer(
                new SmartMvcAuthInterceptor(new PermitAllAuthInterceptor(),
                        new CurrentAuth(), properties), properties);
        assertNull(configurer.getValidator());

        properties.getValidation().setEnabled(false);
        assertInstanceOf(NoOpValidator.class, configurer.getValidator());
    }

    @Test
    void convertsJavaTimeQueryAndPathParameters() {
        SmartMvcProperties properties = new SmartMvcProperties();
        properties.getDateTime().setZoneId("Asia/Shanghai");
        SmartMvcWebMvcConfigurer configurer = new SmartMvcWebMvcConfigurer(
                new SmartMvcAuthInterceptor(new PermitAllAuthInterceptor(),
                        new CurrentAuth(), properties), properties);
        DefaultFormattingConversionService conversionService =
                new DefaultFormattingConversionService();
        configurer.addFormatters(conversionService);

        assertEquals(Instant.parse("2026-08-06T06:30:00Z"), conversionService.convert(
                "2026-08-06 14:30:00", Instant.class));
        assertEquals(LocalDate.of(2026, 8, 6), conversionService.convert(
                "2026-08-06", LocalDate.class));
        assertEquals(LocalTime.of(14, 30), conversionService.convert(
                "14:30", LocalTime.class));
    }

    private ObjectMapper objectMapper(SmartMvcProperties properties) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        new SmartMvcJacksonCustomizer(properties).customize(builder);
        return builder.build();
    }

    private MethodParameter returnType(String methodName) throws NoSuchMethodException {
        return new MethodParameter(ResponseMethods.class.getDeclaredMethod(methodName), -1);
    }

    /**
     * Controller return-type fixture for response advice tests.
     */
    private static class ResponseMethods {
        Object objectResult() {
            return null;
        }

        void voidResult() {
        }

        String stringResult() {
            return null;
        }
    }

    /**
     * JSON fixture containing values affected by SmartMVC Jackson configuration.
     */
    public static class JsonPayload {
        private Long id;
        private LocalDateTime time;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }

    /**
     * JSON fixture covering the Java Time types configured by SmartMVC.
     */
    public static class TemporalPayload {
        private Instant instant;
        private LocalDate date;
        private LocalTime time;
        private OffsetDateTime offsetDateTime;
        private ZonedDateTime zonedDateTime;

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getTime() {
            return time;
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }

        public OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
            this.offsetDateTime = offsetDateTime;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }

        public void setZonedDateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }
    }

    /**
     * Minimal server response fixture used to inspect response headers without a mock agent.
     */
    private static class TestServerHttpResponse implements ServerHttpResponse {
        private final HttpHeaders headers = new HttpHeaders();
        private final ByteArrayOutputStream body = new ByteArrayOutputStream();

        @Override
        public void setStatusCode(HttpStatusCode status) {
        }

        @Override
        public void flush() throws IOException {
            body.flush();
        }

        @Override
        public void close() {
        }

        @Override
        public OutputStream getBody() {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}
