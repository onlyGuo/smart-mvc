---
title: How a request moves through SmartMVC
description: Follow a request from arrival to response and understand the responsibility of each SmartMVC component.
prev:
  text: Quick start
  link: /en/guide/getting-started.html
next:
  text: Consistent responses
  link: /en/features/response.html
---

# How a request moves through SmartMVC

Once you know the execution order, it becomes easier to decide which feature or setting belongs at each point in the request lifecycle.

## Request processing flow

A typical request passes through these stages:

1. **Request logging starts the timer.** A filter records the start time.
2. **Spring MVC selects a handler.** It finds the controller method for the request.
3. **Authentication and authorization run.** If the endpoint is protected, the SmartMVC authentication interceptor processes it.
4. **Arguments are converted and validated.** Date-time strings become Java types, and Bean Validation checks the input.
5. **The controller executes.** Your application runs its own business logic.
6. **The result is normalized.** A normal return value becomes an `ApiResponse`; an exception becomes a consistent error response.
7. **A request summary is logged.** SmartMVC uses the actual controller as the logger category and includes the final status code and elapsed time.

## The successful path

```java
@GetMapping("/users/{id}")
public UserView get(@PathVariable Long id) {
    return userService.get(id);
}
```

The controller returns only the business data. After the method completes, response enhancement wraps it as follows:

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": { "id": "1001", "name": "Ada" },
  "timestamp": "1786005000000"
}
```

## The failure path

```java
throw new ResourceNotFoundException("User 1001 was not found");
```

The global exception handler reads the HTTP status, error code, and message from the exception. By default, the response uses HTTP 404:

```json
{
  "success": false,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User 1001 was not found",
  "data": null,
  "timestamp": "1786005000000"
}
```

## Which parts can an application replace?

SmartMVC uses Spring Boot's conditional auto-configuration. An application can:

- disable consistent response wrapping or the built-in exception handler;
- provide its own `AuthInterceptor`;
- provide its own `CurrentAuth` or related components;
- choose its date formats, time zone, and request-log level;
- return an `ApiResponse` directly when one endpoint needs precise control.

The framework supplies useful defaults, while the application remains responsible for its business rules. The following sections explain each capability in more detail, beginning with [consistent responses](../features/response.md).
