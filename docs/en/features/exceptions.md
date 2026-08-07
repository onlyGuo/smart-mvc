---
title: Exceptions and request validation
description: Use SmartMvcException, HTTP status modes, and field validation details to produce predictable error responses.
prev:
  text: Consistent responses
  link: /en/features/response.html
next:
  text: Date and time
  link: /en/features/date-time.html
---

# Exceptions and request validation

Consistent exception handling gives clients a stable error contract while preserving the diagnostic information the server needs.

## Throwing a SmartMVC exception

```java
public UserView get(Long id) {
    return repository.findById(id)
            .map(UserView::from)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User " + id + " was not found"));
}
```

The default HTTP status is 404, and the response body is:

```json
{
  "success": false,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User 1001 was not found",
  "data": null,
  "timestamp": "1786005000000"
}
```

## The common exception base class

Every SmartMVC exception extends `SmartMvcException`. The base class stores:

- the HTTP status code;
- the application error code;
- the error message;
- optional detail data;
- an optional original cause.

These exceptions do not generate stack traces and have suppression disabled. They are designed for expected business rejections and expected HTTP failures. Unexpected programming errors are different: the global handler still records their complete stack traces on the server and returns a safe 500 response to the client.

## Common exceptions

| Situation | Exception | HTTP |
| --- | --- | ---: |
| Invalid request parameters or content | `BadRequestException` | 400 |
| Authentication failed | `UnauthorizedException` | 401 |
| Authenticated but not permitted | `ForbiddenException` | 403 |
| Resource does not exist | `ResourceNotFoundException` | 404 |
| Operation conflicts with the current state | `ConflictException` | 409 |
| Business rule is not satisfied | `BusinessException` | 422 |
| Too many requests | `TooManyRequestsException` | 429 |
| Internal failure while executing business logic | `BusinessExecutionException` | 500 |
| A downstream service is temporarily unavailable | `ServiceUnavailableException` | 503 |

See the [API reference](../reference/api.md) for the complete list.

## Custom business error codes

Use `BusinessException` for business failures that a client can recognize and handle:

```java
throw new BusinessException(
        "ORDER_ALREADY_PAID",
        "The order has already been paid",
        Map.of("orderId", orderId)
);
```

It maps to HTTP 422 by default. Its `details` value becomes the response `data` field.

## Bean Validation

The application provides the Bean Validation API and implementation. Add `spring-boot-starter-validation` explicitly to the application; SmartMVC does not bring in a validation provider transitively. It only integrates with validation results produced by Spring MVC and converts them into the standardized error response.

```java
public record CreateUserRequest(
        @NotBlank String username,
        @Email String email
) {
}

@PostMapping
public UserView create(@Valid @RequestBody CreateUserRequest request) {
    return userService.create(request);
}
```

When validation fails, SmartMVC returns `PARAMETER_VALIDATION_FAILED` and includes field details in `data`:

```json
{
  "success": false,
  "code": "PARAMETER_VALIDATION_FAILED",
  "message": "Request validation failed",
  "data": [
    {
      "field": "email",
      "rejectedValue": "invalid",
      "message": "must be a well-formed email address"
    }
  ],
  "timestamp": "1786005000000"
}
```

A client can use this structure to place each validation message beside the corresponding field.

## Common Spring MVC errors

The global handler also converts:

- missing request parameters, failed type conversion, and unreadable JSON into HTTP 400;
- missing static or MVC resources into HTTP 404;
- unsupported request methods into HTTP 405;
- unsupported content types into HTTP 415;
- method-argument validation failures into HTTP 400.

## HTTP status mode

Keeping the actual HTTP status is recommended:

```yaml
spring.smart.mvc.exception.status-mode: HTTP_STATUS
```

If an existing protocol requires every response to use HTTP 200, choose:

```yaml
spring.smart.mvc.exception.status-mode: ALWAYS_OK
```

The application error code, message, and details remain in the response body. New applications should normally use `HTTP_STATUS`.

## Disabling the built-in exception handler

```yaml
spring.smart.mvc.exception.enabled: false
```

Once disabled, the application can provide its own `@RestControllerAdvice` without the SmartMVC handler.
