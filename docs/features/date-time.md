---
title: 日期与时间
description: 配置请求与响应格式、时区、ISO 输入和缺失部分补全策略。
---

# 日期与时间

SmartMVC 让请求参数、路径变量、JSON 请求体和 JSON 响应遵循同一套日期时间规则。

## 支持的类型

| 类型 | 请求参数 | JSON 输入 | JSON 输出 | 使用 `zone-id` |
| --- | :---: | :---: | :---: | :---: |
| `LocalDateTime` | ✓ | ✓ | ✓ | — |
| `LocalDate` | ✓ | ✓ | ✓ | — |
| `LocalTime` | ✓ | ✓ | ✓ | — |
| `Instant` | ✓ | ✓ | ✓ | ✓ |
| `OffsetDateTime` | ✓ | ✓ | ✓ | ✓ |
| `ZonedDateTime` | ✓ | ✓ | ✓ | ✓ |
| `java.util.Date` | ✓ | ✓ | ✓ | ✓ |

## 分开配置日期、时间和日期时间

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

- `LocalDateTime` 使用 `request-format` 和 `response-format`；
- `LocalDate` 使用日期专用格式；
- `LocalTime` 使用时间专用格式；
- `Instant`、`OffsetDateTime`、`ZonedDateTime` 和 `Date` 会结合 `zone-id` 转换。

## 请求参数示例

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

同样的字符串格式也适用于 JSON 字段。

## 时区如何生效

`LocalDate`、`LocalTime` 和 `LocalDateTime` 本身没有时区，因此不会进行时区平移。

`Instant` 等表示时间线上具体时刻的类型会使用 `zone-id`：

- 解析没有偏移量的本地时间时，按配置时区转换为具体时刻；
- 输出时，先转换到配置时区，再应用 `response-format`。

默认值 `system-default` 使用 JVM 默认时区。部署环境可能变化时，建议显式指定 IANA 时区。

## 标准 ISO 输入

除了配置格式，时间线相关类型还支持标准 ISO 输入，但并不是每种目标类型都接受所有 ISO 形式：

| 目标类型 | `...Z` | `...+08:00` | `...+08:00[Asia/Shanghai]` |
| --- | :---: | :---: | :---: |
| `Instant` | ✓ | ✓ | — |
| `OffsetDateTime` | ✓ | ✓ | — |
| `ZonedDateTime` | ✓ | ✓ | ✓ |
| `java.util.Date` | ✓ | ✓ | — |

例如：

```text
2026-08-06T01:30:00Z
2026-08-06T09:30:00+08:00
2026-08-06T09:30:00+08:00[Asia/Shanghai]
```

## 不完整输入策略

`FILL_MISSING` 会在目标类型的主格式解析失败后，尝试以下固定形式。每种短形式只适用于表中列出的目标类型：

| 输入 | 适用目标类型 | 补全结果 |
| --- | --- | --- |
| `2026-08-06 09:30` | `LocalDateTime`、`Instant`、`OffsetDateTime`、`ZonedDateTime`、`Date` | 秒补为 `00` |
| `2026-08-06` | `LocalDateTime`、`Instant`、`OffsetDateTime`、`ZonedDateTime`、`Date` | 时间补为 `00:00:00` |
| `2026-08` | `LocalDate` 及上述日期时间类型 | 日期补为当月 1 日；需要时间的类型补为零点 |
| `2026` | `LocalDate` 及上述日期时间类型 | 日期补为当年 1 月 1 日；需要时间的类型补为零点 |
| `09:30` | `LocalTime` | 秒补为 `00` |
| `09` | `LocalTime` | 分和秒补为 `00` |

`REJECT` 不使用这些补全形式：

```yaml
spring.smart.mvc.date-time.incomplete-input-policy: REJECT
```

ISO 输入回退与此策略无关，适用于带时间线语义的类型。

## 类型选择建议

- 生日、营业日期等纯日期使用 `LocalDate`；
- 每天执行的本地时间使用 `LocalTime`；
- 不需要时区语义的本地日期时间使用 `LocalDateTime`；
- 跨地区传递的具体时刻优先使用 `Instant`。
