package ink.icoding.mvc.auth;

import java.util.Set;

/**
 * Evaluates annotation-based role and permission requirements without framework dependencies.
 *
 * <p>The policy supports both all-value and any-value matching through {@link AuthMode}.
 * Wildcard identities containing {@code *} or {@code *:*} can satisfy arbitrary declared
 * requirements, which is used by the default permit-all integration.</p>
 */
public final class AuthPolicy {

    private AuthPolicy() {
    }

    public static boolean isAllowed(AuthPrincipal<?> principal, Auth requirement) {
        if (principal == null || requirement == null) {
            return false;
        }
        return matches(principal.getRoles(), requirement.roles(), requirement.mode())
                && matches(principal.getPermissions(), requirement.permissions(), requirement.mode());
    }

    private static boolean matches(Set<String> actual, String[] required, AuthMode mode) {
        if (required == null || required.length == 0) {
            return true;
        }
        if (actual.contains("*") || actual.contains("*:*")) {
            return true;
        }
        if (mode == AuthMode.ANY) {
            for (String value : required) {
                if (actual.contains(value)) {
                    return true;
                }
            }
            return false;
        }
        for (String value : required) {
            if (!actual.contains(value)) {
                return false;
            }
        }
        return true;
    }
}
