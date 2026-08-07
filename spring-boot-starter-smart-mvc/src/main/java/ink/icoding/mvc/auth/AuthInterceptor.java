package ink.icoding.mvc.auth;

import ink.icoding.mvc.entitys.AuthConfig;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Application authentication extension point used by SmartMVC for identity and authorization.
 *
 * <p>Applications normally implement only {@link #authenticate(String, HttpServletRequest)}.
 * The default methods provide token extraction, annotation authorization, request permission
 * matching and current-session lifecycle handling. Implementations may override those methods
 * when credentials come from cookies or sessions, or when an application uses a custom
 * authorization model.</p>
 *
 * @param <T> application user model type
 */
public interface AuthInterceptor<T> {

    /**
     * Authenticates a request from its extracted token.
     *
     * @param token configured authentication token, possibly {@code null}
     * @param request current servlet request
     * @return authenticated identity, or {@code null} when authentication fails
     */
    AuthPrincipal<T> authenticate(String token, HttpServletRequest request);

    /**
     * Extracts a token using the configured header and prefix.
     */
    default String resolveToken(HttpServletRequest request, AuthConfig config) {
        String value = request.getHeader(config.getAuthorizationHeader());
        String prefix = config.getTokenPrefix();
        if (value == null || prefix == null || prefix.trim().isEmpty()) {
            return value;
        }
        String normalizedPrefix = prefix.trim();
        return value.startsWith(normalizedPrefix)
                ? value.substring(normalizedPrefix.length()).trim()
                : value;
    }

    /**
     * Authorizes the authenticated identity against annotations and the request permission.
     */
    default boolean authorize(AuthPrincipal<T> principal, Auth requirement,
                              HttpServletRequest request, AuthConfig config) {
        if (principal == null) {
            return false;
        }
        if (requirement != null && !AuthPolicy.isAllowed(principal, requirement)) {
            return false;
        }
        if (!config.isCheckRequestPermission()) {
            return true;
        }
        return AuthPermissionMatcher.matches(principal.getPermissions(), request.getMethod(),
                resolveRequestPath(request));
    }

    /**
     * Stores an authenticated identity in the request-bound current authentication object.
     */
    default void bind(AuthPrincipal<T> principal, CurrentAuth currentAuth) {
        currentAuth.setPrincipal(principal);
    }

    /**
     * Clears request authentication data after request completion.
     */
    default void clear(CurrentAuth currentAuth) {
        currentAuth.clear();
    }

    /**
     * Resolves the request path used by method-and-path permission matching.
     */
    default String resolveRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }
}
