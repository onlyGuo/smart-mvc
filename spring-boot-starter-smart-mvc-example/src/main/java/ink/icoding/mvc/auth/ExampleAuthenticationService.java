package ink.icoding.mvc.auth;

import ink.icoding.mvc.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Provides deterministic in-memory credentials and tokens for authentication demonstrations.
 *
 * <p>This service is intentionally not production security infrastructure. It stores plain-text
 * example passwords and fixed tokens solely to make the Example module executable without a
 * database or external identity provider. Two identities demonstrate user and administrator
 * roles together with annotation and method-and-path permission checks.</p>
 */
@Service
public class ExampleAuthenticationService {

    private final Map<String, ExampleAccount> accountsByUsername;
    private final Map<String, ExampleAccount> accountsByToken;

    public ExampleAuthenticationService() {
        ExampleAccount administrator = new ExampleAccount(
                new ExampleUser("1001", "admin", "Example Administrator"),
                "admin123", "example-admin-token", Set.of("admin"),
                Set.of("admin:read", "GET:/auth/**", "GET:/example/**"));
        ExampleAccount user = new ExampleAccount(
                new ExampleUser("1002", "user", "Example User"),
                "user123", "example-user-token", Set.of("user"),
                Set.of("profile:read", "GET:/auth/me"));

        Map<String, ExampleAccount> usernames = new LinkedHashMap<String, ExampleAccount>();
        usernames.put(administrator.user.getUsername(), administrator);
        usernames.put(user.user.getUsername(), user);
        this.accountsByUsername = Collections.unmodifiableMap(usernames);

        Map<String, ExampleAccount> tokens = new LinkedHashMap<String, ExampleAccount>();
        tokens.put(administrator.token, administrator);
        tokens.put(user.token, user);
        this.accountsByToken = Collections.unmodifiableMap(tokens);
    }

    public String login(String username, String password) {
        ExampleAccount account = accountsByUsername.get(username);
        if (account == null || !Objects.equals(account.password, password)) {
            throw new UnauthorizedException("Invalid example username or password");
        }
        return account.token;
    }

    public AuthPrincipal<ExampleUser> authenticate(String token) {
        ExampleAccount account = accountsByToken.get(token);
        if (account == null) {
            return null;
        }
        return new AuthPrincipal<ExampleUser>(account.user.getId(), account.user,
                account.roles, account.permissions,
                Collections.<String, Object>singletonMap("authenticationType", "example-token"));
    }

    /**
     * Immutable internal account definition that associates credentials and authorization data
     * with an example user without exposing password information through controller responses.
     */
    private static final class ExampleAccount {

        private final ExampleUser user;
        private final String password;
        private final String token;
        private final Set<String> roles;
        private final Set<String> permissions;

        private ExampleAccount(ExampleUser user, String password, String token,
                               Set<String> roles, Set<String> permissions) {
            this.user = user;
            this.password = password;
            this.token = token;
            this.roles = roles;
            this.permissions = permissions;
        }
    }
}
