package ink.icoding.mvc.entitys;

import ink.icoding.mvc.auth.AuthenticationMode;

/**
 * Framework-neutral configuration for SmartMVC authentication and authorization.
 *
 * <p>The configuration selects the authentication scope, credential header and token prefix,
 * excluded request paths, and optional method-and-path permission checks. Spring Boot binds
 * these values from the {@code spring.smart.mvc.auth} namespace.</p>
 */
public class AuthConfig {

    private boolean enabled = true;
    private AuthenticationMode mode = AuthenticationMode.ANNOTATED;
    private boolean checkRequestPermission;
    private String authorizationHeader = "Authorization";
    private String tokenPrefix = "Bearer";
    private String[] excludePaths = new String[0];

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AuthenticationMode getMode() {
        return mode;
    }

    public void setMode(AuthenticationMode mode) {
        this.mode = mode == null ? AuthenticationMode.ANNOTATED : mode;
    }

    public boolean isCheckRequestPermission() {
        return checkRequestPermission;
    }

    public void setCheckRequestPermission(boolean checkRequestPermission) {
        this.checkRequestPermission = checkRequestPermission;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public String[] getExcludePaths() {
        return excludePaths.clone();
    }

    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = excludePaths == null ? new String[0] : excludePaths.clone();
    }
}
