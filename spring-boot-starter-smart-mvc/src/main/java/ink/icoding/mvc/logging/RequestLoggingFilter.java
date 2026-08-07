package ink.icoding.mvc.logging;

import ink.icoding.mvc.entitys.RequestLogConfig;
import ink.icoding.mvc.entitys.RequestLogLevel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Records a concise execution summary for each HTTP request.
 *
 * <p>The once-per-request filter captures the method and URI before delegation, then logs the
 * final response status and elapsed time even when downstream processing raises an exception.</p>
 */
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String FALLBACK_LOGGER_NAME = "ink.icoding.mvc.request";

    private final RequestLogConfig config;

    public RequestLoggingFilter(RequestLogConfig config) {
        this.config = config;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        long startedAt = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
            writeLog(resolveLogger(request), config.getLevel(), request.getMethod(),
                    requestUri(request), response.getStatus(), elapsedMillis);
        }
    }

    private Logger resolveLogger(HttpServletRequest request) {
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handler instanceof HandlerMethod) {
            return LoggerFactory.getLogger(((HandlerMethod) handler).getBeanType());
        }
        if (handler != null) {
            return LoggerFactory.getLogger(handler.getClass());
        }
        return LoggerFactory.getLogger(FALLBACK_LOGGER_NAME);
    }

    private void writeLog(Logger logger, RequestLogLevel level, String method, String uri,
                          int status, long elapsedMillis) {
        switch (level) {
            case TRACE:
                logger.trace("HTTP {} {} -> {} ({} ms)", method, uri, status, elapsedMillis);
                break;
            case DEBUG:
                logger.debug("HTTP {} {} -> {} ({} ms)", method, uri, status, elapsedMillis);
                break;
            case WARN:
                logger.warn("HTTP {} {} -> {} ({} ms)", method, uri, status, elapsedMillis);
                break;
            case ERROR:
                logger.error("HTTP {} {} -> {} ({} ms)", method, uri, status, elapsedMillis);
                break;
            case INFO:
            default:
                logger.info("HTTP {} {} -> {} ({} ms)", method, uri, status, elapsedMillis);
                break;
        }
    }

    private String requestUri(HttpServletRequest request) {
        String query = request.getQueryString();
        return query == null || query.isEmpty()
                ? request.getRequestURI()
                : request.getRequestURI() + "?" + query;
    }
}
