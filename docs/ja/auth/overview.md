---
title: 認証・認可の概要
description: 認証モード、@Auth、@Anonymous、ロール、権限の基本ルールを説明します。
prev:
  text: リクエストログ
  link: /ja/features/request-logging.html
next:
  text: 独自認証と現在のユーザー
  link: /ja/auth/custom-authentication.html
---

# 認証・認可の概要

SmartMVC は、認証・認可をアプリケーションへ組み込むための入口と、リクエスト中のライフサイクルを定義します。一方で、ユーザーの保存場所やトークンの形式、発行方法は規定しません。

アプリケーションが担当することは次のとおりです。

- トークン、Cookie、Session などの資格情報を検証する
- ユーザー、ロール、権限を取得する
- アカウントが利用可能かを判断する
- 独自の `AuthInterceptor` を実装する

SmartMVC は次の処理を担当します。

- 現在の API に認証が必要かを判断する
- リクエストから資格情報を取り出す
- アプリケーションの認証実装を呼び出す
- アノテーション要件とリクエスト権限を検証する
- 認証情報を `CurrentAuth` にバインドする
- リクエスト終了時に認証情報をクリアする

この境界により、アプリケーションは既存のユーザーストアやトークン方式を保ったまま、SmartMVC の共通ライフサイクルを利用できます。

## 2 つの認証モード

### `ANNOTATED`

```yaml
spring.smart.mvc.auth.mode: ANNOTATED
```

`@Auth` が付いた Controller またはメソッドだけを認証します。認証アノテーションがない API は公開されたままです。

次のようなプロジェクトに向いています。

- 既存プロジェクトへ段階的に認証を追加する
- 公開 API が多い
- 保護された API をソースコード上で明示したい

### `GLOBAL`

```yaml
spring.smart.mvc.auth.mode: GLOBAL
```

すべての Controller メソッドをデフォルトで認証し、`@Anonymous` が付いたものと `exclude-paths` に一致するものだけを除外します。

次のようなプロジェクトに向いています。

- 管理画面用のバックエンド
- ほとんどの API にログインが必要
- デフォルトで保護する方針を採用したい

新規の管理系 API では `GLOBAL`、公開 API を含む既存システムへ少しずつ導入する場合は `ANNOTATED` が分かりやすい選択です。

## `@Auth`

ログインだけを必須にする場合は、引数なしで付けます。

```java
@Auth
@GetMapping("/profile")
public ProfileView profile() {
    // ...
}
```

ロールと名前付き権限も必要な場合は、それぞれを宣言します。

```java
@Auth(
    roles = "admin",
    permissions = "user:read"
)
@GetMapping("/users")
public List<UserView> users() {
    // ...
}
```

`@Auth` は Controller クラス、メソッド、または合成アノテーションに付けられます。

## `@Anonymous`

ログイン API など、認証を行わずに呼び出す必要があるメソッドには `@Anonymous` を付けます。

```java
@Anonymous
@PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {
    // ...
}
```

匿名 API では、認証、認可、認証情報のバインドをすべてスキップします。したがって、デフォルトの振る舞いは次のとおりです。

- `currentAuth.isAuthenticated()` は `false` を返す
- `currentAuth.getUser()` は `null` を返す
- ロールと権限の集合は空になる

メソッドに付けた `@Auth` または `@Anonymous` は、クラスに付けた宣言より優先されます。Controller 全体のルールに対して、特定のメソッドだけ例外を設けることができます。

## 除外パス

```yaml
spring:
  smart:
    mvc:
      auth:
        exclude-paths:
          - /actuator/health
          - /assets/**
```

除外パスは `GLOBAL` と `ANNOTATED` の両モードで有効です。一致したパスは SmartMVC の認証インターセプターに入りません。Handler に `@Auth` が付いている場合も同じです。

ヘルスチェックや静的リソースなど、アプリケーションの認証処理そのものを通す必要がないパスに使用してください。

## 複数のロールと権限

デフォルトの `AuthMode.ALL` では、宣言されたすべての値が必要です。

```java
@Auth(
    roles = { "admin", "operator" },
    permissions = { "user:read", "user:update" }
)
```

`AuthMode.ANY` では、空でないグループごとに少なくとも 1 つ一致すれば要件を満たします。

```java
@Auth(
    roles = { "admin", "auditor" },
    permissions = { "report:read", "report:export" },
    mode = AuthMode.ANY
)
```

この例では、「ロールのどちらか 1 つ以上」と「権限のどちらか 1 つ以上」の両方が必要です。ロールのグループと権限のグループの間は、常に AND で評価されます。

## デフォルトの認証実装

アプリケーションが独自の `AuthInterceptor` を提供しない場合、Starter は `PermitAllAuthInterceptor` を登録します。この実装は、ワイルドカードのロール `*` と権限 `*:*` を持つ認証情報を作成します。そのため、空のプロジェクトや機能確認用のアプリケーションは、認証サービスを用意しなくても起動できます。

これは導入を妨げないためのデフォルト実装であり、本番用のユーザー確認を代替するものではありません。実際の認証が必要なアプリケーションでは、必ず独自実装を登録してください。次の章で、その手順を具体的に説明します。
