package ink.icoding.mvc.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests HTTP status metadata and stack-trace suppression for SmartMVC exceptions.
 */
class SmartMvcExceptionTest {

    @Test
    void expectedExceptionsDoNotCaptureStackTraces() {
        Throwable cause = new IllegalStateException("downstream");
        BusinessExecutionException exception =
                new BusinessExecutionException("execution failed", cause);

        assertEquals(500, exception.getStatus());
        assertEquals("BUSINESS_EXECUTION_FAILED", exception.getCode());
        assertEquals(0, exception.getStackTrace().length);
        assertSame(cause, exception.getCause());
    }

    @Test
    void businessRejectionsUseUnprocessableEntity() {
        assertEquals(422, new BusinessException("rejected").getStatus());
    }
}
