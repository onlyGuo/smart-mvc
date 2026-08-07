package ink.icoding.mvc.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.icoding.mvc.autoconfigure.SmartMvcProperties;
import ink.icoding.mvc.entitys.ApiResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Applies the standardized {@link ApiResponse} envelope to ordinary controller return values.
 *
 * <p>The advice respects response-wrapping configuration, avoids wrapping responses that are
 * already standardized, handles void results, and serializes wrapped strings correctly when
 * Spring selects a string message converter.</p>
 */
@ControllerAdvice
@ConditionalOnProperty(prefix = "spring.smart.mvc.response", name = "wrap-enabled",
        havingValue = "true", matchIfMissing = true)
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final SmartMvcProperties properties;
    private final ObjectMapper objectMapper;

    public ApiResponseBodyAdvice(SmartMvcProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> type = returnType.getParameterType();
        boolean voidType = Void.TYPE.equals(type) || Void.class.equals(type);
        return properties.getResponse().isWrapEnabled()
                && (!voidType || properties.getResponse().isWrapVoid())
                && !ApiResponse.class.isAssignableFrom(type)
                && !byte[].class.isAssignableFrom(type)
                && !Resource.class.isAssignableFrom(type)
                && !StreamingResponseBody.class.isAssignableFrom(type)
                && !ProblemDetail.class.isAssignableFrom(type);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  org.springframework.http.MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse || body instanceof Resource
                || body instanceof StreamingResponseBody || body instanceof ProblemDetail
                || body instanceof byte[]) {
            return body;
        }
        ApiResponse<Object> wrapped = ApiResponse.success(
                properties.getResponse().getSuccessMessage(), body);
        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                return objectMapper.writeValueAsString(wrapped);
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("Failed to serialize wrapped String response", exception);
            }
        }
        return wrapped;
    }
}
