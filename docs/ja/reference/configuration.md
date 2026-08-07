---
title: 設定リファレンス
description: spring.smart.mvc 配下のすべての設定項目、型、デフォルト値、振る舞いをまとめます。
prev:
  text: ログイン・認可のサンプル
  link: /ja/examples/login.html
next:
  text: API リファレンス
  link: /ja/reference/api.html
---

# 設定リファレンス

すべてのプロパティは `spring.smart.mvc` を接頭辞に持ちます。表に記載したデフォルト値は `core` の設定モデルで定義され、Starter の Spring Boot 設定メタデータを通じて IDE に公開されます。

最初からすべてを設定する必要はありません。各機能にはデフォルト値があり、変更したい項目だけを `application.yaml` に追加できます。

## `exception`

| プロパティ | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `exception.enabled` | `boolean` | `true` | SmartMVC のグローバル例外ハンドラーを登録します。 |
| `exception.status-mode` | `HTTP_STATUS \| ALWAYS_OK` | `HTTP_STATUS` | 実際の HTTP ステータスを使うか、エラー時も通信上のステータスを 200 にするかを選びます。 |

## `response`

| プロパティ | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `response.wrap-enabled` | `boolean` | `true` | 通常の Controller 戻り値を統一レスポンスでラップします。 |
| `response.wrap-void` | `boolean` | `true` | `void` / `Void` の戻り値もラップします。 |
| `response.success-message` | `String` | `success` | 自動生成する成功レスポンスの `message` です。 |
| `response.long-as-string` | `boolean` | `true` | `long` / `Long` を文字列としてシリアライズします。 |

`ApiResponse`、`byte[]`、Spring の `Resource`、`StreamingResponseBody`、`ProblemDetail` は自動ラップされません。

## `date-time`

| プロパティ | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `date-time.request-format` | `String` | `yyyy-MM-dd HH:mm:ss` | 日時型のリクエスト形式です。 |
| `date-time.response-format` | `String` | `yyyy-MM-dd HH:mm:ss` | 日時型を JSON へ出力するときの形式です。 |
| `date-time.date-request-format` | `String` | `yyyy-MM-dd` | `LocalDate` のリクエスト形式です。 |
| `date-time.date-response-format` | `String` | `yyyy-MM-dd` | `LocalDate` の出力形式です。 |
| `date-time.time-request-format` | `String` | `HH:mm:ss` | `LocalTime` のリクエスト形式です。 |
| `date-time.time-response-format` | `String` | `HH:mm:ss` | `LocalTime` の出力形式です。 |
| `date-time.zone-id` | `String` | `system-default` | Instant 系の型と `Date` の変換に使う IANA タイムゾーンです。 |
| `date-time.incomplete-input-policy` | `FILL_MISSING \| REJECT` | `FILL_MISSING` | 不完全な日時入力を補完するか、拒否するかを選びます。 |

対応する型は、`LocalDateTime`、`LocalDate`、`LocalTime`、`Instant`、`OffsetDateTime`、`ZonedDateTime`、`java.util.Date` です。後ろの 4 型には、出力時に設定済みのタイムゾーンが適用されます。

## `validation`

| プロパティ | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `validation.enabled` | `boolean` | `true` | SmartMVC が Spring MVC の引数検証結果を受け取り、処理できるようにします。無効にすると何もしないバリデーターを使用します。 |

この設定だけでは Bean Validation の実装は導入されません。アプリケーションが `spring-boot-starter-validation` を明示的に追加し、そのバージョンを管理してください。SmartMVC は検証 Provider を推移的依存関係として提供しません。

## `request-log`

| プロパティ | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `request-log.enabled` | `boolean` | `true` | HTTP メソッド、URI、ステータスコード、処理時間を出力します。 |
| `request-log.level` | `TRACE \| DEBUG \| INFO \| WARN \| ERROR` | `INFO` | 概要ログを出力するレベルです。 |

一致する Controller がある場合、ロガーカテゴリにはその Controller クラスが使われます。

## `auth`

| プロパティ | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `auth.enabled` | `boolean` | `true` | SmartMVC の認証・認可チェックを有効にします。インターセプター自体は登録されたままです。 |
| `auth.mode` | `GLOBAL \| ANNOTATED` | `ANNOTATED` | すべての API を認証するか、`@Auth` が付いた API だけを認証するかを選びます。 |
| `auth.check-request-permission` | `boolean` | `false` | 認証情報が持つ `METHOD:PATH` 形式の権限を検証します。 |
| `auth.authorization-header` | `String` | `Authorization` | 資格情報を読み取るリクエストヘッダーです。 |
| `auth.token-prefix` | `String` | `Bearer` | ヘッダーの値から取り除く接頭辞です。 |
| `auth.exclude-paths` | `String[]` | `[]` | 認証を完全にスキップする Spring MVC パスパターンです。 |

`@Anonymous` は両方の認証モードで認証をスキップします。`exclude-paths` も常に有効で、`ANNOTATED` モードでも、除外されたパスは `@Auth` が付いていても認証インターセプターに入りません。それ以外では、`ANNOTATED` は `@Auth` が付いた Handler だけを処理します。

## 完全な YAML 例

次の例は、すべての設定項目とデフォルト値を一度に示したものです。実際のアプリケーションでは、変更する項目だけを記述してください。

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
