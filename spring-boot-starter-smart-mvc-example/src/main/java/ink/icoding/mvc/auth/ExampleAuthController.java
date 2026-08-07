package ink.icoding.mvc.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Exposes executable login and authorization scenarios for the SmartMVC Example application.
 *
 * <p>The anonymous endpoints demonstrate bypass behavior, the current-user endpoint demonstrates
 * token authentication and {@link CurrentAuth}, and the administrator endpoint combines role,
 * named permission, and HTTP method/path permission checks.</p>
 */
@RestController
@RequestMapping("/auth")
public class ExampleAuthController {

    private final ExampleAuthenticationService authenticationService;
    private final CurrentAuth currentAuth;

    public ExampleAuthController(ExampleAuthenticationService authenticationService,
                                 CurrentAuth currentAuth) {
        this.authenticationService = authenticationService;
        this.currentAuth = currentAuth;
    }

    @Anonymous
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return new LoginResponse("Bearer",
                authenticationService.login(request.getUsername(), request.getPassword()));
    }

    @Anonymous
    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("authenticated", currentAuth.isAuthenticated());
        result.put("message", "This endpoint is intentionally anonymous");
        return result;
    }

    @Auth
    @GetMapping("/me")
    public AuthPrincipal<?> currentIdentity() {
        return currentAuth.requirePrincipal();
    }

    @Auth(roles = "admin", permissions = "admin:read")
    @GetMapping("/admin")
    public Map<String, Object> administratorEndpoint() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("message", "Administrator authorization succeeded");
        result.put("user", currentAuth.getUser(ExampleUser.class));
        result.put("roles", currentAuth.getRoles());
        result.put("permissions", currentAuth.getPermissions());
        return result;
    }

    /**
     * Mutable JSON request model containing the credentials accepted by the example login route.
     *
     * <p>Bean Validation rejects missing or blank values before the in-memory credential service
     * is called.</p>
     */
    public static class LoginRequest {

        @NotBlank
        private String username;

        @NotBlank
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * Immutable login result containing the scheme and token required by protected endpoints.
     */
    public static class LoginResponse {

        private final String tokenType;
        private final String token;

        public LoginResponse(String tokenType, String token) {
            this.tokenType = tokenType;
            this.token = token;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getToken() {
            return token;
        }
    }
}
