package ink.icoding.mvc.auth;

/**
 * Defines the handler selection strategy used by the SmartMVC authentication integration.
 *
 * <p>The mode controls whether authentication is applied globally or only to explicitly
 * annotated handlers. Excluded paths and {@link Anonymous} declarations remain unauthenticated
 * in either mode.</p>
 */
public enum AuthenticationMode {

    /**
     * Authenticates every handler except excluded paths and handlers marked with {@link Anonymous}.
     */
    GLOBAL,

    /**
     * Authenticates only handlers marked with {@link Auth}.
     */
    ANNOTATED
}
