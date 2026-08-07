package ink.icoding.mvc.auth;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable, framework-neutral representation of an authenticated identity.
 *
 * <p>An identity contains a stable identifier, an optional application-specific user object,
 * role and permission sets, and arbitrary read-only attributes. Defensive copies prevent
 * request authentication state from being modified after construction.</p>
 *
 * @param <T> application user model type
 */
public final class AuthPrincipal<T> {

    private final String id;
    private final T user;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final Map<String, Object> attributes;

    public AuthPrincipal(String id) {
        this(id, null, null, null, null);
    }

    public AuthPrincipal(String id, Set<String> roles, Set<String> permissions,
                         Map<String, Object> attributes) {
        this(id, null, roles, permissions, attributes);
    }

    public AuthPrincipal(String id, T user, Set<String> roles, Set<String> permissions,
                         Map<String, Object> attributes) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("principal id must not be blank");
        }
        this.id = id;
        this.user = user;
        this.roles = immutableSet(roles);
        this.permissions = immutableSet(permissions);
        this.attributes = attributes == null
                ? Collections.<String, Object>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, Object>(attributes));
    }

    private static Set<String> immutableSet(Set<String> source) {
        return source == null
                ? Collections.<String>emptySet()
                : Collections.unmodifiableSet(new LinkedHashSet<String>(source));
    }

    public String getId() {
        return id;
    }

    public T getUser() {
        return user;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
