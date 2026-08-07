package ink.icoding.mvc;

import ink.icoding.mvc.auth.Anonymous;
import ink.icoding.mvc.auth.Auth;
import ink.icoding.mvc.auth.AuthContext;
import ink.icoding.mvc.auth.AuthInterceptor;
import ink.icoding.mvc.auth.AuthPrincipal;
import ink.icoding.mvc.auth.AuthenticationMode;
import ink.icoding.mvc.auth.CurrentAuth;
import ink.icoding.mvc.auth.SmartMvcAuthInterceptor;
import ink.icoding.mvc.autoconfigure.SmartMvcProperties;
import ink.icoding.mvc.exceptions.ForbiddenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests authentication modes, authorization and context cleanup in the MVC interceptor.
 */
class AuthInterceptorTest {

    @AfterEach
    void clearContext() {
        AuthContext.clear();
    }

    @Test
    void authenticatesSecuredHandlerAndExposesTypedUser() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        AuthInterceptor<TestUser> strategy = (token, request) ->
                new AuthPrincipal<TestUser>("42", new TestUser("Alice"),
                        Collections.singleton("admin"),
                        Collections.singleton("order:read"), null);
        CurrentAuth currentAuth = new CurrentAuth();
        SmartMvcAuthInterceptor interceptor = interceptor(strategy, currentAuth, properties);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/orders/42");
        request.addHeader("Authorization", "Bearer sample-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = securedHandler("secured");

        assertTrue(interceptor.preHandle(request, response, handler));
        assertNotNull(AuthContext.get());
        assertEquals("42", currentAuth.getUserId());
        assertEquals("Alice", currentAuth.getUser(TestUser.class).name);
        assertTrue(currentAuth.hasRole("admin"));
        interceptor.afterCompletion(request, response, handler, null);
        assertFalse(currentAuth.isAuthenticated());
    }

    @Test
    void deniesIdentityWithoutRequiredRole() throws Exception {
        AuthInterceptor<Object> strategy = (token, request) -> new AuthPrincipal<Object>("42");
        SmartMvcAuthInterceptor interceptor = interceptor(strategy, new CurrentAuth(),
                new SmartMvcProperties());

        assertThrows(ForbiddenException.class, () -> interceptor.preHandle(
                new MockHttpServletRequest(), new MockHttpServletResponse(),
                securedHandler("secured")));
    }

    @Test
    void globalModeAuthenticatesUnannotatedHandlersAndChecksRequestPermission() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        properties.getAuth().setMode(AuthenticationMode.GLOBAL);
        properties.getAuth().setCheckRequestPermission(true);
        AuthInterceptor<Object> strategy = (token, request) -> new AuthPrincipal<Object>("42",
                Collections.<String>emptySet(),
                Collections.singleton("GET:/api/users/**"), null);
        CurrentAuth currentAuth = new CurrentAuth();
        SmartMvcAuthInterceptor interceptor = interceptor(strategy, currentAuth, properties);
        MockHttpServletRequest allowed = new MockHttpServletRequest("GET", "/api/users/42");

        assertTrue(interceptor.preHandle(allowed, new MockHttpServletResponse(), plainHandler()));
        assertTrue(currentAuth.hasPermission("GET", "/api/users/42"));
        interceptor.afterCompletion(allowed, new MockHttpServletResponse(), plainHandler(), null);

        MockHttpServletRequest denied = new MockHttpServletRequest("POST", "/api/users/42");
        assertThrows(ForbiddenException.class, () -> interceptor.preHandle(denied,
                new MockHttpServletResponse(), plainHandler()));
    }

    @Test
    void annotatedModeSkipsUnannotatedHandlers() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        AuthInterceptor<Object> strategy = (token, request) -> {
            calls.incrementAndGet();
            return new AuthPrincipal<Object>("42");
        };
        SmartMvcAuthInterceptor interceptor = interceptor(strategy, new CurrentAuth(),
                new SmartMvcProperties());

        assertTrue(interceptor.preHandle(new MockHttpServletRequest(),
                new MockHttpServletResponse(), plainHandler()));
        assertEquals(0, calls.get());
        assertNull(AuthContext.get());
    }

    @Test
    void anonymousMethodOverridesGlobalAuthentication() throws Exception {
        SmartMvcProperties properties = new SmartMvcProperties();
        properties.getAuth().setMode(AuthenticationMode.GLOBAL);
        AtomicInteger calls = new AtomicInteger();
        AuthInterceptor<Object> strategy = (token, request) -> {
            calls.incrementAndGet();
            return null;
        };
        SmartMvcAuthInterceptor interceptor = interceptor(strategy, new CurrentAuth(), properties);

        assertTrue(interceptor.preHandle(new MockHttpServletRequest(),
                new MockHttpServletResponse(), securedHandler("open")));
        assertEquals(0, calls.get());
    }

    private SmartMvcAuthInterceptor interceptor(AuthInterceptor<?> strategy,
                                                CurrentAuth currentAuth,
                                                SmartMvcProperties properties) {
        return new SmartMvcAuthInterceptor(strategy, currentAuth, properties);
    }

    private HandlerMethod securedHandler(String name) throws NoSuchMethodException {
        return new HandlerMethod(new SecuredController(), SecuredController.class.getMethod(name));
    }

    private HandlerMethod plainHandler() throws NoSuchMethodException {
        return new HandlerMethod(new PlainController(), PlainController.class.getMethod("plain"));
    }

    /**
     * Controller fixture containing secured and anonymous handler methods.
     */
    @Auth(roles = "admin", permissions = "order:read")
    public static class SecuredController {
        public void secured() {
        }

        @Anonymous
        public void open() {
        }
    }

    /**
     * Controller fixture without authentication annotations.
     */
    public static class PlainController {
        public void plain() {
        }
    }

    /**
     * Application-specific user model used by typed session access tests.
     */
    private static final class TestUser {

        private final String name;

        private TestUser(String name) {
            this.name = name;
        }
    }
}
