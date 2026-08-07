---
title: Date and time
description: Configure request and response patterns, time zones, ISO input, and incomplete-input handling.
prev:
  text: Exceptions and request validation
  link: /en/features/exceptions.html
next:
  text: Request logging
  link: /en/features/request-logging.html
---

# Date and time

SmartMVC applies one date-time policy to request parameters, path variables, JSON request bodies, and JSON responses.

## Supported types

| Type | Request parameter | JSON input | JSON output | Uses `zone-id` |
| --- | :---: | :---: | :---: | :---: |
| `LocalDateTime` | ✓ | ✓ | ✓ | — |
| `LocalDate` | ✓ | ✓ | ✓ | — |
| `LocalTime` | ✓ | ✓ | ✓ | — |
| `Instant` | ✓ | ✓ | ✓ | ✓ |
| `OffsetDateTime` | ✓ | ✓ | ✓ | ✓ |
| `ZonedDateTime` | ✓ | ✓ | ✓ | ✓ |
| `java.util.Date` | ✓ | ✓ | ✓ | ✓ |

## Separate patterns for date, time, and date-time values

```yaml
spring:
  smart:
    mvc:
      date-time:
        request-format: yyyy-MM-dd HH:mm:ss
        response-format: yyyy-MM-dd HH:mm:ss
        date-request-format: yyyy-MM-dd
        date-response-format: yyyy-MM-dd
        time-request-format: HH:mm:ss
        time-response-format: HH:mm:ss
        zone-id: Asia/Shanghai
        incomplete-input-policy: FILL_MISSING
```

- `LocalDateTime` uses `request-format` and `response-format`;
- `LocalDate` uses the date-specific patterns;
- `LocalTime` uses the time-specific patterns;
- `Instant`, `OffsetDateTime`, `ZonedDateTime`, and `Date` are also converted with `zone-id`.

## Request parameter example

```java
@GetMapping("/events")
public List<EventView> events(
        @RequestParam LocalDate day,
        @RequestParam Instant from) {
    return eventService.find(day, from);
}
```

```text
GET /events?day=2026-08-06&from=2026-08-06%2009:30:00
```

The same string patterns apply to JSON fields.

## How the time zone is applied

`LocalDate`, `LocalTime`, and `LocalDateTime` do not contain time-zone information, so SmartMVC does not shift their values between zones.

Types such as `Instant` represent a specific point on the timeline and use `zone-id`:

- when a local date-time without an offset is parsed, the configured time zone turns it into a specific instant;
- when a value is serialized, SmartMVC first converts it to the configured time zone and then applies `response-format`.

The default value, `system-default`, uses the JVM's default time zone. If deployment environments may differ, set an explicit IANA time zone.

## Standard ISO input

In addition to the configured pattern, timeline-related types accept standard ISO input. The accepted forms depend on the target type:

| Target type | `...Z` | `...+08:00` | `...+08:00[Asia/Shanghai]` |
| --- | :---: | :---: | :---: |
| `Instant` | ✓ | ✓ | — |
| `OffsetDateTime` | ✓ | ✓ | — |
| `ZonedDateTime` | ✓ | ✓ | ✓ |
| `java.util.Date` | ✓ | ✓ | — |

For example:

```text
2026-08-06T01:30:00Z
2026-08-06T09:30:00+08:00
2026-08-06T09:30:00+08:00[Asia/Shanghai]
```

## Incomplete-input policy

When parsing with a target type's main pattern fails, `FILL_MISSING` tries the fixed short forms below. Each form applies only to the listed target types:

| Input | Target types | Completed value |
| --- | --- | --- |
| `2026-08-06 09:30` | `LocalDateTime`, `Instant`, `OffsetDateTime`, `ZonedDateTime`, `Date` | seconds become `00` |
| `2026-08-06` | `LocalDateTime`, `Instant`, `OffsetDateTime`, `ZonedDateTime`, `Date` | time becomes `00:00:00` |
| `2026-08` | `LocalDate` and the date-time types listed above | day becomes the first of the month; types with a time use midnight |
| `2026` | `LocalDate` and the date-time types listed above | date becomes January 1; types with a time use midnight |
| `09:30` | `LocalTime` | seconds become `00` |
| `09` | `LocalTime` | minutes and seconds become `00` |

`REJECT` disables these completion forms:

```yaml
spring.smart.mvc.date-time.incomplete-input-policy: REJECT
```

ISO fallback is independent of this policy and remains available for types that represent a point on the timeline.

## Choosing a Java type

- use `LocalDate` for a birthday, business date, or another date without a time;
- use `LocalTime` for a recurring local time of day;
- use `LocalDateTime` for a local date and time that has no time-zone meaning;
- prefer `Instant` for a specific moment transferred across regions or systems.
