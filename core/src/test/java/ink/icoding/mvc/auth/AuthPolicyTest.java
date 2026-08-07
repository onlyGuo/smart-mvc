package ink.icoding.mvc.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the framework-neutral authorization policy and request context.
 */
class AuthPolicyTest {

    @AfterEach
    void clearContext() {
        AuthContext.clear();
    }

    @Test
    void evaluatesRolesAndPermissionsWithoutFrameworkDependencies() throws Exception {
        Method method = SecuredMethods.class.getDeclaredMethod("allRequired");
        Auth requirement = method.getAnnotation(Auth.class);
        AuthPrincipal<String> allowed = new AuthPrincipal<String>("1", "user-object",
                new HashSet<String>(Arrays.asList("admin")),
                new HashSet<String>(Arrays.asList("order:read")), null);
        AuthPrincipal<Object> denied = new AuthPrincipal<Object>("2",
                new HashSet<String>(Arrays.asList("user")),
                new HashSet<String>(Arrays.asList("order:read")), null);

        assertTrue(AuthPolicy.isAllowed(allowed, requirement));
        assertFalse(AuthPolicy.isAllowed(denied, requirement));
        AuthContext.set(allowed);
        assertSame(allowed, AuthContext.require());
        assertEquals("user-object", allowed.getUser());
    }

    @Test
    void matchesHttpMethodAndAntStyleRequestPermissions() {
        Set<String> permissions = new HashSet<String>(Arrays.asList(
                "GET:/api/users/**", "POST:/api/*/profile"));

        assertTrue(AuthPermissionMatcher.matches(permissions, "GET", "/api/users/42"));
        assertTrue(AuthPermissionMatcher.matches(permissions, "GET", "/api/users"));
        assertTrue(AuthPermissionMatcher.matches(permissions, "POST", "/api/42/profile"));
        assertFalse(AuthPermissionMatcher.matches(permissions, "DELETE", "/api/users/42"));
        assertTrue(AuthPermissionMatcher.matches("*:*", "PATCH", "/anything"));
        assertTrue(AuthPermissionMatcher.matches("GET：api/users/**", "GET", "/api/users/42"));
    }

    @Test
    void wildcardRoleAndPermissionSatisfyAnnotationRequirements() throws Exception {
        Auth requirement = SecuredMethods.class.getDeclaredMethod("allRequired")
                .getAnnotation(Auth.class);
        AuthPrincipal<Object> wildcard = new AuthPrincipal<Object>("system",
                Collections.singleton("*"), Collections.singleton("*:*"), null);

        assertTrue(AuthPolicy.isAllowed(wildcard, requirement));
    }

    /**
     * Supplies annotated methods used by authorization policy tests.
     */
    private static class SecuredMethods {
        @Auth(roles = "admin", permissions = "order:read")
        void allRequired() {
        }
    }
}
