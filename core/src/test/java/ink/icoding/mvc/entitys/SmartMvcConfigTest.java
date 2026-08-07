package ink.icoding.mvc.entitys;

import ink.icoding.mvc.auth.AuthenticationMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the framework-neutral SmartMVC configuration defaults.
 */
class SmartMvcConfigTest {

    @Test
    void providesDocumentedDefaults() {
        SmartMvcConfig config = new SmartMvcConfig();

        assertTrue(config.getException().isEnabled());
        assertEquals(ExceptionStatusMode.HTTP_STATUS, config.getException().getStatusMode());
        assertTrue(config.getResponse().isWrapEnabled());
        assertTrue(config.getResponse().isWrapVoid());
        assertTrue(config.getResponse().isLongAsString());
        assertEquals("success", config.getResponse().getSuccessMessage());
        assertEquals("yyyy-MM-dd HH:mm:ss", config.getDateTime().getRequestFormat());
        assertEquals("yyyy-MM-dd HH:mm:ss", config.getDateTime().getResponseFormat());
        assertEquals("yyyy-MM-dd", config.getDateTime().getDateRequestFormat());
        assertEquals("yyyy-MM-dd", config.getDateTime().getDateResponseFormat());
        assertEquals("HH:mm:ss", config.getDateTime().getTimeRequestFormat());
        assertEquals("HH:mm:ss", config.getDateTime().getTimeResponseFormat());
        assertEquals("system-default", config.getDateTime().getZoneId());
        assertEquals(IncompleteDateTimePolicy.FILL_MISSING,
                config.getDateTime().getIncompleteInputPolicy());
        assertTrue(config.getValidation().isEnabled());
        assertTrue(config.getRequestLog().isEnabled());
        assertEquals(RequestLogLevel.INFO, config.getRequestLog().getLevel());
        assertTrue(config.getAuth().isEnabled());
        assertEquals(AuthenticationMode.ANNOTATED, config.getAuth().getMode());
        assertFalse(config.getAuth().isCheckRequestPermission());
    }
}
