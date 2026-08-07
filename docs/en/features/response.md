---
title: Consistent responses
description: Understand ApiResponse, automatic wrapping, excluded response types, and paginated results.
prev:
  text: How a request moves through SmartMVC
  link: /en/guide/how-it-works.html
next:
  text: Exceptions and validation
  link: /en/features/exceptions.html
---

# Consistent responses

A consistent response contract lets every client use the same rules to determine whether a request succeeded, read its data, and present an appropriate message.

## Response structure

`ApiResponse<T>` contains five fields:

| Field | Type | Meaning |
| --- | --- | --- |
| `success` | `boolean` | Whether the request completed as expected |
| `code` | `String` | A machine-readable result code |
| `message` | `String` | A human-readable explanation |
| `data` | `T` | Business data or error details |
| `timestamp` | `long` | The response creation time in milliseconds |

## Automatic wrapping

When `response.wrap-enabled` is enabled, an ordinary controller return value automatically becomes a successful response:

```java
@GetMapping("/{id}")
public UserView get(@PathVariable Long id) {
    return userService.get(id);
}
```

You do not need to call `ApiResponse.success(...)` in every controller method.

`String` return values are wrapped correctly as well. Even when Spring selects `StringHttpMessageConverter`, SmartMVC writes JSON rather than treating the response object as plain text.

## Response types that remain unchanged

SmartMVC does not wrap these return types:

- an object that is already an `ApiResponse`;
- `byte[]`;
- a Spring `Resource`;
- `StreamingResponseBody`;
- `ProblemDetail`.

These types commonly represent files, binary data, streaming output, or a standard problem response. Adding another JSON envelope would interfere with their intended behavior.

## Creating a response explicitly

When an endpoint needs a specific message or error code, return an `ApiResponse` directly:

```java
return ApiResponse.success("User created", userView);
```

```java
return ApiResponse.failure(
        "ORDER_CLOSED",
        "The order is already closed",
        Map.of("orderId", orderId)
);
```

A direct `ApiResponse` is recognized and will not be wrapped a second time.

## `void` return values

By default, `void` and `Void` become successful responses with `data: null`.

```yaml
spring:
  smart:
    mvc:
      response:
        wrap-void: false
```

With this setting disabled, a `void` return value no longer produces the response envelope.

## Paginated results

`PageResult<T>` is a general-purpose pagination model. It does not depend on a database or a particular pagination library:

```java
PageResult<UserView> result = new PageResult<>(
        users,
        total,
        page,
        pageSize
);
```

It provides:

- `items` — the records on the current page;
- `total` — the total number of records;
- `page` — the current page number;
- `pageSize` — the number of records per page;
- `totalPages` — the page count calculated from `total` and `pageSize`.

## Why are Long values strings by default?

JavaScript cannot represent every 64-bit integer exactly. SmartMVC therefore serializes `long` and `Long` values as strings by default:

```yaml
spring.smart.mvc.response.long-as-string: true
```

You can disable this option when every client can safely process 64-bit integer values.

## Configuration

```yaml
spring:
  smart:
    mvc:
      response:
        wrap-enabled: true
        wrap-void: true
        success-message: success
        long-as-string: true
```

When `wrap-enabled` is false, the automatic response-enhancement bean is not registered, and Spring MVC handles controller return values without SmartMVC wrapping.
