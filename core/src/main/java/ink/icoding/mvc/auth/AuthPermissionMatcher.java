package ink.icoding.mvc.auth;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Matches an HTTP method and request path against framework-neutral permission expressions.
 *
 * <p>An expression uses the form {@code METHOD:/path/pattern}. The method and path may
 * both be {@code *}; path patterns support {@code **}, {@code *} and {@code ?}. Matching
 * is case-insensitive for HTTP methods, normalizes missing leading slashes, and does not
 * depend on Spring path-matching classes.</p>
 */
public final class AuthPermissionMatcher {

    private AuthPermissionMatcher() {
    }

    public static boolean matches(Set<String> permissions, String method, String path) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        for (String permission : permissions) {
            if (matches(permission, method, path)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matches(String permission, String method, String path) {
        if (permission == null) {
            return false;
        }
        String expression = permission.trim();
        int separator = expression.indexOf(':');
        if (separator < 0) {
            separator = expression.indexOf('：');
        }
        if (separator < 0) {
            return false;
        }

        String expectedMethod = expression.substring(0, separator).trim();
        String expectedPath = normalizePath(expression.substring(separator + 1).trim());
        String actualMethod = method == null ? "" : method.trim();
        String actualPath = normalizePath(path);
        return ("*".equals(expectedMethod) || expectedMethod.equalsIgnoreCase(actualMethod))
                && pathMatches(expectedPath, actualPath);
    }

    private static String normalizePath(String path) {
        if (path == null || path.trim().isEmpty() || "*".equals(path.trim())) {
            return "*";
        }
        String normalized = path.trim();
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }

    private static boolean pathMatches(String pattern, String path) {
        if ("*".equals(pattern) || "**".equals(pattern) || "/**".equals(pattern)) {
            return true;
        }
        String[] patternSegments = splitPath(pattern);
        String[] pathSegments = splitPath(path);
        return matchesSegments(patternSegments, 0, pathSegments, 0);
    }

    private static String[] splitPath(String path) {
        String stripped = path;
        while (stripped.startsWith("/")) {
            stripped = stripped.substring(1);
        }
        while (stripped.endsWith("/") && !stripped.isEmpty()) {
            stripped = stripped.substring(0, stripped.length() - 1);
        }
        return stripped.isEmpty() ? new String[0] : stripped.split("/+");
    }

    private static boolean matchesSegments(String[] patterns, int patternIndex,
                                           String[] paths, int pathIndex) {
        if (patternIndex == patterns.length) {
            return pathIndex == paths.length;
        }
        String pattern = patterns[patternIndex];
        if ("**".equals(pattern)) {
            if (patternIndex == patterns.length - 1) {
                return true;
            }
            for (int nextPath = pathIndex; nextPath <= paths.length; nextPath++) {
                if (matchesSegments(patterns, patternIndex + 1, paths, nextPath)) {
                    return true;
                }
            }
            return false;
        }
        return pathIndex < paths.length && segmentMatches(pattern, paths[pathIndex])
                && matchesSegments(patterns, patternIndex + 1, paths, pathIndex + 1);
    }

    private static boolean segmentMatches(String pattern, String path) {
        StringBuilder regex = new StringBuilder("^");
        for (int index = 0; index < pattern.length(); index++) {
            char current = pattern.charAt(index);
            if (current == '*') {
                regex.append(".*");
            } else if (current == '?') {
                regex.append('.');
            } else {
                regex.append(Pattern.quote(String.valueOf(current)));
            }
        }
        return path.matches(regex.append('$').toString());
    }
}
