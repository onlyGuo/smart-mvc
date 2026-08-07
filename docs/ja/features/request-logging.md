---
title: リクエストログ
description: Controller ごとのロガー、概要の内容、出力レベル、機密情報の扱いを説明します。
prev:
  text: 日付と時刻
  link: /ja/features/date-time.html
next:
  text: 認証・認可の概要
  link: /ja/auth/overview.html
---

# リクエストログ

リクエストログは、「どの API が呼ばれたか」「どのステータスを返したか」「処理にどれだけ時間がかかったか」を短い 1 行で確認するための機能です。

## デフォルトの出力

```text
INFO  com.example.OrderController : HTTP GET /orders/1001 -> 200 (18 ms)
```

概要には次の情報が含まれます。

- HTTP メソッド
- リクエスト URI とクエリ文字列
- 最終的な HTTP ステータスコード
- ミリ秒単位の処理時間

## ロガーに Controller クラスを使う理由

リクエスト完了後、SmartMVC は Spring MVC が選択した Handler を確認します。その Handler が Controller メソッドであれば、Controller クラスをロガーカテゴリとして使います。

このため、特定の Controller だけログレベルを変更できます。

```yaml
logging:
  level:
    com.example.OrderController: DEBUG
```

Spring が静的リソースハンドラーなど、Controller メソッド以外の Handler を選択した場合は、その Handler の実クラスがロガーカテゴリになります。フォールバックカテゴリ `ink.icoding.mvc.request` が使われるのは、リクエストに一致した Handler が存在しない場合だけです。

## 同期リクエストと非同期リクエストの境界

通常の同期 Servlet リクエストでは、記録されるステータスと処理時間は、そのリクエスト処理の最終結果を表します。

`Callable`、`DeferredResult`、`WebAsyncTask`、`StreamingResponseBody` などの Spring MVC 非同期処理では、現在のフィルターは最初の Servlet dispatch が戻った時点でログを出力します。そのため、記録されたステータスと時間が、非同期タスクやストリームの実際の完了結果を表すとは限りません。完了時点を正確に計測する必要がある場合は、`AsyncListener` または非同期処理向けの観測コンポーネントを利用してください。

## SmartMVC のログレベルを設定する

```yaml
spring:
  smart:
    mvc:
      request-log:
        enabled: true
        level: INFO
```

`TRACE`、`DEBUG`、`INFO`、`WARN`、`ERROR` を指定でき、デフォルトは `INFO` です。

## 機密情報を記録しないために

デフォルトの概要ログには、次の情報を含めません。

- リクエストヘッダー
- リクエスト本文
- レスポンス本文
- Controller が返した業務オブジェクト

ただし、元のクエリ文字列は記録します。パスワード、トークン、本人確認情報などの機密値を URL パラメーターに入れないでください。Web サーバー、プロキシ、ログ基盤にも URL が保存される可能性があります。

業務監査ログが必要な場合は、業務層で `CurrentAuth` を参照し、目的に必要な項目だけを選んで、適切にマスキングして記録してください。

## リクエストログを無効にする

```yaml
spring.smart.mvc.request-log.enabled: false
```

無効にすると、リクエストログフィルターは登録されません。
