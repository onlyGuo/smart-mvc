---
title: 例外と入力値検証
description: SmartMvcException、HTTP ステータスモード、フィールド検証エラーの詳細を含む一貫したエラーレスポンスについて説明します。
prev:
  text: 統一レスポンス
  link: /ja/features/response.html
next:
  text: 日付と時刻
  link: /ja/features/date-time.html
---

# 例外と入力値検証

統一例外処理の目的は、クライアントに安定したエラー形式を返しながら、サーバー側に診断に必要な情報を残すことです。

## SmartMVC の例外を送出する

たとえば、指定されたユーザーが見つからない場合は、`ResourceNotFoundException` を使えます。

```java
public UserView get(Long id) {
    return repository.findById(id)
            .map(UserView::from)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User " + id + " was not found"));
}
```

デフォルトの HTTP ステータスは 404 で、レスポンス本文は次の形式になります。

```json
{
  "success": false,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User 1001 was not found",
  "data": null,
  "timestamp": "1786005000000"
}
```

## 共通の基底クラス

SmartMVC の例外はすべて `SmartMvcException` を継承します。基底クラスには次の情報が保存されます。

- HTTP ステータスコード
- 業務エラーコード
- エラーメッセージ
- 任意の詳細データ
- 任意の原因例外

これらの例外はスタックトレースを生成せず、例外抑制も無効です。入力拒否や権限不足など、想定内の業務上・HTTP 上の失敗を効率よく表現できます。

一方、未知のプログラムエラーではスタックトレースが必要です。グローバルハンドラーはそのような例外を完全なスタックトレース付きでログに記録し、クライアントには安全な HTTP 500 レスポンスを返します。

## よく使う例外

| 状況 | 例外 | HTTP |
| --- | --- | ---: |
| リクエストパラメーターまたは内容が不正 | `BadRequestException` | 400 |
| 認証に失敗 | `UnauthorizedException` | 401 |
| 認証済みだが権限が不足 | `ForbiddenException` | 403 |
| リソースが存在しない | `ResourceNotFoundException` | 404 |
| 現在の状態と操作が競合 | `ConflictException` | 409 |
| 業務ルールを満たさない | `BusinessException` | 422 |
| リクエスト回数が多すぎる | `TooManyRequestsException` | 429 |
| 業務処理中に内部エラーが発生 | `BusinessExecutionException` | 500 |
| 下流サービスが一時的に利用不可 | `ServiceUnavailableException` | 503 |

すべての例外は [API リファレンス](../reference/api.md)で確認できます。

## 独自の業務エラーコード

`BusinessException` は、クライアントが識別して処理できる業務上の失敗に適しています。

```java
throw new BusinessException(
        "ORDER_ALREADY_PAID",
        "The order has already been paid",
        Map.of("orderId", orderId)
);
```

デフォルトでは HTTP 422 に対応し、`details` はレスポンスの `data` フィールドに入ります。クライアントは `code` を判定に使い、`message` を表示し、必要であれば `data` から追加情報を取得できます。

## Bean Validation

Bean Validation の API と実装は、アプリケーション側で用意します。アプリケーションに `spring-boot-starter-validation` を明示的に追加してください。SmartMVC は検証 Provider を推移的依存関係として提供せず、Spring MVC が生成した検証結果を受け取り、統一されたエラーレスポンスへ変換する処理だけを行います。

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

検証に失敗すると、SmartMVC は `PARAMETER_VALIDATION_FAILED` を返し、`data` にフィールドごとの詳細を格納します。

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

クライアントは、この情報を使って各入力欄の近くに適切なメッセージを表示できます。

## 一般的な Spring MVC 例外

グローバルハンドラーは、SmartMVC 固有の例外だけでなく、次のような Spring MVC の例外も変換します。

- 必須パラメーターの不足、型変換失敗、JSON の読み取り失敗：HTTP 400
- 静的リソースまたは MVC リソースが見つからない：HTTP 404
- HTTP メソッドが許可されていない：HTTP 405
- Content-Type がサポートされていない：HTTP 415
- メソッド引数の検証失敗：HTTP 400

## HTTP ステータスモード

新しいプロジェクトでは、実際の HTTP ステータスを維持する設定を推奨します。

```yaml
spring.smart.mvc.exception.status-mode: HTTP_STATUS
```

既存のクライアント仕様などにより、通信上のステータスを常に HTTP 200 にする必要がある場合は、次の設定を使用できます。

```yaml
spring.smart.mvc.exception.status-mode: ALWAYS_OK
```

この場合でも、エラーコード、メッセージ、詳細はレスポンス本文に保持されます。ただし、HTTP の標準的な意味を利用できる新規システムでは `HTTP_STATUS` が適切です。

## デフォルト例外処理を無効にする

```yaml
spring.smart.mvc.exception.enabled: false
```

無効にすると、アプリケーション独自の `@RestControllerAdvice` で例外処理を完全に置き換えられます。
