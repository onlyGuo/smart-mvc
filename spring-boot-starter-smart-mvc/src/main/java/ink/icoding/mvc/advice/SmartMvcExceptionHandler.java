package ink.icoding.mvc.advice;

import ink.icoding.mvc.autoconfigure.SmartMvcProperties;
import ink.icoding.mvc.entitys.ApiResponse;
import ink.icoding.mvc.entitys.ExceptionStatusMode;
import ink.icoding.mvc.entitys.FieldViolation;
import ink.icoding.mvc.exceptions.SmartMvcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Translates SmartMVC and common Spring MVC exceptions into standardized HTTP error responses.
 *
 * <p>The handler preserves application error codes and details, converts binding failures into
 * field violations, and applies the configured real-status or always-OK transport strategy.
 * Unexpected exceptions are reduced to a safe internal-server-error response.</p>
 */
@RestControllerAdvice
@ConditionalOnProperty(prefix = "spring.smart.mvc.exception", name = "enabled",
        havingValue = "true", matchIfMissing = true)
public class SmartMvcExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMvcExceptionHandler.class);

    private final SmartMvcProperties properties;

    public SmartMvcExceptionHandler(SmartMvcProperties properties) {
        this.properties = properties;
    }

    @ExceptionHandler(SmartMvcException.class)
    public ResponseEntity<ApiResponse<Object>> handleSmartMvcException(SmartMvcException exception) {
        return failure(exception.getStatus(), exception.getCode(), exception.getMessage(),
                exception.getDetails());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Object>> handleBindingException(Exception exception) {
        List<FieldError> errors;
        if (exception instanceof MethodArgumentNotValidException) {
            errors = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors();
        } else {
            errors = ((BindException) exception).getBindingResult().getFieldErrors();
        }
        List<FieldViolation> violations = new ArrayList<FieldViolation>(errors.size());
        for (FieldError error : errors) {
            violations.add(new FieldViolation(error.getField(), error.getRejectedValue(),
                    error.getDefaultMessage()));
        }
        return failure(400, "PARAMETER_VALIDATION_FAILED", "Request validation failed", violations);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class,
            HandlerMethodValidationException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(Exception exception) {
        return failure(400, "BAD_REQUEST", exception.getMessage(), null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NoResourceFoundException exception) {
        return failure(404, "RESOURCE_NOT_FOUND", exception.getMessage(), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception) {
        return failure(405, "METHOD_NOT_ALLOWED", exception.getMessage(), null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception) {
        return failure(415, "UNSUPPORTED_MEDIA_TYPE", exception.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpectedException(Exception exception) {
        LOGGER.error("Unhandled MVC exception:{}", exception.getMessage(), exception);
        return failure(500, "INTERNAL_SERVER_ERROR", "Internal server error", null);
    }

    private ResponseEntity<ApiResponse<Object>> failure(int status, String code,
                                                        String message, Object details) {
        int responseStatus = properties.getException().getStatusMode() == ExceptionStatusMode.ALWAYS_OK
                ? 200 : status;
        return ResponseEntity.status(responseStatus).body(ApiResponse.failure(code, message, details));
    }
}
