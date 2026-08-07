---
title: Authentication and authorization
description: Understand authentication modes, @Auth, @Anonymous, roles, and permissions.
prev:
  text: Request logging
  link: /en/features/request-logging.html
next:
  text: Custom authentication and the current user
  link: /en/auth/custom-authentication.html
---

# Authentication and authorization

SmartMVC defines how authentication and authorization connect to the request lifecycle. It does not decide where users are stored, and it does not issue a particular kind of token.

The application is responsible for:

- validating a token, cookie, or session;
- loading the user, roles, and permissions;
- deciding whether the account is allowed to sign in;
- providing an `AuthInterceptor` implementation.

SmartMVC is responsible for:

- deciding whether the current endpoint requires authentication;
- extracting credentials from the request;
- calling the application's authentication implementation;
- checking annotation requirements and request permissions;
- binding the resulting identity to `CurrentAuth`;
- clearing that identity after the request completes.

## Two authentication modes

### `ANNOTATED`

```yaml
spring.smart.mvc.auth.mode: ANNOTATED
```

Only controller classes or methods marked with `@Auth` are authenticated. Endpoints without the annotation remain public.

This mode works well when:

- authentication is being introduced gradually into an existing application;
- the service has many public endpoints;
- every protected endpoint should be explicitly visible in code.

### `GLOBAL`

```yaml
spring.smart.mvc.auth.mode: GLOBAL
```

Every controller method requires authentication by default. Only endpoints marked with `@Anonymous` and paths in `exclude-paths` are skipped.

This mode works well when:

- the application is an administration system;
- most endpoints require a signed-in user;
- the application should be protected by default.

## `@Auth`

To require only a signed-in user:

```java
@Auth
@GetMapping("/profile")
public ProfileView profile() {
    // ...
}
```

To require a role and a named permission as well:

```java
@Auth(
    roles = "admin",
    permissions = "user:read"
)
@GetMapping("/users")
public List<UserView> users() {
    // ...
}
```

`@Auth` can be placed on a class, a method, or a composed annotation.

## `@Anonymous`

```java
@Anonymous
@PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {
    // ...
}
```

An anonymous endpoint skips authentication, authorization, and identity binding. With the default behavior:

- `currentAuth.isAuthenticated()` returns `false`;
- `currentAuth.getUser()` returns `null`;
- the role and permission sets are empty.

A method-level `@Auth` or `@Anonymous` declaration takes precedence over a class-level declaration, so an individual method can override the controller's default rule.

## Excluded paths

```yaml
spring:
  smart:
    mvc:
      auth:
        exclude-paths:
          - /actuator/health
          - /assets/**
```

Excluded paths apply in both `GLOBAL` and `ANNOTATED` mode. They never enter the SmartMVC authentication interceptor, even when the matched handler has `@Auth`.

## Multiple roles and permissions

The default `AuthMode.ALL` requires every declared value:

```java
@Auth(
    roles = { "admin", "operator" },
    permissions = { "user:read", "user:update" }
)
```

`AuthMode.ANY` requires at least one match in each non-empty group:

```java
@Auth(
    roles = { "admin", "auditor" },
    permissions = { "report:read", "report:export" },
    mode = AuthMode.ANY
)
```

This rule means “at least one role” **and** “at least one permission.” The role group and permission group are always joined with AND.

## Default authentication implementation

If the application does not provide an `AuthInterceptor`, the Starter registers `PermitAllAuthInterceptor`. It creates an identity with the wildcard role `*` and wildcard permission `*:*`, which lets a new project or a simple feature demonstration run immediately.

A production application that requires real authentication must provide its own implementation. The next guide builds that implementation step by step.
