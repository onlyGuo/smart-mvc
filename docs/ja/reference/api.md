---
title: API リファレンス
description: SmartMVC の主要アノテーション、認証モデル、レスポンスオブジェクト、例外をすばやく確認できます。
prev:
  text: 設定リファレンス
  link: /ja/reference/configuration.html
---

# API リファレンス

この章は、これまでに説明した主要 API を調べるための一覧です。初めて利用する場合は、先に[クイックスタート](../guide/getting-started.md)と各機能の説明を読むと、ここにある型の役割を理解しやすくなります。

## アノテーション

### `@Auth`

Controller クラス、メソッド、または合成アノテーションに使用できます。

```java
@Auth(
    roles = { "admin", "auditor" },
    permissions = { "report:read" },
    mode = AuthMode.ANY
)
```

| メンバー | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `roles` | `String[]` | `{}` | 必要なロールです。 |
| `permissions` | `String[]` | `{}` | 必要な名前付き権限です。 |
| `mode` | `AuthMode` | `ALL` | 複数のロールと権限について、すべて一致またはいずれか一致を指定します。 |

### `@Anonymous`

認証を必要としないクラスまたはメソッドを示します。メソッド側の宣言が優先され、匿名 API では現在のユーザー認証情報を作成しません。

## `AuthPrincipal<T>`

現在の利用者を表す不変の認証情報です。

| メソッド | 戻り値 |
| --- | --- |
| `getId()` | 変化しないユーザー識別子 |
| `getUser()` | アプリケーション独自のユーザーオブジェクト。`null` を許可します。 |
| `getRoles()` | 変更できないロール集合 |
| `getPermissions()` | 変更できない権限集合 |
| `getAttributes()` | 変更できない追加属性 Map |
| `getAttribute(name)` | 指定した名前の追加属性 |

コンストラクターは集合と Map を防御的にコピーします。`id` は `null` または空白にはできません。

## `CurrentAuth`

Spring が管理するシングルトンのファサードです。内部ではリクエストスレッドのコンテキストを参照します。

```java
currentAuth.isAuthenticated();
currentAuth.getUserId();
currentAuth.getUser(AppUser.class);
currentAuth.getRoles();
currentAuth.getPermissions();
currentAuth.hasRole("admin");
currentAuth.hasPermission("GET", "/api/users/42");
```

`requirePrincipal()` は、認証情報がない場合に `IllegalStateException` を送出します。通常は、`@Auth` で保護された処理の中で使います。

## `AuthInterceptor<T>`

独自認証を組み込むには、次のメソッドを実装します。

```java
AuthPrincipal<T> authenticate(String token, HttpServletRequest request);
```

必要に応じて `resolveToken`、`authorize`、`bind`、`clear`、`resolveRequestPath` をオーバーライドできます。アプリケーションが `AuthInterceptor` の Bean を登録すると、自動構成の `PermitAllAuthInterceptor` は登録を控えます。

## `ApiResponse<T>`

フィールドは `success`、`code`、`message`、`data`、`timestamp` です。

```java
ApiResponse.success(data);
ApiResponse.success("created", data);
ApiResponse.failure("ORDER_CLOSED", "Order is already closed");
ApiResponse.failure("INVALID_LINES", "Some lines are invalid", details);
```

## `PageResult<T>`

```java
new PageResult<>(items, total, page, pageSize);
```

`items`、`total`、`page`、`pageSize` を持ちます。`getTotalPages()` は、全件数をページサイズで割り、端数を切り上げて総ページ数を計算します。

## 例外一覧

すべての例外は `SmartMvcException` を継承し、スタックトレースを生成しません。

| 例外 | code | HTTP |
| --- | --- | ---: |
| `BadRequestException` | `BAD_REQUEST` | 400 |
| `ParameterValidationException` | `PARAMETER_VALIDATION_FAILED` | 400 |
| `UnauthorizedException` | `UNAUTHORIZED` | 401 |
| `ForbiddenException` | `FORBIDDEN` | 403 |
| `ResourceNotFoundException` | `RESOURCE_NOT_FOUND` | 404 |
| `MethodNotAllowedException` | `METHOD_NOT_ALLOWED` | 405 |
| `NotAcceptableException` | `NOT_ACCEPTABLE` | 406 |
| `RequestTimeoutException` | `REQUEST_TIMEOUT` | 408 |
| `ConflictException` | `CONFLICT` | 409 |
| `GoneException` | `GONE` | 410 |
| `PayloadTooLargeException` | `PAYLOAD_TOO_LARGE` | 413 |
| `UnsupportedMediaTypeException` | `UNSUPPORTED_MEDIA_TYPE` | 415 |
| `BusinessException` | `BUSINESS_ERROR` / 独自指定 | 422 |
| `UnprocessableEntityException` | `UNPROCESSABLE_ENTITY` | 422 |
| `LockedException` | `LOCKED` | 423 |
| `TooManyRequestsException` | `TOO_MANY_REQUESTS` | 429 |
| `BusinessExecutionException` | `BUSINESS_EXECUTION_FAILED` | 500 |
| `InternalServerException` | `INTERNAL_SERVER_ERROR` | 500 |
| `NotImplementedException` | `NOT_IMPLEMENTED` | 501 |
| `BadGatewayException` | `BAD_GATEWAY` | 502 |
| `ServiceUnavailableException` | `SERVICE_UNAVAILABLE` | 503 |
| `GatewayTimeoutException` | `GATEWAY_TIMEOUT` | 504 |

`new BusinessException(message)` は、デフォルトの code `BUSINESS_ERROR` を使います。業務固有の code、メッセージ、details を渡すこともでき、クライアントが内容を理解して適切な操作へつなげられる業務上の拒否を表すのに適しています。
