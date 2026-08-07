---
title: Configuration reference
description: All spring.smart.mvc properties, including their types, defaults, and behavior.
prev:
  text: Sign-in and authorization example
  link: /en/examples/login.html
next:
  text: API reference
  link: /en/reference/api.html
---

# Configuration reference

Every property uses the prefix `spring.smart.mvc`. The defaults in these tables come from the `core` configuration models and are exposed to IDEs through the Starter's Spring Boot configuration metadata.

## `exception`

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `exception.enabled` | `boolean` | `true` | Register the SmartMVC global exception handler |
| `exception.status-mode` | `HTTP_STATUS \| ALWAYS_OK` | `HTTP_STATUS` | Use the actual HTTP status, or always transport errors with status 200 |

## `response`

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `response.wrap-enabled` | `boolean` | `true` | Wrap ordinary controller return values |
| `response.wrap-void` | `boolean` | `true` | Wrap `void` and `Void` return values |
| `response.success-message` | `String` | `success` | The `message` used for automatically created success responses |
| `response.long-as-string` | `boolean` | `true` | Serialize `long` and `Long` as strings |

`ApiResponse`, `byte[]`, Spring `Resource`, `StreamingResponseBody`, and `ProblemDetail` are never wrapped automatically.

## `date-time`

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `date-time.request-format` | `String` | `yyyy-MM-dd HH:mm:ss` | Request pattern for date-time types |
| `date-time.response-format` | `String` | `yyyy-MM-dd HH:mm:ss` | JSON output pattern for date-time types |
| `date-time.date-request-format` | `String` | `yyyy-MM-dd` | Request pattern for `LocalDate` |
| `date-time.date-response-format` | `String` | `yyyy-MM-dd` | Output pattern for `LocalDate` |
| `date-time.time-request-format` | `String` | `HH:mm:ss` | Request pattern for `LocalTime` |
| `date-time.time-response-format` | `String` | `HH:mm:ss` | Output pattern for `LocalTime` |
| `date-time.zone-id` | `String` | `system-default` | IANA time zone used when converting instant-based types and `Date` |
| `date-time.incomplete-input-policy` | `FILL_MISSING \| REJECT` | `FILL_MISSING` | Complete or reject incomplete date-time input |

The supported types are `LocalDateTime`, `LocalDate`, `LocalTime`, `Instant`, `OffsetDateTime`, `ZonedDateTime`, and `java.util.Date`. The latter four use the configured time zone during output.

## `validation`

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `validation.enabled` | `boolean` | `true` | Let SmartMVC integrate with and handle Spring MVC validation results; when disabled, SmartMVC configures a no-op validator |

This switch does not install a Bean Validation implementation. The application must explicitly add `spring-boot-starter-validation` and manage its version; SmartMVC does not bring in a validation provider transitively.

## `request-log`

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `request-log.enabled` | `boolean` | `true` | Log the method, URI, status code, and elapsed time |
| `request-log.level` | `TRACE \| DEBUG \| INFO \| WARN \| ERROR` | `INFO` | Log level for the request summary |

When a controller is matched, its class is used as the logger category.

## `auth`

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `auth.enabled` | `boolean` | `true` | Enable SmartMVC authentication and authorization checks; the interceptor itself remains registered |
| `auth.mode` | `GLOBAL \| ANNOTATED` | `ANNOTATED` | Authenticate every controller endpoint, or only endpoints marked with `@Auth` |
| `auth.check-request-permission` | `boolean` | `false` | Check `METHOD:PATH` permissions stored in the identity |
| `auth.authorization-header` | `String` | `Authorization` | Request header used to read credentials |
| `auth.token-prefix` | `String` | `Bearer` | Prefix removed from the request-header value |
| `auth.exclude-paths` | `String[]` | `[]` | Spring MVC path patterns that skip authentication completely |

`@Anonymous` skips authentication in both modes. `exclude-paths` also always applies: in `ANNOTATED` mode, an excluded path does not enter the authentication interceptor even if its handler has `@Auth`. For all other paths, `ANNOTATED` processes only handlers marked with `@Auth`.

## Complete YAML example

```yaml
spring:
  smart:
    mvc:
      exception:
        enabled: true
        status-mode: HTTP_STATUS
      response:
        wrap-enabled: true
        wrap-void: true
        success-message: success
        long-as-string: true
      date-time:
        request-format: yyyy-MM-dd HH:mm:ss
        response-format: yyyy-MM-dd HH:mm:ss
        date-request-format: yyyy-MM-dd
        date-response-format: yyyy-MM-dd
        time-request-format: HH:mm:ss
        time-response-format: HH:mm:ss
        zone-id: system-default
        incomplete-input-policy: FILL_MISSING
      validation:
        enabled: true
      request-log:
        enabled: true
        level: INFO
      auth:
        enabled: true
        mode: ANNOTATED
        check-request-permission: false
        authorization-header: Authorization
        token-prefix: Bearer
        exclude-paths: []
```
