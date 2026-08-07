---
title: 日付と時刻
description: リクエストとレスポンスの形式、タイムゾーン、ISO 入力、不完全な入力の補完方針を説明します。
prev:
  text: 例外と入力値検証
  link: /ja/features/exceptions.html
next:
  text: リクエストログ
  link: /ja/features/request-logging.html
---

# 日付と時刻

SmartMVC は、リクエストパラメーター、パス変数、JSON リクエスト、JSON レスポンスで、共通の日付・時刻ルールを適用します。

## 対応する型

| 型 | リクエストパラメーター | JSON 入力 | JSON 出力 | `zone-id` を使用 |
| --- | :---: | :---: | :---: | :---: |
| `LocalDateTime` | ✓ | ✓ | ✓ | — |
| `LocalDate` | ✓ | ✓ | ✓ | — |
| `LocalTime` | ✓ | ✓ | ✓ | — |
| `Instant` | ✓ | ✓ | ✓ | ✓ |
| `OffsetDateTime` | ✓ | ✓ | ✓ | ✓ |
| `ZonedDateTime` | ✓ | ✓ | ✓ | ✓ |
| `java.util.Date` | ✓ | ✓ | ✓ | ✓ |

## 日付、時刻、日時を個別に設定する

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

- `LocalDateTime` は `request-format` と `response-format` を使います。
- `LocalDate` は日付専用の形式を使います。
- `LocalTime` は時刻専用の形式を使います。
- `Instant`、`OffsetDateTime`、`ZonedDateTime`、`Date` は `zone-id` と組み合わせて変換されます。

## リクエストパラメーターの例

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

同じ文字列形式を、JSON フィールドにも使用できます。

## タイムゾーンの適用方法

`LocalDate`、`LocalTime`、`LocalDateTime` 自体にはタイムゾーンがありません。そのため、これらの値に対して時差による移動は行われません。

`Instant` など、時間軸上の具体的な時点を表す型には `zone-id` が適用されます。

- オフセットを含まないローカル日時を解析するときは、設定されたタイムゾーンを使って具体的な時点に変換します。
- 出力するときは、先に設定されたタイムゾーンへ変換し、その後で `response-format` を適用します。

デフォルトの `system-default` は JVM のデフォルトタイムゾーンを使います。実行環境によってタイムゾーンが変わる可能性がある場合は、`Asia/Tokyo` のような IANA タイムゾーンを明示することをおすすめします。

## 標準 ISO 形式の入力

時間軸上の値を表す型は、設定した形式に加えて標準 ISO 表記も受け付けます。ただし、受け付ける形式は対象の型によって異なります。

| 対象の型 | `...Z` | `...+08:00` | `...+08:00[Asia/Shanghai]` |
| --- | :---: | :---: | :---: |
| `Instant` | ✓ | ✓ | — |
| `OffsetDateTime` | ✓ | ✓ | — |
| `ZonedDateTime` | ✓ | ✓ | ✓ |
| `java.util.Date` | ✓ | ✓ | — |

入力例：

```text
2026-08-06T01:30:00Z
2026-08-06T09:30:00+08:00
2026-08-06T09:30:00+08:00[Asia/Shanghai]
```

外部システム間で具体的な時点を受け渡す場合は、オフセットを含む ISO 形式を使うと、時差の解釈が明確になります。

## 不完全な入力の扱い

`FILL_MISSING` は、対象型の主要な形式で解析できなかった場合に、次の固定形式を試します。各短縮形式が使えるのは、表に記載した対象型だけです。

| 入力 | 対象の型 | 補完後の値 |
| --- | --- | --- |
| `2026-08-06 09:30` | `LocalDateTime`、`Instant`、`OffsetDateTime`、`ZonedDateTime`、`Date` | 秒を `00` に補完 |
| `2026-08-06` | `LocalDateTime`、`Instant`、`OffsetDateTime`、`ZonedDateTime`、`Date` | 時刻を `00:00:00` に補完 |
| `2026-08` | `LocalDate` と上記の日時型 | その月の 1 日に補完し、時刻を持つ型では 0 時にする |
| `2026` | `LocalDate` と上記の日時型 | その年の 1 月 1 日に補完し、時刻を持つ型では 0 時にする |
| `09:30` | `LocalTime` | 秒を `00` に補完 |
| `09` | `LocalTime` | 分と秒を `00` に補完 |

曖昧な入力を受け付けたくない場合は、`REJECT` を指定します。

```yaml
spring.smart.mvc.date-time.incomplete-input-policy: REJECT
```

ISO 入力へのフォールバックは、この方針とは独立しています。ISO 入力は、時間軸上の意味を持つ型に対して利用できます。

## 型の選び方

- 生年月日や営業日など、時刻を持たない日付には `LocalDate`
- 毎日決まった時刻など、日付を持たない時刻には `LocalTime`
- タイムゾーンを必要としないローカル日時には `LocalDateTime`
- 地域をまたいで共有する具体的な時点には、できるだけ `Instant`

型を先に正しく選ぶことで、フォーマットやタイムゾーンの設定も分かりやすくなります。
