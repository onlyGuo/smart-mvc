package ink.icoding.mvc.auth;

/**
 * Immutable user model returned by the database-free authentication example.
 *
 * <p>The model deliberately contains only an identifier, username, and display name so the
 * example can demonstrate typed {@link CurrentAuth} access without introducing persistence,
 * password storage, or application-specific domain dependencies.</p>
 */
public final class ExampleUser {

    private final String id;
    private final String username;
    private final String displayName;

    public ExampleUser(String id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }
}
