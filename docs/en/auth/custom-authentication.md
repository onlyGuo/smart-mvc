---
title: Custom authentication and the current user
description: Implement AuthInterceptor, build an AuthPrincipal, and use CurrentAuth safely in application code.
prev:
  text: Authentication and authorization
  link: /en/auth/overview.html
next:
  text: Sign-in and authorization example
  link: /en/examples/login.html
---

# Custom authentication and the current user

This guide implements Bearer Token authentication, then explains the lifecycle of roles, request permissions, and `CurrentAuth`.

## 1. Define the application user

```java
public record AppUser(
        String id,
        String username,
        String displayName
) {
}
```

SmartMVC does not require the user object to implement a framework interface. It can be an entity or DTO that already exists in the application.

## 2. Implement `AuthInterceptor`

```java
@Component
public class TokenAuthInterceptor implements AuthInterceptor<AppUser> {

    private final TokenService tokenService;

    public TokenAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public AuthPrincipal<AppUser> authenticate(
            String token,
            HttpServletRequest request) {
        AppUser user = tokenService.findUser(token);
        if (user == null) {
            return null;
        }

        return new AuthPrincipal<>(
                user.id(),
                user,
                Set.of("admin"),
                Set.of("user:read", "GET:/api/users/**"),
                Map.of("tenantId", "tenant-001")
        );
    }
}
```

Returning `null` means authentication failed. SmartMVC throws `UnauthorizedException`, and the client receives HTTP 401.

As soon as the application registers this bean, the default `PermitAllAuthInterceptor` backs off automatically.

## 3. Configure where credentials are read

```yaml
spring:
  smart:
    mvc:
      auth:
        authorization-header: Authorization
        token-prefix: Bearer
```

For this request header:

```text
Authorization: Bearer eyJhbGciOi...
```

`authenticate` receives the token after the configured prefix has been removed.

If credentials come from a cookie, session, or another source, override `resolveToken`:

```java
@Override
public String resolveToken(
        HttpServletRequest request,
        AuthConfig config) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
        return null;
    }
    return Arrays.stream(cookies)
            .filter(cookie -> "session_token".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
}
```

## `AuthPrincipal<T>`

The identity returned after successful authentication contains:

| Value | Purpose |
| --- | --- |
| `id` | A stable, non-null user identifier |
| `user` | The application's own user object; it may be null |
| `roles` | The user's roles |
| `permissions` | Named permissions and request permissions |
| `attributes` | Additional values such as tenant or authentication method |

Collections and maps are defensively copied and made read-only. This prevents the identity from changing unexpectedly after authentication completes.

## Using `CurrentAuth` in application code

```java
@Service
public class ProfileService {

    private final CurrentAuth currentAuth;

    public ProfileService(CurrentAuth currentAuth) {
        this.currentAuth = currentAuth;
    }

    public ProfileView currentProfile() {
        AppUser user = currentAuth.getUser(AppUser.class);
        return ProfileView.from(user);
    }
}
```

Frequently used methods include:

```java
currentAuth.isAuthenticated();
currentAuth.getPrincipal();
currentAuth.requirePrincipal();
currentAuth.getUserId();
currentAuth.getUser(AppUser.class);
currentAuth.getRoles();
currentAuth.getPermissions();
currentAuth.hasRole("admin");
currentAuth.hasPermission("GET", "/api/users/1001");
```

`requirePrincipal()` throws `IllegalStateException` when no identity is available. It is suitable for code that can only run during a protected request.

## Is it safe for concurrent requests?

`CurrentAuth` is a Spring singleton, but it does not store the user in instance fields. Every lookup delegates to `AuthContext`, which uses `ThreadLocal` storage:

- synchronous Servlet requests are isolated from one another;
- stale context is cleared before a request begins;
- the context is cleared again after the request ends, preventing identity leakage when a thread-pool thread is reused.

The context does not propagate automatically to `@Async` methods, manually created threads, thread-pool tasks, or reactive pipelines. When work crosses a thread boundary, explicitly pass the required user ID, roles, or an immutable identity snapshot.

### Spring MVC asynchronous handling

`Callable`, `DeferredResult`, `WebAsyncTask`, and `StreamingResponseBody` also cross the Servlet request-thread boundary. The current authentication interceptor clears identity in the synchronous `afterCompletion` callback and does not handle the `afterConcurrentHandlingStarted` transition. The automatic cleanup guarantee above therefore applies only to synchronous Servlet requests: an asynchronous task does not inherit `CurrentAuth`, and the initial request thread is not cleared automatically at the point where asynchronous handling begins.

When using a Spring MVC asynchronous return type, copy only the minimal immutable identity data before leaving the synchronous request thread and arrange cleanup for the asynchronous lifecycle, or provide an interceptor that explicitly handles the asynchronous transition. Do not rely on `CurrentAuth` directly inside the asynchronous callback.

## Request permission expressions

Enable request-level permission checking:

```yaml
spring.smart.mvc.auth.check-request-permission: true
```

A request permission has the form `METHOD:/path/pattern`:

| Expression | Matches |
| --- | --- |
| `GET:/api/users/**` | GET requests at every level below the path |
| `POST:/api/users/*` | POST requests with one child path segment |
| `*:/api/public/**` | Any HTTP method for the path |
| `*:*` | Any method and any path |

Path patterns support:

- `**` — multiple path segments;
- `*` — any characters within one path segment;
- `?` — one character.

HTTP method matching is case-insensitive.

## Customizing authorization

If authorization must call an external permission service or use a different model entirely, override `authorize`:

```java
@Override
public boolean authorize(
        AuthPrincipal<AppUser> principal,
        Auth requirement,
        HttpServletRequest request,
        AuthConfig config) {
    return permissionService.isAllowed(
            principal.getId(),
            request.getMethod(),
            request.getRequestURI()
    );
}
```

You can also override `bind`, `clear`, and `resolveRequestPath`. In most applications, override only the behavior that must change and keep the default lifecycle handling for everything else.

## Recommended starting configuration

```yaml
spring:
  smart:
    mvc:
      auth:
        enabled: true
        mode: GLOBAL
        authorization-header: Authorization
        token-prefix: Bearer
        check-request-permission: true
        exclude-paths:
          - /actuator/health
```

After connecting your own implementation, use the no-database example in the next section to verify the complete flow.
