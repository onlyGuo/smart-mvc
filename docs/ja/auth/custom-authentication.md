---
title: 独自認証と現在のユーザー
description: AuthInterceptor を実装し、AuthPrincipal を構築して、CurrentAuth を安全に利用する方法を説明します。
prev:
  text: 認証・認可の概要
  link: /ja/auth/overview.html
next:
  text: ログイン・認可のサンプル
  link: /ja/examples/login.html
---

# 独自認証と現在のユーザー

この章では、Bearer トークンを使う認証処理を実装します。あわせて、ロール、リクエスト権限、`CurrentAuth` のライフサイクルを詳しく確認します。

## 1. アプリケーションのユーザー型を定義する

```java
public record AppUser(
        String id,
        String username,
        String displayName
) {
}
```

SmartMVC は、ユーザー型に特定のインターフェースを実装することを求めません。アプリケーションにすでにあるエンティティや DTO を、そのまま使えます。

## 2. `AuthInterceptor` を実装する

```java
@Component
public class TokenAuthInterceptor implements AuthInterceptor<AppUser> {

    private final TokenService tokenService;

    public TokenAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public AuthPrincipal<AppUser> authenticate(
            String token,
            HttpServletRequest request) {
        AppUser user = tokenService.findUser(token);
        if (user == null) {
            return null;
        }

        return new AuthPrincipal<>(
                user.id(),
                user,
                Set.of("admin"),
                Set.of("user:read", "GET:/api/users/**"),
                Map.of("tenantId", "tenant-001")
        );
    }
}
```

`authenticate` が `null` を返すと認証失敗です。SmartMVC は `UnauthorizedException` を送出し、HTTP 401 を返します。

この実装を Spring Bean として登録すると、デフォルトの `PermitAllAuthInterceptor` は自動的に登録を控え、アプリケーションの実装が使われます。

## 3. 資格情報の場所を設定する

```yaml
spring:
  smart:
    mvc:
      auth:
        authorization-header: Authorization
        token-prefix: Bearer
```

次のリクエストを受け取った場合、`authenticate` には接頭辞を取り除いたトークンが渡されます。

```text
Authorization: Bearer eyJhbGciOi...
```

Cookie、Session、独自ヘッダーなどから資格情報を読みたい場合は、`resolveToken` をオーバーライドできます。

```java
@Override
public String resolveToken(
        HttpServletRequest request,
        AuthConfig config) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
        return null;
    }
    return Arrays.stream(cookies)
            .filter(cookie -> "session_token".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
}
```

## `AuthPrincipal<T>` の内容

認証に成功したら、現在の利用者を表す `AuthPrincipal<T>` を返します。

| 内容 | 用途 |
| --- | --- |
| `id` | 変化しないユーザー識別子。`null` または空白にはできません。 |
| `user` | アプリケーション独自のユーザーオブジェクト。`null` も許可されます。 |
| `roles` | ロールの集合 |
| `permissions` | 名前付き権限とリクエスト権限の集合 |
| `attributes` | テナントや認証方式などの追加属性 |

コンストラクターは集合と Map を防御的にコピーし、読み取り専用にします。認証が完了した後に、別の処理から認証情報を誤って変更することを防ぎます。

## 業務コードから `CurrentAuth` を使う

`CurrentAuth` は Spring 管理の Bean なので、Service や Controller へ通常どおりコンストラクターインジェクションできます。

```java
@Service
public class ProfileService {

    private final CurrentAuth currentAuth;

    public ProfileService(CurrentAuth currentAuth) {
        this.currentAuth = currentAuth;
    }

    public ProfileView currentProfile() {
        AppUser user = currentAuth.getUser(AppUser.class);
        return ProfileView.from(user);
    }
}
```

よく使うメソッドは次のとおりです。

```java
currentAuth.isAuthenticated();
currentAuth.getPrincipal();
currentAuth.requirePrincipal();
currentAuth.getUserId();
currentAuth.getUser(AppUser.class);
currentAuth.getRoles();
currentAuth.getPermissions();
currentAuth.hasRole("admin");
currentAuth.hasPermission("GET", "/api/users/1001");
```

`requirePrincipal()` は、認証情報がない場合に `IllegalStateException` を送出します。`@Auth` で保護された処理からだけ呼び出されるコードなど、認証済みであることが前提の場所に適しています。

## 同時リクエストでも安全か

`CurrentAuth` 自体は Spring のシングルトンですが、ユーザー情報をインスタンスフィールドには保存しません。すべての参照は、`ThreadLocal` を利用する `AuthContext` へ委譲されます。

- 同期 Servlet リクエストごとに認証情報が分離される
- リクエスト処理の前に、古いコンテキストをいったんクリアする
- リクエスト終了後にもクリアし、スレッドプールで再利用されたスレッドに情報が残ることを防ぐ

そのため、通常の同期 Servlet リクエストでは、複数の利用者が同時にアクセスしても認証情報が混ざりません。

ただし、`ThreadLocal` の内容は `@Async`、独自スレッド、スレッドプールのタスク、リアクティブチェーンへ自動では伝播しません。別スレッドで処理する場合は、必要なユーザー ID、ロール、または不変の認証情報スナップショットを明示的に渡してください。

### Spring MVC の非同期処理

`Callable`、`DeferredResult`、`WebAsyncTask`、`StreamingResponseBody` も Servlet のリクエストスレッドをまたぎます。現在の認証インターセプターは、同期処理の `afterCompletion` で認証情報を消去しますが、`afterConcurrentHandlingStarted` で非同期処理への切り替えを扱っていません。そのため、前述の自動消去の保証は同期 Servlet リクエストだけに適用されます。非同期タスクには `CurrentAuth` が引き継がれず、非同期処理を開始した時点では元のリクエストスレッドのコンテキストも自動消去されません。

Spring MVC の非同期戻り値を使う場合は、同期リクエストスレッドを離れる前に必要最小限の不変な認証情報だけをコピーし、非同期ライフサイクルに応じた消去をアプリケーション側で行ってください。別の方法として、非同期切り替えを明示的に処理する独自インターセプターを提供できます。非同期コールバック内で `CurrentAuth` に直接依存しないでください。

## リクエスト権限の表現

HTTP メソッドとパスに基づく権限チェックを有効にします。

```yaml
spring.smart.mvc.auth.check-request-permission: true
```

権限は `METHOD:/path/pattern` の形式で表します。

| 式 | 一致する範囲 |
| --- | --- |
| `GET:/api/users/**` | GET リクエストと、そのパス以下のすべての階層 |
| `POST:/api/users/*` | POST リクエストと、直下の 1 階層 |
| `*:/api/public/**` | 任意の HTTP メソッド |
| `*:*` | 任意のメソッドとパス |

パスでは次のワイルドカードを利用できます。

- `**`：複数のパス区切りをまたいで一致
- `*`：1 つのパス区切りの中で任意の文字列に一致
- `?`：任意の 1 文字に一致

HTTP メソッドの比較では、大文字と小文字を区別しません。

## 認可処理を独自に実装する

外部の権限サービスへ毎回問い合わせる場合や、異なる認可モデルを使う場合は、`authorize` をオーバーライドできます。

```java
@Override
public boolean authorize(
        AuthPrincipal<AppUser> principal,
        Auth requirement,
        HttpServletRequest request,
        AuthConfig config) {
    return permissionService.isAllowed(
            principal.getId(),
            request.getMethod(),
            request.getRequestURI()
    );
}
```

必要に応じて、`bind`、`clear`、`resolveRequestPath` もオーバーライドできます。通常は、実際に変更したい処理だけを上書きし、その他のライフサイクル処理にはデフォルト実装を使う方が安全です。

## 推奨設定の例

```yaml
spring:
  smart:
    mvc:
      auth:
        enabled: true
        mode: GLOBAL
        authorization-header: Authorization
        token-prefix: Bearer
        check-request-permission: true
        exclude-paths:
          - /actuator/health
```

独自認証を組み込めたら、次の章にあるデータベース不要のサンプルで、一連の処理とレスポンスを確認できます。
