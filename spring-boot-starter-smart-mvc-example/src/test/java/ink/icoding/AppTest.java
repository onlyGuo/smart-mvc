package ink.icoding;

import ink.icoding.mvc.SmartMvcSpringBootStarterExampleApp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies that the Example module exposes its Spring Boot application entry point to tests.
 */
class AppTest {

    @Test
    void exposesApplicationEntryPoint() {
        assertNotNull(SmartMvcSpringBootStarterExampleApp.class);
    }
}
