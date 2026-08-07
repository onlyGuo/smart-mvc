package ink.icoding.mvc.auth;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

/**
 * Default authentication strategy that grants every protected request a wildcard identity.
 *
 * <p>The generated principal owns the {@code *} role and {@code *:*} permission, allowing an
 * empty SmartMVC application to run without custom security code. Any application-defined
 * {@link AuthInterceptor} bean causes auto-configuration to back off from this implementation.</p>
 */
public class PermitAllAuthInterceptor implements AuthInterceptor<Object> {

    @Override
    public AuthPrincipal<Object> authenticate(String token, HttpServletRequest request) {
        return new AuthPrincipal<Object>("smart-mvc-permit-all", null,
                Collections.singleton("*"), Collections.singleton("*:*"), null);
    }
}
