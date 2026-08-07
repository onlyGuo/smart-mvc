---
title: 自定义认证与当前用户
description: 实现 AuthInterceptor，构建 AuthPrincipal，并在业务代码中安全使用 CurrentAuth。
next:
  text: 登录鉴权示例
  link: /examples/login.html
---

# 自定义认证与当前用户

这一节实现一个基于 Bearer Token 的认证过程，并说明角色、请求权限和 `CurrentAuth` 的生命周期。

## 1. 定义应用用户

```java
public record AppUser(
        String id,
        String username,
        String displayName
) {
}
```

SmartMVC 不要求用户实现特定接口。用户类型可以是应用已有的实体或 DTO。

## 2. 实现 `AuthInterceptor`

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

返回 `null` 表示认证失败，SmartMVC 会抛出 `UnauthorizedException` 并返回 HTTP 401。

应用注册该 Bean 后，默认的 `PermitAllAuthInterceptor` 会自动退让。

## 3. 配置凭据位置

```yaml
spring:
  smart:
    mvc:
      auth:
        authorization-header: Authorization
        token-prefix: Bearer
```

对于下面的请求：

```text
Authorization: Bearer eyJhbGciOi...
```

`authenticate` 收到的是去掉前缀后的 Token。

如果凭据来自 Cookie、Session 或其他位置，可以覆写：

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

## `AuthPrincipal<T>`

认证成功后返回的身份包含：

| 内容 | 用途 |
| --- | --- |
| `id` | 稳定的用户标识，不能为空 |
| `user` | 应用自己的用户对象，可以为空 |
| `roles` | 角色集合 |
| `permissions` | 命名权限和请求权限集合 |
| `attributes` | 租户、认证方式等扩展属性 |

集合和 Map 会被防御性复制并变为只读，避免认证完成后身份被意外修改。

## 在业务代码中使用 `CurrentAuth`

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

常用方法：

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

`requirePrincipal()` 在没有身份时抛出 `IllegalStateException`，适合只会在受保护请求中执行的代码。

## 并发请求是否安全

`CurrentAuth` 是一个 Spring 单例，但它不在实例字段中保存用户。所有查询都委托给基于 `ThreadLocal` 的 `AuthContext`：

- 同步 Servlet 请求之间相互隔离；
- 请求开始前会先清理旧上下文；
- 请求结束后再次清理，避免线程池复用导致身份残留。

它不会自动传播到 `@Async`、自建线程、线程池任务或响应式链路。跨线程执行时，应显式传递需要的用户 ID、角色或不可变身份快照。

### Spring MVC 异步处理

`Callable`、`DeferredResult`、`WebAsyncTask` 和 `StreamingResponseBody` 同样会跨越 Servlet 请求线程。当前认证拦截器只在同步完成回调 `afterCompletion` 中清理身份，没有在 `afterConcurrentHandlingStarted` 中处理异步切换。因此，前面的自动清理保证只适用于同步 Servlet 请求：异步任务不会自动获得 `CurrentAuth`，初始请求线程切换到异步处理时也不会在该时点自动清理上下文。

使用 Spring MVC 异步返回类型时，请在离开同步请求线程前复制最少且不可变的身份数据，并由应用安排异步生命周期中的清理；或者提供能够处理异步切换的自定义拦截器。不要在异步回调中直接依赖 `CurrentAuth`。

## 请求权限表达式

开启请求级权限校验：

```yaml
spring.smart.mvc.auth.check-request-permission: true
```

权限格式为 `METHOD:/path/pattern`：

| 表达式 | 匹配范围 |
| --- | --- |
| `GET:/api/users/**` | GET 请求及该路径下所有层级 |
| `POST:/api/users/*` | POST 请求及单层子路径 |
| `*:/api/public/**` | 任意 HTTP 方法 |
| `*:*` | 任意方法和路径 |

路径支持：

- `**`：跨多个路径段；
- `*`：单个路径段内的任意字符；
- `?`：单个字符。

HTTP 方法匹配不区分大小写。

## 自定义授权过程

需要从外部权限服务实时判断，或使用完全不同的授权模型时，可以覆写 `authorize`：

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

还可以按需覆写 `bind`、`clear` 和 `resolveRequestPath`。通常只覆写真正需要改变的部分，其他生命周期逻辑继续使用默认实现。

## 推荐配置

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

完成自定义接入后，可以参考下一节的无数据库示例验证完整流程。
