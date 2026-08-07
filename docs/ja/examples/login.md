---
title: ログイン・認可のサンプル
description: Example モジュールを実行し、匿名アクセス、ログイン、現在のユーザー、ロール、リクエスト権限を確認します。
prev:
  text: 独自認証と現在のユーザー
  link: /ja/auth/custom-authentication.html
next:
  text: 設定リファレンス
  link: /ja/reference/configuration.html
---

# ログイン・認可のサンプル

`spring-boot-starter-smart-mvc-example` には、メモリ上だけで完結する認証サンプルがあります。外部サービスやデータベースには依存せず、SmartMVC への接続方法と各リクエストの結果を確認するためのものです。

このサンプルでは固定アカウント、学習用の平文パスワード、固定トークンを使います。ローカルでの学習と自動テスト専用であり、そのまま本番環境へ持ち込むことは想定していません。

## Example を起動する

最初に、リポジトリのルートディレクトリで全モジュールをインストールします。

```bash
mvn install -DskipTests
```

次に Example モジュールを起動します。

```bash
mvn -f spring-boot-starter-smart-mvc-example/pom.xml spring-boot:run
```

## サンプルアカウント

| ユーザー名 | パスワード | トークン | ロール |
| --- | --- | --- | --- |
| `admin` | `admin123` | `example-admin-token` | `admin` |
| `user` | `user123` | `example-user-token` | `user` |

## 1. 匿名 API へアクセスする

```bash
curl http://localhost:8080/auth/public
```

この API には `@Anonymous` が付いています。レスポンスデータの `authenticated` は `false` です。匿名 API では現在の認証情報を作成しないことを確認できます。

## 2. ログインしてトークンを取得する

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

レスポンスの `data` には次の値が返ります。

```json
{
  "tokenType": "Bearer",
  "token": "example-admin-token"
}
```

## 3. 現在の認証情報を取得する

取得したトークンを `Authorization` ヘッダーに指定します。

```bash
curl http://localhost:8080/auth/me \
  -H 'Authorization: Bearer example-admin-token'
```

返される `AuthPrincipal` には次の情報が含まれます。

- ユーザー ID と `ExampleUser`
- `admin` ロール
- `admin:read` などの名前付き権限
- `GET:/auth/**` などのリクエスト権限
- `authenticationType` 追加属性

トークンを指定しない場合、または無効なトークンを指定した場合、API は HTTP 401 を返します。

## 4. 管理者 API の認可を確認する

管理者トークンでリクエストします。

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-admin-token'
```

この API では、次の条件をすべて確認します。

1. ユーザーが認証済みである
2. `admin` ロールを持つ
3. `admin:read` の名前付き権限を持つ
4. 現在のリクエストに一致する `GET:/auth/**` 権限を持つ

同じ API を一般ユーザーのトークンで呼び出します。

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-user-token'
```

この場合は HTTP 403 が返り、エラーコードは `FORBIDDEN` になります。認証には成功していますが、管理者用のロールまたは権限を満たしていないためです。

## サンプルコード内の役割

| クラス | 役割 |
| --- | --- |
| `ExampleAuthController` | ログイン、匿名アクセス、現在の認証情報、管理者 API を提供します。 |
| `ExampleAuthenticationService` | メモリ上のアカウントを保持し、パスワードとトークンを検証します。 |
| `ExampleAuthInterceptor` | トークンサービスを SmartMVC の認証ライフサイクルへ接続します。 |
| `ExampleUser` | `CurrentAuth` から型を指定してユーザーを取得する方法を示します。 |

統合テスト `ExampleAuthenticationIntegrationTest` は、匿名アクセス、ログイン、未認証、権限不足、管理者アクセス成功の各経路を検証します。独自実装を始めるときは、このサンプルを小さな動作確認用の基準として利用できます。
