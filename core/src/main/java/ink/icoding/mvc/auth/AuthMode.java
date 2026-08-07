package ink.icoding.mvc.auth;

/**
 * Defines how multiple role or permission requirements declared by {@link Auth} are combined.
 *
 * <p>{@link #ALL} requires every declared value, while {@link #ANY} accepts an identity
 * that contains at least one declared value.</p>
 */
public enum AuthMode {
    ALL,
    ANY
}
