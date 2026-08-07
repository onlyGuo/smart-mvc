package ink.icoding.mvc.auth;

import java.util.Collections;
import java.util.Set;

/**
 * Spring-managed facade for accessing the authenticated identity of the current request.
 *
 * <p>The facade is a singleton bean backed by a request-thread context, so it can be injected
 * safely into controllers, services, and other singleton Spring beans. It stores no user state
 * in the bean itself; every lookup delegates to the thread-isolated core context. The facade is
 * intended for synchronous Servlet request processing and does not automatically propagate
 * identity into application-created asynchronous threads.</p>
 */
public class CurrentAuth {

    public boolean isAuthenticated() {
        return AuthContext.get() != null;
    }

    public AuthPrincipal<?> getPrincipal() {
        return AuthContext.get();
    }

    public AuthPrincipal<?> requirePrincipal() {
        return AuthContext.require();
    }

    public String getUserId() {
        AuthPrincipal<?> principal = getPrincipal();
        return principal == null ? null : principal.getId();
    }

    public Object getUser() {
        AuthPrincipal<?> principal = getPrincipal();
        return principal == null ? null : principal.getUser();
    }

    public <T> T getUser(Class<T> userType) {
        Object user = getUser();
        return user == null ? null : userType.cast(user);
    }

    public Set<String> getRoles() {
        AuthPrincipal<?> principal = getPrincipal();
        return principal == null ? Collections.<String>emptySet() : principal.getRoles();
    }

    public Set<String> getPermissions() {
        AuthPrincipal<?> principal = getPrincipal();
        return principal == null ? Collections.<String>emptySet() : principal.getPermissions();
    }

    public boolean hasRole(String role) {
        return getRoles().contains("*") || getRoles().contains(role);
    }

    public boolean hasPermission(String method, String path) {
        return AuthPermissionMatcher.matches(getPermissions(), method, path);
    }

    public void setPrincipal(AuthPrincipal<?> principal) {
        AuthContext.set(principal);
    }

    public void clear() {
        AuthContext.clear();
    }
}
