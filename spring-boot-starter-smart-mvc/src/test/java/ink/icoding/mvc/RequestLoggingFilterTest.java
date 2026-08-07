package ink.icoding.mvc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ink.icoding.mvc.entitys.RequestLogConfig;
import ink.icoding.mvc.entitys.RequestLogLevel;
import ink.icoding.mvc.logging.RequestLoggingFilter;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that request summaries use the matched controller category and configured severity.
 */
class RequestLoggingFilterTest {

    @Test
    void logsWithControllerClassAndConfiguredLevel() throws Exception {
        Logger controllerLogger = (Logger) LoggerFactory.getLogger(LoggingController.class);
        Level previousLevel = controllerLogger.getLevel();
        ListAppender<ILoggingEvent> appender = new ListAppender<ILoggingEvent>();
        appender.start();
        controllerLogger.setLevel(Level.TRACE);
        controllerLogger.addAppender(appender);
        try {
            RequestLogConfig config = new RequestLogConfig();
            config.setLevel(RequestLogLevel.WARN);
            RequestLoggingFilter filter = new RequestLoggingFilter(config);
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/example");
            request.setQueryString("page=1");
            request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                    new HandlerMethod(new LoggingController(),
                            LoggingController.class.getMethod("endpoint")));
            MockHttpServletResponse response = new MockHttpServletResponse();
            response.setStatus(201);

            filter.doFilter(request, response, new MockFilterChain());

            assertEquals(1, appender.list.size());
            ILoggingEvent event = appender.list.get(0);
            assertEquals(LoggingController.class.getName(), event.getLoggerName());
            assertEquals(Level.WARN, event.getLevel());
            assertTrue(event.getFormattedMessage()
                    .startsWith("HTTP GET /api/example?page=1 -> 201 ("));
        } finally {
            controllerLogger.detachAppender(appender);
            controllerLogger.setLevel(previousLevel);
            appender.stop();
        }
    }

    /**
     * Controller fixture used as the expected SLF4J logger category.
     */
    public static class LoggingController {
        public void endpoint() {
        }
    }
}
