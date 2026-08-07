package ink.icoding.mvc.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Connects the Example module's in-memory token service to the SmartMVC authentication contract.
 *
 * <p>Registering this component causes starter auto-configuration to replace its permit-all
 * default. Token extraction, annotation authorization, HTTP permission matching, and
 * {@link CurrentAuth} binding continue to use the default behavior supplied by
 * {@link AuthInterceptor}.</p>
 */
@Component
public class ExampleAuthInterceptor implements AuthInterceptor<ExampleUser> {

    private final ExampleAuthenticationService authenticationService;

    public ExampleAuthInterceptor(ExampleAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public AuthPrincipal<ExampleUser> authenticate(String token, HttpServletRequest request) {
        return authenticationService.authenticate(token);
    }
}
