package ink.icoding.mvc.auth;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Legacy compatibility alias for the SmartMVC authentication extension point.
 *
 * <p>Existing applications can keep their provider implementation while receiving the default
 * token, authorization, and current-context behavior inherited from {@link AuthInterceptor}.
 * New integrations should implement {@code AuthInterceptor} directly.</p>
 *
 * @param <T> application user model type
 * @deprecated implement {@link AuthInterceptor} instead
 */
@Deprecated
public interface AuthProvider<T> extends AuthInterceptor<T> {

    /**
     * @param token token extracted using SmartMVC properties; it may be null
     * @param request current servlet request, also allowing cookie/session authentication
     * @return an authenticated identity, or null when authentication fails
     */
    AuthPrincipal<T> authenticate(String token, HttpServletRequest request);
}
