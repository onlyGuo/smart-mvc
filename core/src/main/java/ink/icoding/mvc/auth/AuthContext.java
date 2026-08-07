package ink.icoding.mvc.auth;

/**
 * Stores the authenticated identity associated with the current request thread.
 *
 * <p>The context uses {@link ThreadLocal} isolation so concurrent synchronous requests do
 * not share identity state. Integrations must clear the context when request processing
 * completes to prevent state from surviving servlet thread reuse.</p>
 */
public final class AuthContext {

    private static final ThreadLocal<AuthPrincipal<?>> CURRENT =
            new ThreadLocal<AuthPrincipal<?>>();

    private AuthContext() {
    }

    public static AuthPrincipal<?> get() {
        return CURRENT.get();
    }

    public static AuthPrincipal<?> require() {
        AuthPrincipal<?> principal = get();
        if (principal == null) {
            throw new IllegalStateException("No authenticated principal in current context");
        }
        return principal;
    }

    public static void set(AuthPrincipal<?> principal) {
        if (principal == null) {
            clear();
        } else {
            CURRENT.set(principal);
        }
    }

    public static void clear() {
        CURRENT.remove();
    }
}
