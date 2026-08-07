---
title: Request logging
description: Understand controller logger categories, request summaries, log levels, and the boundary around sensitive data.
prev:
  text: Date and time
  link: /en/features/date-time.html
next:
  text: Authentication and authorization
  link: /en/auth/overview.html
---

# Request logging

A request summary answers three immediate questions: which endpoint was called, which status it returned, and how long it took.

## Default output

```text
INFO  com.example.OrderController : HTTP GET /orders/1001 -> 200 (18 ms)
```

The summary contains:

- the HTTP method;
- the request URI and query string;
- the final HTTP status code;
- elapsed time in milliseconds.

## Why the logger category is the controller

After a request completes, SmartMVC inspects the handler selected by Spring MVC. If that handler is a controller method, the controller class becomes the logger category.

This makes it possible to configure one controller directly:

```yaml
logging:
  level:
    com.example.OrderController: DEBUG
```

If Spring matches a non-controller handler, such as a static-resource handler, SmartMVC uses that handler's concrete class as the logger category. The fallback category `ink.icoding.mvc.request` is used only when the request has no matched handler.

## Synchronous and asynchronous requests

For an ordinary synchronous Servlet request, the logged status and elapsed time describe the final result of that request processing.

For Spring MVC asynchronous handling such as `Callable`, `DeferredResult`, `WebAsyncTask`, or `StreamingResponseBody`, the current filter writes its entry when the initial Servlet dispatch returns. The recorded status and duration therefore may not describe completion of the asynchronous task or stream. Use an `AsyncListener` or a dedicated asynchronous observability component when completion-time measurements are required.

## Configuring the SmartMVC log level

```yaml
spring:
  smart:
    mvc:
      request-log:
        enabled: true
        level: INFO
```

Supported levels are `TRACE`, `DEBUG`, `INFO`, `WARN`, and `ERROR`. The default is `INFO`.

## Sensitive-data boundary

The default summary does not record:

- request headers;
- the request body;
- the response body;
- business objects returned by the controller.

It does record the raw query string. Do not place passwords, tokens, identity numbers, or other sensitive values in URL parameters, because web servers, proxies, and logging systems may also retain URLs.

For application audit logs, read `CurrentAuth` in the business layer and record only the necessary, appropriately masked fields.

## Disabling request logging

```yaml
spring.smart.mvc.request-log.enabled: false
```

When disabled, the request-logging filter is not registered.
