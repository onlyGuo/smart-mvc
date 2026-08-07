---
title: API reference
description: A concise reference for SmartMVC annotations, identity models, response objects, and exceptions.
prev:
  text: Configuration reference
  link: /en/reference/configuration.html
---

# API reference

## Annotations

### `@Auth`

May be used on a controller class, a method, or a composed annotation.

```java
@Auth(
    roles = { "admin", "auditor" },
    permissions = { "report:read" },
    mode = AuthMode.ANY
)
```

| Member | Type | Default | Description |
| --- | --- | --- | --- |
| `roles` | `String[]` | `{}` | Required roles |
| `permissions` | `String[]` | `{}` | Required named permissions |
| `mode` | `AuthMode` | `ALL` | Match all or any value within the role and permission groups |

### `@Anonymous`

Marks a class or method that does not require authentication. A method-level declaration takes precedence, and no current-user identity is created for the endpoint.

## `AuthPrincipal<T>`

An immutable representation of the current identity:

| Method | Returns |
| --- | --- |
| `getId()` | Stable user identifier |
| `getUser()` | The application's user object; it may be null |
| `getRoles()` | Immutable role set |
| `getPermissions()` | Immutable permission set |
| `getAttributes()` | Immutable map of additional attributes |
| `getAttribute(name)` | One additional attribute |

The constructor defensively copies collections and maps. `id` cannot be null.

## `CurrentAuth`

A Spring-managed singleton facade backed by the request thread context:

```java
currentAuth.isAuthenticated();
currentAuth.getUserId();
currentAuth.getUser(AppUser.class);
currentAuth.getRoles();
currentAuth.getPermissions();
currentAuth.hasRole("admin");
currentAuth.hasPermission("GET", "/api/users/42");
```

`requirePrincipal()` throws `IllegalStateException` if no identity is available. It is normally used only on code paths already protected by `@Auth`.

## `AuthInterceptor<T>`

The required method is:

```java
AuthPrincipal<T> authenticate(String token, HttpServletRequest request);
```

Optional methods to override are `resolveToken`, `authorize`, `bind`, `clear`, and `resolveRequestPath`. When the application provides this bean, the auto-configured `PermitAllAuthInterceptor` backs off.

## `ApiResponse<T>`

Fields: `success`, `code`, `message`, `data`, and `timestamp`.

```java
ApiResponse.success(data);
ApiResponse.success("created", data);
ApiResponse.failure("ORDER_CLOSED", "Order is already closed");
ApiResponse.failure("INVALID_LINES", "Some lines are invalid", details);
```

## `PageResult<T>`

```java
new PageResult<>(items, total, page, pageSize);
```

Fields: `items`, `total`, `page`, and `pageSize`. `getTotalPages()` rounds up when calculating the number of pages.

## Exception matrix

Every exception extends `SmartMvcException` and does not generate a stack trace.

| Exception | code | HTTP |
| --- | --- | ---: |
| `BadRequestException` | `BAD_REQUEST` | 400 |
| `ParameterValidationException` | `PARAMETER_VALIDATION_FAILED` | 400 |
| `UnauthorizedException` | `UNAUTHORIZED` | 401 |
| `ForbiddenException` | `FORBIDDEN` | 403 |
| `ResourceNotFoundException` | `RESOURCE_NOT_FOUND` | 404 |
| `MethodNotAllowedException` | `METHOD_NOT_ALLOWED` | 405 |
| `NotAcceptableException` | `NOT_ACCEPTABLE` | 406 |
| `RequestTimeoutException` | `REQUEST_TIMEOUT` | 408 |
| `ConflictException` | `CONFLICT` | 409 |
| `GoneException` | `GONE` | 410 |
| `PayloadTooLargeException` | `PAYLOAD_TOO_LARGE` | 413 |
| `UnsupportedMediaTypeException` | `UNSUPPORTED_MEDIA_TYPE` | 415 |
| `BusinessException` | `BUSINESS_ERROR` / custom | 422 |
| `UnprocessableEntityException` | `UNPROCESSABLE_ENTITY` | 422 |
| `LockedException` | `LOCKED` | 423 |
| `TooManyRequestsException` | `TOO_MANY_REQUESTS` | 429 |
| `BusinessExecutionException` | `BUSINESS_EXECUTION_FAILED` | 500 |
| `InternalServerException` | `INTERNAL_SERVER_ERROR` | 500 |
| `NotImplementedException` | `NOT_IMPLEMENTED` | 501 |
| `BadGatewayException` | `BAD_GATEWAY` | 502 |
| `ServiceUnavailableException` | `SERVICE_UNAVAILABLE` | 503 |
| `GatewayTimeoutException` | `GATEWAY_TIMEOUT` | 504 |

`new BusinessException(message)` uses the default code `BUSINESS_ERROR`. You can also provide an application-specific code, message, and details for a business rejection that the client can understand and handle.
