package ink.icoding.mvc;

import ink.icoding.mvc.auth.AuthPrincipal;
import ink.icoding.mvc.auth.CurrentAuth;
import ink.icoding.mvc.exceptions.ResourceNotFoundException;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrates the response, temporal conversion, validation, and exception features of SmartMVC.
 *
 * <p>The endpoints intentionally return representative controller values and raise a known
 * application exception so the example application can be used for manual HTTP verification
 * of the starter's auto-configured behavior.</p>
 */
@RestController
@RequestMapping("/example")
public class SmartMvcExampleController {

    @Resource
    private CurrentAuth currentAuth;

    @GetMapping("/response")
    public ExamplePayload response() {
        return new ExamplePayload(9007199254740993L,
                LocalDateTime.of(2026, 8, 6, 14, 30, 0));
    }

    @GetMapping("/time")
    public LocalDateTime time(@RequestParam LocalDateTime value) {
        return value;
    }

    @GetMapping("/instant")
    public Instant instant(@RequestParam Instant value) {
        return value;
    }

    @GetMapping("/void")
    public void voidResponse() {
    }

    @GetMapping("/missing")
    public void missing() {
        throw new ResourceNotFoundException("example resource is missing");
    }

    @GetMapping("/validate")
    public long validate(@RequestParam @Min(1) long value) {
        return value;
    }

    @GetMapping("login-validate")
    public AuthPrincipal<?> loginValidate() {
        return currentAuth.getPrincipal();
    }

    /**
     * Immutable example payload containing long-integer, legacy date, instant, and local
     * date-time values that exercise SmartMVC's configured Jackson serializers.
     */
    public static class ExamplePayload {
        private final Long id;
        private final LocalDateTime createdAt;
        private final Date date;
        private final Instant instant;

        public ExamplePayload(Long id, LocalDateTime createdAt) {
            this.id = id;
            this.createdAt = createdAt;
            this.date = new Date();
            this.instant = Instant.now();
        }

        public Long getId() {
            return id;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public Date getDate() {
            return date;
        }

        public Instant getInstant() {
            return instant;
        }
    }
}
